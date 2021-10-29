@file:JvmName("Main")

package io.github.pikokr.bucket

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import dev.kord.gateway.PrivilegedIntent
import io.github.pikokr.bucket.config.BucketServerConfig
import java.io.File

@PrivilegedIntent
suspend fun main() {
    val yaml = Yaml(
        configuration = YamlConfiguration(
            strictMode = false,
            sequenceStyle = SequenceStyle.Block
        )
    )

    val configFile = File("config.yml")
    if (!configFile.exists()) {
        val config = BucketServerConfig()
        val content = yaml.encodeToString(BucketServerConfig.serializer(), config)
        configFile.writeText(content)
        error("Server configuration file created. Please edit configuration and re-run.")
    }
    val serializer = BucketServerConfig.serializer()
    val config = yaml.decodeFromStream(serializer, configFile.inputStream()).also { config ->
        if (config.bot.token.isBlank()) {
            error("Token is empty. Please edit configuration and re-run.")
        }

        val content = yaml.encodeToString(serializer, config)
        configFile.writeText(content)
    }

    BucketImpl.init(config)
}