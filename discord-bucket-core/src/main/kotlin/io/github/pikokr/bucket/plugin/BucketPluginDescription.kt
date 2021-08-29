package io.github.pikokr.bucket.plugin

import kotlinx.serialization.Serializable

@Serializable
data class BucketPluginDescription(
    val name: String,
    val version: String,
    val main: String,
    val description: String = "",
    val website: String = "",
    val author: String = "",
    val authors: List<String> = emptyList()
)