package com.degoos.kayle.codec

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.schema.SchemaContext
import com.hypixel.hytale.codec.schema.config.ObjectSchema
import com.hypixel.hytale.codec.schema.config.Schema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import org.bson.*

/**
 * A bridge implementation of Hytale's [Codec] interface that delegates serialization logic
 * to the **kotlinx.serialization** library.
 *
 * This adapter allows you to use standard Kotlin `@Serializable` data classes within Hytale's
 * [com.hypixel.hytale.codec.builder.BuilderCodec] system. It handles the conversion between
 * Hytale's internal BSON format and Kotlinx's JSON model.
 *
 * **Key Behavior:**
 * * **Case Preservation:** This codec **does not** transform key casing. Fields defined in camelCase
 * in your Kotlin class will remain camelCase in the final configuration/storage. This ensures
 * data integrity for maps or sensitive string keys.
 * * **Type Safety:** It leverages [KSerializer] to ensure strict typing during the decode process.
 *
 * @param T The type of the object to be serialized. Must be marked with `@Serializable`.
 * @param serializer The kotlinx serializer strategy for type [T].
 * @param json The Json configuration instance. Defaults to `ignoreUnknownKeys = true` and `encodeDefaults = true`.
 */
class KotlinxCodec<T>(
    private val serializer: KSerializer<T>,
    private val json: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true },
) : Codec<T> {

    override fun decode(bson: BsonValue, extraInfo: ExtraInfo): T? {
        try {
            if (bson.isNull) return null
            val jsonElement = bsonToJsonElement(bson)
            return json.decodeFromJsonElement(serializer, jsonElement)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    override fun encode(value: T, extraInfo: ExtraInfo): BsonValue {
        try {
            if (value == null) return BsonNull.VALUE
            val jsonElement = json.encodeToJsonElement(serializer, value)
            return jsonElementToBson(jsonElement)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    override fun toSchema(context: SchemaContext): Schema {
        return ObjectSchema()
    }

    private fun bsonToJsonElement(bson: BsonValue): JsonElement {
        return when (bson.bsonType) {
            BsonType.DOCUMENT -> buildJSONObject {
                bson.asDocument().forEach { (key, value) ->
                    put(key, bsonToJsonElement(value))
                }
            }

            BsonType.ARRAY -> buildJSONArray {
                bson.asArray().forEach { add(bsonToJsonElement(it)) }
            }

            BsonType.STRING -> JsonPrimitive(bson.asString().value)
            BsonType.INT32 -> JsonPrimitive(bson.asInt32().value)
            BsonType.INT64 -> JsonPrimitive(bson.asInt64().value)
            BsonType.DOUBLE -> JsonPrimitive(bson.asDouble().value)
            BsonType.BOOLEAN -> JsonPrimitive(bson.asBoolean().value)
            BsonType.NULL -> JsonNull
            else -> JsonPrimitive(bson.toString()) // Fallback
        }
    }

    private fun jsonElementToBson(element: JsonElement): BsonValue {
        return when (element) {
            is JsonObject -> {
                val doc = BsonDocument()
                element.forEach { (key, value) ->
                    doc.append(key, jsonElementToBson(value))
                }
                doc
            }

            is JsonArray -> {
                val arr = BsonArray()
                element.forEach { arr.add(jsonElementToBson(it)) }
                arr
            }

            is JsonNull -> BsonNull.VALUE

            is JsonPrimitive -> {
                when {
                    element.isString -> BsonString(element.content)
                    element.booleanOrNull != null -> BsonBoolean(element.boolean)
                    element.intOrNull != null -> BsonInt32(element.int)
                    element.longOrNull != null -> BsonInt64(element.long)
                    element.doubleOrNull != null -> BsonDouble(element.double)
                    else -> BsonString(element.content)
                }
            }

        }
    }

    private fun buildJSONObject(builder: JsonObjectBuilder.() -> Unit): JsonObject {
        return buildJsonObject(builder)
    }

    private fun buildJSONArray(builder: JsonArrayBuilder.() -> Unit): JsonArray {
        return buildJsonArray(builder)
    }
}

/**
 * Creates a Hytale-compatible [Codec] for the given reified type [T] using
 * **kotlinx.serialization**.
 *
 * This is a syntax-sugar helper to avoid manually passing the serializer.
 *
 * **Usage Example:**
 * ```kotlin
 * @Serializable
 * data class Reward(val id: String, val amount: Int)
 *
 * // In your Hytale Config Class:
 * val REWARD_CODEC = kotlinxCodecOf<Reward>()
 * val CODEC = BuilderCodec.builder(MyConfig.class) {...}
 * .append(new KeyedCodec<>("DailyReward", REWARD_CODEC, ...)
 * .add().build();
 * ```
 *
 * @param T The type to serialize. It must be annotated with `@Serializable`.
 * @return A new instance of [KotlinxCodec] for type [T].
 * @see KotlinxCodec
 */
inline fun <reified T> kotlinxCodecOf(
    json: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
): Codec<T> {
    return KotlinxCodec(serializer<T>(), json)
}