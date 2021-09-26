package io.github.pikokr.bucket.plugin

import java.io.File

interface BucketPluginManager {
    val plugins: Array<BucketPlugin>

    fun loadPlugin(file: File) : BucketPlugin?

    fun loadPlugins(directory: File) : Array<BucketPlugin>

    fun enablePlugins()

    fun disablePlugins()

    fun unloadPlugin(plugin: BucketPlugin): Boolean

    fun unloadPlugins()

    fun getPluginDescription(plugin: BucketPlugin): BucketPluginDescription
}