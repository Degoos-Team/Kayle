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

class AvatarService(private val rawSkin: String) : CaffeineService<PortraitView, ByteArray>(Kayle.instance) {

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
        PortraitGenerator.generateAvatarPNG(rawSkin, key, client)
}

object PortraitGenerator {

    const val API_URL = "https://hytlskins.com/skin/recipe/"

    suspend fun generateAvatarPNG(
        rawSkin: String,
        view: PortraitView,
        client: HttpClient
    ): ByteArray? {
        return try {
            val encodedSkin = Base64URL.encode(rawSkin.toByteArray())
            val url = "$API_URL${view.apiKey}?recipe=$encodedSkin"

            val request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", AuthConfig.USER_AGENT)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()

            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).await()

            if (response.statusCode() == 200) {
                val body = response.body()
                if (body != null && body.isNotEmpty()) {
                    return body
                }
            } else {
                println("Failed to generate avatar. Status code: ${response.statusCode()}")
                println("URL: $url")
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}