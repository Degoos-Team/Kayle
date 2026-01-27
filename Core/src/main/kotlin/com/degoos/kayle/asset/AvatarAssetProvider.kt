package com.degoos.kayle.asset

import com.degoos.kayle.Kayle
import com.degoos.kayle.profile.PlayerProfileCache
import com.degoos.kayle.profile.PortraitView
import com.degoos.kayle.util.CaffeineService
import com.github.benmanes.caffeine.cache.Caffeine
import com.hypixel.hytale.server.core.asset.common.CommonAssetModule
import com.hypixel.hytale.server.core.asset.common.CommonAssetRegistry
import java.time.Duration
import java.util.*

data class AvatarKey(val uuid: UUID, val view: PortraitView)

/**
 * Provides caching and management for avatar assets.
 *
 * Configuration:
 * - The cache is limited to store 1000 entries at once.
 * - Cached entries are removed after 10 hours.
 */
object AvatarAssetProvider : CaffeineService<AvatarKey, String>(Kayle.instance) {

    const val PACK_NAME = "Degoos:KayleAvatars"

    suspend fun fetch(uuid: UUID, view: PortraitView): String? {
        return fetch(AvatarKey(uuid, view))
    }

    override fun Caffeine<Any, Any>.configure() {
        maximumSize(1000L)
        expireAfterWrite(Duration.ofHours(10))
        removalListener<AvatarKey, String> { _, value, _ ->
            value?.let {
                CommonAssetRegistry.removeCommonAssetByName(PACK_NAME, it)
            }
        }
    }

    override suspend fun fetchFromSource(key: AvatarKey): String? {
        val assetName = "Icons/PlayerAvatar/${key.view.apiKey}/${key.uuid}.png"
        val profile = PlayerProfileCache.fetch(key.uuid) ?: return null
        val avatar = profile.avatarService.fetch(key.view) ?: return null
        val icon = MemoryCommonAsset(assetName, avatar)
        CommonAssetModule.get().addCommonAsset(PACK_NAME, icon)
        return assetName
    }


}