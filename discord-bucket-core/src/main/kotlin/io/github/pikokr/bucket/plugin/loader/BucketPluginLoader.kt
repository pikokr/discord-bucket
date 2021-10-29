package io.github.pikokr.bucket.plugin.loader

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.github.pikokr.bucket.exception.InvalidPluginException
import io.github.pikokr.bucket.plugin.BucketPlugin
import io.github.pikokr.bucket.plugin.BucketPluginDescription
import io.github.pikokr.bucket.plugin.BucketPluginManagerImpl.Extension.isEnabled
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.JarFile

object BucketPluginLoader {
    private val loaders = CopyOnWriteArrayList<BucketClassLoader>()
    private val loaderCache = hashMapOf<BucketPlugin, BucketClassLoader>()
    private val yaml by lazy {
        Yaml(configuration = YamlConfiguration(
            strictMode = false,
            sequenceStyle = SequenceStyle.Block
        ))
    }

    fun loadPlugin(file: File): BucketPlugin {
        if (!file.exists()) {
            throw InvalidPluginException(FileNotFoundException("Provided plugin in '${file.path}' does not exist."))
        }

        val description = file.pluginDescription

        val loader = BucketClassLoader(this::class.java.classLoader, description, file)
        loaders.add(loader)
        loaderCache[loader.plugin] = loader
        return loader.plugin
    }

    fun unloadPlugin(plugin: BucketPlugin) {
        plugin.isEnabled = false

        val loader = plugin.loader
        loaders.remove(loader)
        loaderCache.remove(plugin)
        loader.close()
    }

    val BucketPlugin.loader: BucketClassLoader
        get() = loaderCache[this] ?: (this::class.java.classLoader as BucketClassLoader).also { loader ->
            loaderCache[this] = loader
        }

    private val File.pluginDescription: BucketPluginDescription
        get() {
            return runCatching {
                JarFile(this).use { jarFile ->
                    val entry = jarFile.getJarEntry("plugin.yml")

                    if (entry != null) {
                        jarFile.getInputStream(entry).use { stream ->
                            yaml.decodeFromStream(BucketPluginDescription.serializer(), stream)
                        }
                    } else {
                        throw RuntimeException()
                    }
                }
            }.getOrElse { throwable ->
                throw InvalidPluginException("Invalid 'plugin.yml'", throwable)
            }
        }
}