package io.github.pikokr.bucket.plugin

import io.github.pikokr.bucket.plugin.loader.BucketPluginLoader
import java.io.File

internal class BucketPluginManagerImpl : BucketPluginManager {
    companion object Extension {
        private lateinit var manager: BucketPluginManagerImpl

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

    private val loader = BucketPluginLoader()
    private val _plugins = ArrayList<BucketPlugin>()
    private val enabled = HashMap<BucketPlugin, Boolean>()

    override val plugins: Array<BucketPlugin>
        get() = _plugins.toTypedArray()

    override fun loadPlugins(directory: File): Array<BucketPlugin> {
        val updateDirectory = File(directory, "update")
        val checkUpdates = updateDirectory.exists() && updateDirectory.isDirectory
        return (directory.listFiles { file ->
            file.isFile && file.extension == "jar"
        } ?: emptyArray()).mapNotNull { file ->
            runCatching {
                if (checkUpdates) {
                    val updateCandidate = File(updateDirectory, file.name)
                    if (updateCandidate.exists() && updateCandidate.isFile) {
                        updateCandidate.copyTo(file, true)
                        updateCandidate.delete()
                    }
                }
                loader.loadPlugin(file)
            }.getOrElse { throwable ->
                throwable.printStackTrace()
                null
            }?.also { plugin ->
                _plugins.add(plugin)
            }
        }.also { list ->
            println("Loaded ${list.size} plugins")
            println()
        }.toTypedArray()
    }

    override fun enablePlugins() {
        _plugins.forEach { plugin ->
            plugin.isEnabled = true
        }
    }

    override fun disablePlugins() {
        _plugins.forEach { plugin ->
            plugin.isEnabled = false
        }
    }

    override fun unloadPlugins() {
        _plugins.forEach { plugin ->
            if (plugin.isEnabled) {
                loader.unloadPlugin(plugin)
            }
        }
        _plugins.clear()
    }
}