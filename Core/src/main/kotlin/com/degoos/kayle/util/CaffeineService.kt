package com.degoos.kayle.util

import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

/**
 * Abstract class that provides a caching service using an asynchronous Caffeine cache.
 * Facilitates efficient fetching and storage of key-value pairs, with customizable behaviors.
 *
 * @param K Type of the key used in the cache.
 * @param V Type of the value stored in the cache.
 * @param scope Coroutine scope used for asynchronous cache operations.
 */
abstract class CaffeineService<K : Any, V : Any>(
    private val scope: CoroutineScope
) {

    protected val cache: AsyncLoadingCache<K, V?> = Caffeine.newBuilder().apply { configure() }
        .buildAsync { key, _ ->
            scope.future(Dispatchers.IO) {
                fetchFromSource(key)
            }
        }

    /**
     * Configures the properties of the Caffeine cache builder.
     *
     * This method allows subclasses to define custom behaviors for the Caffeine cache
     * by modifying its settings, such as expiry policies, eviction strategies, maximum
     * size, or other relevant configurations. It is invoked during the cache initialization
     * process in the [CaffeineService] class.
     *
     * Subclasses must implement this method to apply specific configurations to the cache.
     * The customization is performed within the scope of a [Caffeine] builder.
     */
    protected abstract fun Caffeine<Any, Any>.configure()

    /**
     * Fetches the value associated with the provided key from the source.
     *
     * This method is used as a fallback mechanism to retrieve data when it is not present
     * in the cache. Subclasses must implement this method to define the logic for fetching
     * data from the underlying data source corresponding to the provided key.
     *
     * The method is executed within a coroutine context, allowing for suspending
     * operations such as network or database calls.
     *
     * @param key The key for which the associated value needs to be fetched from the source.
     * @return The value associated with the key, or null if no value is found in the source.
     */
    protected abstract suspend fun fetchFromSource(key: K): V?

    /**
     * Stores the specified key-value pair in the cache.
     *
     * @param key The key to store the value against in the cache.
     * @param value The value to be associated with the specified key.
     */
    protected fun put(key: K, value: V) = cache.put(key, CompletableFuture.completedFuture(value))

    /**
     * Retrieves the value associated with the given key from the cache.
     *
     * This method attempts to fetch the value associated with the specified key
     * from the underlying cache. If the value is not present in the cache,
     * fetching from the source may be triggered depending on the cache's
     * configuration and behavior. The operation is performed in a suspending
     * manner to support asynchronous workflows.
     *
     * @param key The key whose associated value is to be retrieved from the cache.
     * @return The value associated with the provided key, or null if the key does not exist in the cache.
     */
    suspend fun fetch(key: K): V? {
        return cache.get(key).await()
    }

    /**
     * Removes the cached entry associated with the specified key.
     *
     * @param key The key identifying the cache entry to invalidate.
     */
    fun invalidate(key: K) = cache.synchronous().invalidate(key)

    /**
     * Invalidates all entries in the cache.
     */
    fun invalidateAll() = cache.synchronous().invalidateAll()
}