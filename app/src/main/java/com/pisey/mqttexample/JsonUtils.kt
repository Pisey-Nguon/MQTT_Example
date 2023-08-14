package com.pisey.mqttexample

import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type


class UriSerializer : JsonSerializer<Uri?> {
    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class UriDeserializer : JsonDeserializer<Uri?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        src: JsonElement, srcType: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(src.asString)
    }
}

val gsonS = GsonBuilder()
    .registerTypeAdapter(Uri::class.java, UriSerializer())
    .create()

val gsonD = GsonBuilder()
    .registerTypeAdapter(Uri::class.java, UriDeserializer())
    .create()


//convert a data class to a map
fun <T> T.serializeToMap(): Map<String, Any> {
    return convert()
}

//convert a map to a data class
inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gsonS.toJson(this)
    return gsonD.fromJson(json, object : TypeToken<O>() {}.type)
}

fun <T> T.toJsonString():String{

    return gsonS.toJson(this)
}
fun <T> String.fromJsonStringToDataClass(classOfT:Class<T> ):T?{
    return gsonD.fromJson(this,classOfT)
}
fun <T> Reader.fromJsonReaderToDataClass(classOfT:Class<T> ):T?{
    return gsonD.fromJson(this,classOfT)
}