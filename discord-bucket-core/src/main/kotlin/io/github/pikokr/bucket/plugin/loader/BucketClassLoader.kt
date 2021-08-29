package io.github.pikokr.bucket.plugin.loader

import io.github.pikokr.bucket.exception.InvalidPluginException
import io.github.pikokr.bucket.plugin.BucketPlugin
import io.github.pikokr.bucket.plugin.BucketPluginDescription
import java.io.File
import java.net.URLClassLoader
import java.security.CodeSource
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

internal class BucketClassLoader(
    private val loader: BucketPluginLoader,
    parent: ClassLoader? = null,
    private val description: BucketPluginDescription,
    file: File
): URLClassLoader(arrayOf(file.toURI().toURL()), parent) {
    private val classes = ConcurrentHashMap<String, Class<*>>()
    private val jar = JarFile(file)
    private val url = file.toURI().toURL()

    internal val plugin = runCatching {
        runCatching {
            Class.forName(description.main, true, this)
        }.getOrElse { throwable ->
            throw InvalidPluginException("Main class '${description.main}' for plugin '${description.name}' does not exist.", throwable)
        }.asSubclass(BucketPlugin::class.java)
    }.getOrElse { throwable ->
        throw InvalidPluginException("Main class '${description.main}' for plugin '${description.name}' is not a subclass of BucketPlugin.", throwable)
    }.getConstructor().newInstance()

    internal val listClasses: Collection<Class<*>>
        get() = classes.values

    override fun findClass(name: String): Class<*> {
        return classes[name] ?: run {
            jar.getJarEntry("${name.replace('.', '/')}.class")?.let { entry ->
                val classBytes = runCatching {
                    jar.getInputStream(entry).use { input ->
                        input.readBytes()
                    }
                }.getOrElse { throwable ->
                    throw ClassNotFoundException(name, throwable)
                }
                val dot = name.lastIndexOf('.')
                if (dot != -1) {
                    val pkgName = name.substring(0, dot)
                    if (getDefinedPackage(pkgName) == null) {
                        runCatching {
                            jar.manifest?.let { manifest ->
                                definePackage(pkgName, manifest, url)
                            } ?: definePackage(pkgName, null, null, null, null, null, null, null)
                        }.onFailure {
                            checkNotNull(getDefinedPackage(pkgName)) { "Cannot find package $pkgName" }
                        }
                    }
                }
                val signers = entry.codeSigners
                val source = CodeSource(url, signers)
                defineClass(name, classBytes, 0, classBytes.size, source)
            } ?: super.findClass(name).also { clazz ->
                classes[name] = clazz
            }
        }
    }

    override fun close() {
        jar.use {
            super.close()
        }
    }

    companion object {
        init {
            ClassLoader.registerAsParallelCapable()
        }
    }
}