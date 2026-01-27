@file:UseSerializers(UUIDSerializer::class)

package com.degoos.kayle.profile

import com.degoos.kayle.codec.UUIDSerializer
import com.hypixel.hytale.server.core.auth.AuthConfig
import com.hypixel.hytale.server.core.auth.ServerAuthManager
import com.hypixel.hytale.server.core.cosmetics.PlayerSkin
import kotlinx.coroutines.future.await
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

/**
 * Represents a player's profile containing their UUID, username, and skin information.
 *
 * This class deserializes the player profile data and provides functionality to parse
 * and manage skin data through related properties and services.
 *
 * @property uuid The unique identifier of the player.
 * @property username The player's username.
 * @property rawSkin The raw JSON string containing the player's skin data.
 *
 * @constructor Creates a player profile with the provided UUID, username, and raw skin data.
 * Initializes a transient `AvatarService` instance for skin-related operations.
 *
 * @see AvatarService
 * @see PlayerSkin
 */
@Serializable
data class PlayerProfile(val uuid: UUID, val username: String, @SerialName("skin") val rawSkin: String) {

    @Transient
    val avatarService = AvatarService(rawSkin)

    val skin by lazy {
        try {
            val bson = BsonDocument.parse(rawSkin)
            PlayerSkin(bson)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    companion object {

        private val JSON_ENCODER = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        /**
         * Fetches the `PlayerProfile` associated with the specified username by querying Hytale's API.
         * If an HTTP client is not provided, a default client will be created and used for the request.
         *
         * @param userName The username of the player whose profile is to be fetched. This must be a valid string.
         * @param client An optional `HttpClient` to be used for the HTTP request. If `null`, a new default client will be used.
         * @return The `PlayerProfile` of the user, or `null` if the profile could not be retrieved.
         */
        suspend fun fetch(userName: String, client: HttpClient? = null) =
            process("https://account-data.hytale.com/profile/username/$userName", client)

        /**
         * Fetches the `PlayerProfile` associated with the specified UUID by querying Hytale's API.
         * If an HTTP client is not provided, a default client will be created and used for the request.
         *
         * @param uuid The UUID of the player whose profile is to be fetched.
         * @param client An optional `HttpClient` to be used for the HTTP request. If `null`, a new default client will be used.
         * @return The `PlayerProfile` of the user, or `null` if the profile could not be retrieved.
         */
        suspend fun fetch(uuid: UUID, client: HttpClient? = null) =
            process("https://account-data.hytale.com/profile/uuid/$uuid", client)

        private suspend fun process(url: String, client: HttpClient? = null): PlayerProfile? {
            val c = client ?: HttpClient.newHttpClient()
            val request =
                HttpRequest.newBuilder(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer ${ServerAuthManager.getInstance().sessionToken}")
                    .header("User-Agent", AuthConfig.USER_AGENT)
                    .timeout(Duration.ofSeconds(5L)).GET()
                    .build()
            val response = c.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
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