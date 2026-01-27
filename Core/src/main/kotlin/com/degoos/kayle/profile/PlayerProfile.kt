@file:UseSerializers(UUIDSerializer::class)

package com.degoos.kayle.profile

import com.degoos.kayle.codec.UUIDSerializer
import com.hypixel.hytale.server.core.auth.AuthConfig
import com.hypixel.hytale.server.core.auth.ServerAuthManager
import com.hypixel.hytale.server.core.cosmetics.PlayerSkin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import org.bson.BsonDocument
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class PlayerProfile(val uuid: UUID, val username: String, @SerialName("skin") val rawSkin: String) {

    @Transient
    private val _portraitCache = ConcurrentHashMap<PortraitView, ByteArray>()

    val skin by lazy {
        try {
            val bson = BsonDocument.parse(rawSkin)
            PlayerSkin(bson)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun generateAvatarPNG(
        view: PortraitView,
        client: HttpClient? = null
    ) = _portraitCache.computeIfAbsent(view) {
        PortraitGenerator.generateAvatarPNG(rawSkin, view, client) ?: ByteArray(0)
    }.let { if (it.isEmpty()) null else it }

    companion object {

        private val JSON_ENCODER = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        fun fetch(userName: String, client: HttpClient? = null) =
            process("https://account-data.hytale.com/profile/username/$userName", client)

        fun fetch(uuid: UUID, client: HttpClient? = null) =
            process("https://account-data.hytale.com/profile/uuid/$uuid", client)


        private fun process(url: String, client: HttpClient? = null): PlayerProfile? {
            val c = client ?: HttpClient.newHttpClient()
            val request =
                HttpRequest.newBuilder(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer ${ServerAuthManager.getInstance().sessionToken}")
                    .header("User-Agent", AuthConfig.USER_AGENT)
                    .timeout(Duration.ofSeconds(5L)).GET()
                    .build()
            val response = c.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) return null
            val body = response.body() ?: return null

            try {
                return JSON_ENCODER.decodeFromString<PlayerProfile?>(body)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

    }

}