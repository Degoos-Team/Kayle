package com.degoos.kayle.profile

import com.degoos.kayle.Kayle
import com.degoos.kayle.util.CaffeineService
import com.github.benmanes.caffeine.cache.Caffeine
import com.hypixel.hytale.server.core.auth.AuthConfig
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

class AvatarService(private val uuid: UUID) : CaffeineService<PortraitView, ByteArray>(Kayle.instance) {

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
        PortraitGenerator.generateAvatarPNG(uuid, key, client)
}

object PortraitGenerator {

    private const val BASE_URL = "https://crafthead.net/hytale"

    private fun endpoint(view: PortraitView) = when (view) {
        PortraitView.FACE, PortraitView.AVATAR -> "avatar"
        PortraitView.FRONT, PortraitView.BACK -> "bWht"
    }

    suspend fun generateAvatarPNG(
        uuid: UUID,
        view: PortraitView,
        client: HttpClient
    ): ByteArray? {
        return try {
            val url = "$BASE_URL/${endpoint(view)}/$uuid"
            val request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", AuthConfig.USER_AGENT)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()

            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).await()

            if (response.statusCode() == 200) {
                val body = response.body()
                if (body != null && body.isNotEmpty()) body else null
            } else {
                println("Failed to generate avatar. Status code: ${response.statusCode()}")
                println("URL: $url")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
