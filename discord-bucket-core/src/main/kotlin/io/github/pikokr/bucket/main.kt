package io.github.pikokr.bucket

import io.github.pikokr.bucket.plugin.BucketPluginManager
import java.io.File

fun main() {
    val pluginManager = BucketPluginManager()
    val pluginFolder = File("plugins").apply {
        if (!exists()) {
            mkdirs()
        }
    }

    pluginManager.loadPlugins(pluginFolder)

    println("Start enabling plugins")
    pluginManager.enablePlugins()
    println("Enabled plugins")
    println("Start disabling plugins")
    pluginManager.disablePlugins()
    println("Disabled plugins")
}