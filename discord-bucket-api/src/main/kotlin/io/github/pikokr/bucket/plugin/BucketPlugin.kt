package io.github.pikokr.bucket.plugin

abstract class BucketPlugin {
    open fun onEnable() {}

    open fun onDisable() {}
}