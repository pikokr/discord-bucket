@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.github.pikokr.bucket.plugin

import io.github.pikokr.bucket.Bucket

abstract class BucketPlugin {
    open fun onEnable() {}

    open fun onDisable() {}

    val pluginDescription: BucketPluginDescription
        get() = Bucket.pluginManager.getPluginDescription(this)

    val name: String
        get() = pluginDescription.name

    val version: String
        get() = pluginDescription.version

    val description: String
        get() = pluginDescription.description
}