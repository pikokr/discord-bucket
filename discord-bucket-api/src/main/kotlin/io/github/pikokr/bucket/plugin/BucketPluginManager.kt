package io.github.pikokr.bucket.plugin

import java.io.File

interface BucketPluginManager {
    val plugins: Array<BucketPlugin>

    fun loadPlugins(directory: File) : Array<BucketPlugin>

    fun enablePlugins()

    fun disablePlugins()

    fun unloadPlugins()
}