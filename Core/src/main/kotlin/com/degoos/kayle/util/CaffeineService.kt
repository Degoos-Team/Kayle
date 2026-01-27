package com.degoos.kayle.util

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

abstract class CaffeineService<K : Any, V : Any>(
    private val scope: CoroutineScope
) {

    protected val cache: AsyncLoadingCache<K, V?> = Caffeine.newBuilder().apply { configure() }
        .buildAsync { key, executor ->
            scope.future(Dispatchers.IO) {
                fetchFromSource(key)
            }
        }

    protected abstract fun Caffeine<Any, Any>.configure()

    protected abstract suspend fun fetchFromSource(key: K): V?

    protected fun put(key: K, value: V) = cache.put(key, CompletableFuture.completedFuture(value))

    suspend fun fetch(key: K): V? {
        return cache.get(key).await()
    }

    fun invalidate(key: K) = cache.synchronous().invalidate(key)
}