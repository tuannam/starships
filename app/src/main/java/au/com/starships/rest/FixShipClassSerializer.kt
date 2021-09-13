package au.com.starships.rest

import au.com.starships.model.Ship
import au.com.starships.model.StarShipClass
import com.google.gson.*
import java.lang.reflect.Type

class ShipSerializer: JsonDeserializer<Ship> {
    private val gson: Gson = GsonBuilder().create()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Ship {
        val jsonObject = json.asJsonObject
        val shipClass = jsonObject.getAsJsonPrimitive("starship_class").asString.capitalizeWords()
        val starShipClass = StarShipClass.values().find { it.type == shipClass } ?: run { StarShipClass.UNKNOWN }
        val instance: Ship = gson.fromJson(json, typeOfT) as Ship
        instance.starshipClass = starShipClass
        return instance
    }
}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
