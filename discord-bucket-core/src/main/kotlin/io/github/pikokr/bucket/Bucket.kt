package io.github.pikokr.bucket

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.kord.core.Kord

import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intents
import io.github.pikokr.bucket.config.BucketServerConfig
import io.github.pikokr.bucket.plugin.BucketPluginManagerImpl
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

object Bucket {
    val config: BucketServerConfig by lazy {
        val configFile = File("config.yml")
        if (!configFile.exists()) {
            val config = BucketServerConfig()
            val content = yaml.encodeToString(BucketServerConfig.serializer(), config)
            configFile.writeText(content)
            println("Server configuration file created. Please edit configuration and re-run.")
            exitProcess(1)
        }
        val data = yaml.decodeFromStream(BucketServerConfig.serializer(), configFile.inputStream())
        val content = yaml.encodeToString(BucketServerConfig.serializer(), data)
        configFile.writeText(content)
        data
    }

    internal val pluginManager = BucketPluginManagerImpl()

    val kord by lazy {
        runBlocking {
            Kord(config.bot.token) {
                intents = Intents.nonPrivileged
            }
        }
    }

    private val yaml by lazy {
        Yaml(configuration = YamlConfiguration(
            strictMode = false,
            sequenceStyle = SequenceStyle.Block
        ))
    }

    private var initialized = false

    internal suspend fun run() {
        if (initialized) {
            error("Already initialized")
        }
        initialized = true
        kord.on<ReadyEvent> {
            val self = kord.getSelf()
            println("Bot logged in as ${self.tag}(${self.id})")
        }
        runCatching {
            kord.login()
        }.onFailure {
            error("Failed to initialize kord. Is your token valid?")
        }
    }
}