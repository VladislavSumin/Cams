package ru.vladislavsumin.cams.camera.protocol.motion

import com.google.gson.*
import java.lang.reflect.Type

object MotionEventJsonUtils {
    private val gson: Gson //TODO make as Bean

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(MotionEvent::class.java, MotionEventDeserializer())
        gson = gsonBuilder.create()
    }

    fun fromJson(json: String): MotionEvent = gson.fromJson(json, MotionEvent::class.java)

    private class MotionEventDeserializer : JsonDeserializer<MotionEvent> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MotionEvent {
            val data = json.asJsonObject["AlarmInfo"].asJsonObject
            return MotionEvent(
                    MotionEventType.valueOf(data["Event"].asString),
                    MotionEventStatus.valueOf(data["Status"].asString)
            )
        }
    }
}