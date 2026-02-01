package com.degoos.kayle.codec

import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object Vector3dSerializer : KSerializer<Vector3d> {


    override val descriptor = DoubleArraySerializer().descriptor

    override fun serialize(encoder: Encoder, value: Vector3d) {
        val array = doubleArrayOf(value.x, value.y, value.z)
        encoder.encodeSerializableValue(DoubleArraySerializer(), array)
    }

    override fun deserialize(decoder: Decoder): Vector3d {
        val array = decoder.decodeSerializableValue(DoubleArraySerializer())
        require(array.size == 3) { "Vector3d array must have 3 elements" }
        return Vector3d(array[0], array[1], array[2])
    }
}

object Vector3fSerializer : KSerializer<Vector3f> {

    override val descriptor = FloatArraySerializer().descriptor

    override fun serialize(encoder: Encoder, value: Vector3f) {
        val array = floatArrayOf(value.x, value.y, value.z)
        encoder.encodeSerializableValue(FloatArraySerializer(), array)
    }

    override fun deserialize(decoder: Decoder): Vector3f {
        val array = decoder.decodeSerializableValue(FloatArraySerializer())
        require(array.size == 3) { "Vector3f array must have 3 elements" }
        return Vector3f(array[0], array[1], array[2])
    }
}

object TransformSerializer : KSerializer<Transform> {

    @Serializable
    data class Surrogate(
        @Serializable(Vector3dSerializer::class) val position: Vector3d,
        @Serializable(Vector3fSerializer::class) val rotation: Vector3f
    )

    override val descriptor = Surrogate.serializer().descriptor


    override fun serialize(encoder: Encoder, value: Transform) {
        encoder.encodeSerializableValue(Surrogate.serializer(), Surrogate(value.position, value.rotation))
    }

    override fun deserialize(decoder: Decoder): Transform {
        val surrogate = decoder.decodeSerializableValue(Surrogate.serializer())
        return Transform(surrogate.position, surrogate.rotation)
    }
}