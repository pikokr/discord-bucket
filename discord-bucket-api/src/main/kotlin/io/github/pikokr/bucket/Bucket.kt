package io.github.pikokr.bucket

import io.github.pikokr.bucket.plugin.BucketPluginManager

abstract class Bucket {
    protected abstract val pluginManager: BucketPluginManager

    companion object {
        val pluginManager: BucketPluginManager
            get() = implementation.pluginManager

        private lateinit var implementation: Bucket

        @JvmStatic
        fun setImplementation(implementation: Bucket) {
            require(!this::implementation.isInitialized) { "Already initialized" }

            this.implementation = implementation
        }
    }
}