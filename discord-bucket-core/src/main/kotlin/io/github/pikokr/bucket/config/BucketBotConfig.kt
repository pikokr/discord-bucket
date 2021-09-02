package io.github.pikokr.bucket.config

import kotlinx.serialization.Serializable

@Serializable
data class BucketBotConfig(val token: String = "", val intents: BucketBotIntentsConfig = BucketBotIntentsConfig())

@Serializable
data class BucketBotIntentsConfig(val presence: Boolean = false, val guildMembers: Boolean = false)
