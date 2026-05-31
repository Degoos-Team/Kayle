package com.degoos.kayle.profile

import com.degoos.kayle.Kayle
import com.degoos.kayle.util.CaffeineService
import com.github.benmanes.caffeine.cache.Caffeine
import com.hypixel.hytale.server.core.auth.AuthConfig
import com.nimbusds.jose.util.Base64URL
import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*

enum class PortraitView(val apiKey: String) {
    FACE("face"),
    FRONT("front"),
    BACK("back"),
    AVATAR("avatar")
}

interface PortraitService {
    suspend fun generate(uuid: UUID, rawSkin: String, view: PortraitView, client: HttpClient): ByteArray?
}

/**
 * Renders portraits by sending the raw skin recipe to an external rendering service.
 * Compatible with any service that accepts a base64url-encoded recipe as a query parameter.
 * Originally designed for hytlskins.com: RecipePortraitService("https://hytlskins.com/skin/recipe/")
 */
class RecipePortraitService(val baseUrl: String) : PortraitService {

    override suspend fun generate(uuid: UUID, rawSkin: String, view: PortraitView, client: HttpClient): ByteArray? {
        return try {
            val encodedSkin = Base64URL.encode(rawSkin.toByteArray())
            val url = "$baseUrl${view.apiKey}?recipe=$encodedSkin"
            val request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", AuthConfig.USER_AGENT)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()
            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).await()
            if (response.statusCode() == 200) {
                response.body()?.takeIf { it.isNotEmpty() }
            } else {
                println("RecipePortraitService: status ${response.statusCode()} for $url")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * Renders portraits via crafthead.net using the player UUID directly.
 * No recipe encoding required. Portraits are cached 24h by Crafthead's CDN.
 */
class CraftheadPortraitService : PortraitService {

    private fun endpoint(view: PortraitView) = when (view) {
        PortraitView.FACE, PortraitView.AVATAR -> "avatar"
        PortraitView.FRONT, PortraitView.BACK -> "bust"
    }

    override suspend fun generate(uuid: UUID, rawSkin: String, view: PortraitView, client: HttpClient): ByteArray? {
        return try {
            val url = "https://crafthead.net/hytale/${endpoint(view)}/$uuid"
            val request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", AuthConfig.USER_AGENT)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()
            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).await()
            if (response.statusCode() == 200) {
                response.body()?.takeIf { it.isNotEmpty() }
            } else {
                println("CraftheadPortraitService: status ${response.statusCode()} for $url")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class AvatarService(private val uuid: UUID, private val rawSkin: String) : CaffeineService<PortraitView, ByteArray>(Kayle.instance) {

    companion object {
        private val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(5))
            .build()
    }

    override fun Caffeine<Any, Any>.configure() {
        maximumSize(4)
    }

    override suspend fun fetchFromSource(key: PortraitView) =
        PortraitGenerator.generate(uuid, rawSkin, key, client)
}

/**
 * Singleton that delegates portrait generation to the configured [PortraitService].
 * Defaults to [CraftheadPortraitService]. Override [service] at startup to switch implementations:
 *
 *   PortraitGenerator.service = RecipePortraitService("https://your-renderer.example.com/skin/recipe/")
 */
object PortraitGenerator {

    var service: PortraitService = CraftheadPortraitService()

    suspend fun generate(uuid: UUID, rawSkin: String, view: PortraitView, client: HttpClient): ByteArray? =
        service.generate(uuid, rawSkin, view, client)
}
