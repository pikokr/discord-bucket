package io.github.pikokr.bucket.plugin

import java.io.File

interface BucketPluginManager {
    fun loadPlugins(directory: File) : List<BucketPlugin>

    fun enablePlugins()

    fun disablePlugins()

    fun unloadPlugins()
}