package io.github.pikokr.bucket.config

import kotlinx.serialization.Serializable

@Serializable
data class BucketServerConfig(
    val bot: BucketBotConfig = BucketBotConfig()
)
