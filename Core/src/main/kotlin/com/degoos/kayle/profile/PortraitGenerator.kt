package com.degoos.kayle.profile

import com.hypixel.hytale.server.core.auth.AuthConfig
import com.nimbusds.jose.util.Base64URL
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlin.jvm.optionals.getOrNull

enum class PortraitView(val apiKey: String) {
    FACE("face"),
    FRONT("front"),
    BACK("back"),
    AVATAR("avatar")
}

object PortraitGenerator {

    const val API_URL = "https://hytlskins.com/skin/recipe/"

    fun generateAvatarPNG(rawSkin: String, view: PortraitView, client: HttpClient? = null): ByteArray? {
        val c = client ?: HttpClient.newHttpClient()
        println(rawSkin)
        val url = "$API_URL${view.apiKey}?recipe=${Base64URL.encode(rawSkin.toByteArray())}"
        val request =
            HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", AuthConfig.USER_AGENT)
                .timeout(Duration.ofSeconds(5L)).GET()
                .build()
        val response = c.send(request, HttpResponse.BodyHandlers.ofByteArray())

        val body = when (response.statusCode()) {
            302 -> {
                val location = response.headers().firstValue("location").getOrNull() ?: return null
                val request2 =
                    HttpRequest.newBuilder(URI.create(location))
                        .header("User-Agent", AuthConfig.USER_AGENT)
                        .timeout(Duration.ofSeconds(5L)).GET()
                        .build()


                val response2 = c.send(request2, HttpResponse.BodyHandlers.ofByteArray())
                response2.body() ?: return null
            }

            200 -> {
                response.body() ?: return null
            }

            else -> {
                println("Failed to generate avatar. Status code: ${response.statusCode()}")
                println(url)
                return null
            }
        }
        return body
    }

}