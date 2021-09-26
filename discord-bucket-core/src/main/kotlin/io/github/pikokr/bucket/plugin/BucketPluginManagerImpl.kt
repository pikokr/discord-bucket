package io.github.pikokr.bucket.plugin

import io.github.pikokr.bucket.BucketImpl
import io.github.pikokr.bucket.plugin.loader.BucketClassLoader
import io.github.pikokr.bucket.plugin.loader.BucketPluginLoader
import io.github.pikokr.bucket.plugin.loader.BucketPluginLoader.loader
import org.slf4j.Logger
import java.io.File

internal class BucketPluginManagerImpl(private val bucket: BucketImpl) : BucketPluginManager {
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

    private val _plugins = ArrayList<BucketPlugin>()
    private val enabled = HashMap<BucketPlugin, Boolean>()

    override val plugins: Array<BucketPlugin>
        get() = _plugins.toTypedArray()

    override fun loadPlugin(file: File): BucketPlugin? {
        if (!file.isFile || file.extension != "jar") {
            bucket.logger.warn("Not a jar file.")
            return null
        }

        val updateDirectory = File(file.parent, "update")
        val checkUpdates = updateDirectory.exists() && updateDirectory.isDirectory
        return internalLoad(file, updateDirectory, checkUpdates)
    }

    override fun loadPlugins(directory: File): Array<BucketPlugin> {
        val updateDirectory = File(directory, "update")
        val checkUpdates = updateDirectory.exists() && updateDirectory.isDirectory
        return (directory.listFiles { file ->
            file.isFile && file.extension == "jar"
        } ?: emptyArray()).mapNotNull { file ->
            internalLoad(file, updateDirectory, checkUpdates)
        }.also { list ->
            bucket.logger.info("Loaded ${list.size} plugins")
        }.toTypedArray()
    }

    private fun internalLoad(file: File, updateDirectory: File, checkUpdates: Boolean): BucketPlugin? {
        return runCatching {
            if (checkUpdates) {
                val updateCandidate = File(updateDirectory, file.name)
                if (updateCandidate.exists() && updateCandidate.isFile) {
                    updateCandidate.copyTo(file, true)
                    updateCandidate.delete()
                }
            }
            BucketPluginLoader.loadPlugin(file)
        }.getOrElse { throwable ->
            throwable.printStackTrace()
            null
        }?.also { plugin ->
            _plugins.add(plugin)
        }
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

    override fun unloadPlugin(plugin: BucketPlugin): Boolean {
        if (plugin.isEnabled) {
            BucketPluginLoader.unloadPlugin(plugin)
            _plugins.remove(plugin)
            return true
        }

        bucket.logger.warn("$plugin not loaded. (Is it already unloaded?)")
        return false
    }

    override fun unloadPlugins() {
        _plugins.forEach { plugin ->
            if (plugin.isEnabled) {
                BucketPluginLoader.unloadPlugin(plugin)
            }
        }
        _plugins.clear()
    }

    override fun getPluginDescription(plugin: BucketPlugin): BucketPluginDescription {
        return plugin.loader.description
    }
}