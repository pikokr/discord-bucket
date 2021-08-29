package io.github.pikokr.bucket.plugin

import io.github.pikokr.bucket.plugin.loader.BucketPluginLoader
import java.io.File

internal class BucketPluginManager {
    companion object {
        private lateinit var manager: BucketPluginManager

        internal var BucketPlugin.isEnabled: Boolean
            get() = manager.enabled[this] ?: false
            set(value) {
                if (manager.enabled[this] != value) {
                    manager.enabled[this] = value

                    if (value) {
                        onEnable()
                    } else {
                        onDisable()
                    }
                }
            }
    }

    init {
        manager = this
    }

    private val loader = BucketPluginLoader(this)
    private val plugins = ArrayList<BucketPlugin>()
    private val enabled = HashMap<BucketPlugin, Boolean>()

    internal fun loadPlugins(directory: File): List<BucketPlugin> {
        return (directory.listFiles { file ->
            file.isFile && file.extension == "jar"
        } ?: emptyArray()).mapNotNull { file ->
            runCatching {
                loader.loadPlugin(file)
            }.getOrElse { throwable ->
                throwable.printStackTrace()
                null
            }?.also { plugin ->
                plugins.add(plugin)
            }
        }
    }

    internal fun enablePlugins() {
        plugins.forEach { plugin ->
            plugin.isEnabled = true
        }
    }

    internal fun disablePlugins() {
        plugins.forEach { plugin ->
            plugin.isEnabled = false
        }
    }

    internal fun unloadPlugins() {
        plugins.forEach { plugin ->
            if (plugin.isEnabled) {
                loader.unloadPlugin(plugin)
            }
        }
    }
}