package com.degoos.kayle.asset

import com.hypixel.hytale.server.core.asset.common.CommonAsset
import java.util.concurrent.CompletableFuture

/**
 * A common asset that doesn't require a path to work.
 *
 * This wouldn't be necessary, as FileCommonAsset should be enough,
 * but it has a bug where it searches for a file anyway!
 */
class MemoryCommonAsset(name: String, val data: ByteArray, hash: String = hash(data)) : CommonAsset(name, hash, data) {

    override fun getBlob0(): CompletableFuture<ByteArray?>? {
        return CompletableFuture.completedFuture(data)
    }

    override fun toString(): String {
        return "MemoryCommonAsset(data=${data.size})"
    }


}
