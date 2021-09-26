package io.github.pikokr.bucket

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
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
import java.io.File
import kotlin.system.exitProcess

class BucketImpl : Bucket() {
    override val pluginManager: BucketPluginManager
        get() = internalPluginManager

    internal val logger: Logger
        get() = internalLogger

    companion object {
        @PrivilegedIntent
        @JvmStatic
        suspend fun main(args: Array<String>) {
            require(!::bucket.isInitialized) { "Already initialized" }

            bucket = BucketImpl()
            setImplementation(bucket)

            kord = Kord(config.bot.token) {
                var intents = Intents(Intent.values)

                if (!config.bot.intents.presence) {
                    intents -= Intent.GuildPresences
                }
                if (!config.bot.intents.guildMembers) {
                    intents -= Intent.GuildMembers
                }

                this.intents = intents
            }

            kord.on<ReadyEvent> {
                val self = kord.getSelf()
                internalLogger.info("Bot logged in as ${self.tag} (${self.id})")
            }

            runCatching {
                kord.login()
            }.onFailure { throwable ->
                throw RuntimeException("Failed to initialize kord. Is your token valid?", throwable)
            }
        }

        private lateinit var bucket: BucketImpl

        private lateinit var kord: Kord

        private val internalLogger = LoggerFactory.getLogger(this::class.java)

        private val internalPluginManager = BucketPluginManagerImpl(bucket)

        private val yaml by lazy {
            Yaml(
                configuration = YamlConfiguration(
                    strictMode = false,
                    sequenceStyle = SequenceStyle.Block
                )
            )
        }

        private val config: BucketServerConfig by lazy {
            val configFile = File("config.yml")
            if (!configFile.exists()) {
                val config = BucketServerConfig()
                val content = yaml.encodeToString(BucketServerConfig.serializer(), config)
                configFile.writeText(content)
                internalLogger.warn("Server configuration file created. Please edit configuration and re-run.")
                exitProcess(1)
            }
            val data = yaml.decodeFromStream(BucketServerConfig.serializer(), configFile.inputStream())
            val content = yaml.encodeToString(BucketServerConfig.serializer(), data)
            configFile.writeText(content)
            data
        }
    }
}