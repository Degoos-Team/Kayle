package com.degoos.kayle.dsl

import com.hypixel.hytale.event.EventPriority
import com.hypixel.hytale.event.EventRegistry
import com.hypixel.hytale.event.IAsyncEvent
import com.hypixel.hytale.event.IBaseEvent
import java.util.concurrent.CompletableFuture

/**
 * Registers an event consumer in the event registry with a given key and priority.
 *
 * @param Key The type of the key used to register the event consumer.
 * @param Event The type of the event subclass being handled.
 * @param key The unique key associated with the event consumer.
 * @param priority The priority level of the event consumer. Defaults to `EventPriority.NORMAL`.
 * @param consumer The function to be invoked when the event is triggered.
 */
inline fun <Key, reified Event : IBaseEvent<Key>> EventRegistry.register(
    key: Key,
    priority: EventPriority = EventPriority.NORMAL,
    noinline consumer: (Event) -> Unit
) = register(priority, Event::class.java, key!!, consumer)

/**
 * Registers an event with the specified priority and consumer.
 * This method allows handling a specific event type by supplying a consumer
 * function that will be invoked when the event is triggered.
 *
 * @param priority The priority level of the event listener. Defaults to [EventPriority.NORMAL].
 *                 Determines the order of execution when multiple listeners exist for the same event.
 * @param consumer A function that consumes the event when it is triggered.
 *                 This function defines the behavior of the listener.
 * @param Event The type of the event being registered. Must be a subtype of [IBaseEvent].
 */
inline fun <reified Event : IBaseEvent<*>> EventRegistry.register(
    priority: EventPriority = EventPriority.NORMAL,
    noinline consumer: (Event) -> Unit
) = register(priority, Event::class.java, consumer)

/**
 * Registers an asynchronous event listener for events of the specified type and key.
 *
 * @param Key The type of the key identifying the events to listen for.
 * @param Event The type of the event being registered to handle. Must implement [IAsyncEvent].
 * @param key The key identifying the events to listen for.
 * @param priority The priority of the event listener. Default is [EventPriority.NORMAL].
 * @param consumer A function that consumes a [CompletableFuture] of the event and returns
 * another [CompletableFuture] indicating further asynchronous processing.
 */
inline fun <Key, reified Event : IAsyncEvent<Key>> EventRegistry.registerAsync(
    key: Key,
    priority: EventPriority = EventPriority.NORMAL,
    noinline consumer: (CompletableFuture<Event>) -> CompletableFuture<Event>
) = registerAsync(priority, Event::class.java, key!!, consumer)

/**
 * Registers an asynchronous event with the event registry.
 *
 * @param Event The type of the event to be registered, extending `IAsyncEvent<Void>`.
 * @param priority The priority of the event listener, determining the order in which
 *                 it will be executed relative to other listeners. Defaults to `EventPriority.NORMAL`.
 * @param consumer A function that accepts a `CompletableFuture` of the event as input
 *                 and returns a transformed `CompletableFuture` of the event as output.
 */
inline fun <reified Event : IAsyncEvent<Void>> EventRegistry.registerAsync(
    priority: EventPriority = EventPriority.NORMAL,
    noinline consumer: (CompletableFuture<Event>) -> CompletableFuture<Event>
) = registerAsync(priority, Event::class.java, consumer)

/**
 * Registers ad global event listener.
 *
 * This method allows for the registration of an asynchronous event consumer
 * that listens to all instances of the specified event type globally.
 *
 * @param consumer The consumer function that processes the event asynchronously.
 *                 It receives a `CompletableFuture` representing the event
 *                 and returns a `CompletableFuture` after processing is complete.
 */
inline fun <Key, reified Event : IBaseEvent<Key>> EventRegistry.registerGlobal(
    noinline consumer: (Event) -> Unit
) = registerGlobal(Event::class.java, consumer)

/**
 * Registers an asynchronous global event listener.
 *
 * This method allows for the registration of an asynchronous event consumer
 * that listens to all instances of the specified event type globally.
 *
 * @param consumer The consumer function that processes the event asynchronously.
 *                 It receives a `CompletableFuture` representing the event
 *                 and returns a `CompletableFuture` after processing is complete.
 */
inline fun <Key, reified Event : IAsyncEvent<Key>> EventRegistry.registerAsyncGlobal(
    noinline consumer: (CompletableFuture<Event>) -> CompletableFuture<Event>
) = registerAsyncGlobal(Event::class.java, consumer)
