package io.github.pikokr.bucket.plugin.loader

import io.github.pikokr.bucket.exception.InvalidPluginException
import io.github.pikokr.bucket.plugin.BucketPlugin
import io.github.pikokr.bucket.plugin.BucketPluginDescription
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

internal class BucketClassLoader(
    private val loader: BucketPluginLoader,
    parent: ClassLoader? = null,
    private val description: BucketPluginDescription,
    file: File
): URLClassLoader(arrayOf(file.toURI().toURL()), parent) {
    val jar = JarFile(file)
    val manifest = jar.manifest
    val url = file.toURI().toURL()

    internal val plugin = runCatching {
        runCatching {
            Class.forName(description.main, true, this)
        }.getOrElse { throwable ->
            throw InvalidPluginException("Main class '${description.main}' for plugin '${description.name}' does not exist.", throwable)
        }.asSubclass(BucketPlugin::class.java)
    }.getOrElse { throwable ->
        throw InvalidPluginException("Main class '${description.main}' for plugin '${description.name}' is not a subclass of BucketPlugin.", throwable)
    }.getConstructor().newInstance().apply {
        initialize()
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