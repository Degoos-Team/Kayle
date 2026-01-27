package com.degoos.kayle.profile

import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ForkJoinPool

/**
 * Provides a caching mechanism for `PlayerProfile` objects to reduce the cost of repeated fetch operations.
 * Cache entries are automatically evicted after a specified duration or when the maximum size is reached.
 * This class supports fetching player profiles by both UUID and username, and ensures that common operations
 * are executed asynchronously to improve performance.
 *
 * Properties:
 * - Employs a UUID-keyed cache for quick retrieval of player profiles.
 * - Maintains a name-based index to allow lookups by username.
 * - Manages pending external profile request processes to avoid redundant operations for the same username.
 * - Utilizes a shared thread pool for executing asynchronous tasks.
 *
 * Methods:
 *
 * - `fetch(uuid: UUID)`: Retrieves the `PlayerProfile` associated with the specified UUID from the cache or fetches it
 *   from an external source if not present. Once fetched, the profile is added to the cache.
 *
 * - `fetch(userName: String)`: Retrieves the `PlayerProfile` associated with the specified username. If the username
 *   does not correspond to a cached profile, a slow external request is issued to fetch and cache the profile. Pending
 *   requests for the same username are tracked and re-used to avoid redundancy.
 */
object PlayerProfileCache {

    private val executor = ForkJoinPool.commonPool()

    private val nameIndex = ConcurrentHashMap<String, UUID>()

    private val pendingRequests = ConcurrentHashMap<String, CompletableFuture<PlayerProfile?>>()

    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterAccess(Duration.ofMinutes(10))
        .removalListener<UUID, PlayerProfile> { _, value, _ -> value?.let { nameIndex.remove(it.username.lowercase()) } }
        .buildAsync<UUID, PlayerProfile?>()

    /**
     * Fetches the `PlayerProfile` of a user based on their UUID.
     * The fetched `PlayerProfile` is cached for future lookups.
     *
     * @param uuid The UUID of the player whose profile is to be retrieved.
     * @return A `CompletableFuture` containing the `PlayerProfile` of the player, or `null` if the profile could not be fetched.
     */
    fun fetch(uuid: UUID) = cache.get(uuid) { uuid, executor ->
        CompletableFuture.supplyAsync({
            PlayerProfile.fetch(uuid).also { profile -> profile?.let { nameIndex[it.username.lowercase()] = uuid } }
        }, executor)
    }

    /**
     * Fetches the `PlayerProfile` of a user based on their username.
     * If the username is already resolved, it fetches the profile directly.
     * Otherwise, asynchronously processes the request and caches the result.
     *
     * @param userName The username of the player whose profile is to be fetched. Case-insensitive.
     * @return A `CompletableFuture` containing the `PlayerProfile` of the player, or `null` if the profile could not be fetched.
     */
    fun fetch(userName: String): CompletableFuture<PlayerProfile?> {
        val lower = userName.lowercase()
        val uuid = nameIndex[lower]
        if (uuid != null) {
            return fetch(uuid)
        }

        return pendingRequests.computeIfAbsent(lower) { key ->
            CompletableFuture.supplyAsync({
                val profile = PlayerProfile.fetch(userName) // Llamada lenta a DB
                if (profile != null) {
                    nameIndex[key] = profile.uuid
                    cache.asMap().putIfAbsent(profile.uuid, CompletableFuture.completedFuture(profile))
                }
                profile
            }, executor).whenComplete { _, _ -> pendingRequests.remove(key) }
        }
    }
}