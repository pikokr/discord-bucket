package io.github.pikokr.bucket.plugin.loader

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.github.pikokr.bucket.exception.InvalidPluginException
import io.github.pikokr.bucket.plugin.BucketPlugin
import io.github.pikokr.bucket.plugin.BucketPluginDescription
import io.github.pikokr.bucket.plugin.BucketPluginManager
import io.github.pikokr.bucket.plugin.BucketPluginManager.Companion.isEnabled
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.jar.JarFile

internal class BucketPluginLoader(private val pluginManager: BucketPluginManager) {
    private val loaders = CopyOnWriteArrayList<BucketClassLoader>()
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

        val loader = BucketClassLoader(this, this::class.java.classLoader, description, file)
        loaders.add(loader)
        return loader.plugin
    }

    fun unloadPlugin(plugin: BucketPlugin) {
        if (plugin.isEnabled) {
            plugin.isEnabled = false

            val loader = plugin::class.java.classLoader as BucketClassLoader
            loaders.remove(loader)
            loader.close()
        }
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