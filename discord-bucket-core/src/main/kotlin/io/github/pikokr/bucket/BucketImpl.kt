package io.github.pikokr.bucket

import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.pikokr.bucket.config.BucketServerConfig
import io.github.pikokr.bucket.plugin.BucketPluginManager
import io.github.pikokr.bucket.plugin.BucketPluginManagerImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BucketImpl : Bucket() {
    override val pluginManager: BucketPluginManager
        get() = managerImpl

    internal val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        @PrivilegedIntent
        suspend fun init(config: BucketServerConfig) {
            require(!::bucket.isInitialized) { "Already initialized" }

            bucket = BucketImpl()
            setImplementation(bucket)

            managerImpl = BucketPluginManagerImpl(bucket)
            kord = Kord(config.bot.token)

            kord.on<ReadyEvent> {
                val self = kord.getSelf()
                bucket.logger.info("Bot logged in as ${self.tag} (${self.id})")
            }

            runCatching {
                kord.login {
                    var intents = Intents(Intent.values)

                    if (!config.bot.intents.presence) {
                        intents -= Intent.GuildPresences
                    }
                    if (!config.bot.intents.guildMembers) {
                        intents -= Intent.GuildMembers
                    }

                    this.intents = intents
                }
            }.onFailure { throwable ->
                throw RuntimeException("Failed to initialize kord. Is your token valid?", throwable)
            }
        }

        private lateinit var bucket: BucketImpl

        private lateinit var kord: Kord

        private lateinit var managerImpl: BucketPluginManagerImpl
    }
}