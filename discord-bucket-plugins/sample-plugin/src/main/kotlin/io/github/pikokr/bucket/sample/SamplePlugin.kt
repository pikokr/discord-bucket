package io.github.pikokr.bucket.sample

import io.github.pikokr.bucket.plugin.BucketPlugin

class SamplePlugin : BucketPlugin() {
    override fun onEnable() {
        println("와! 샌즈!")
    }

    override fun onDisable() {
        println("와! 파피루스!")
    }
}