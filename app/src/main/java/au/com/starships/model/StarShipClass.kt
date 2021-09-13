package au.com.starships.model

import au.com.starships.R
import com.google.gson.annotations.SerializedName

enum class StarShipClass(val type: String, val imageRes: Int) {
    CORVETTE("Corvette", R.drawable.corvette),
    STAR_DESTROYER("Star Destroyer", R.drawable.star_destroyer),
    MOBILE_BATTLE_STATION("Deep Space Mobile Battlestation", R.drawable.mobile_battlestation),
    LANDING_CRAFT("Landing Craft", R.drawable.landing_craft),
    LIGHT_FREIGHTER("Light Freighter", R.drawable.light_fighter),
    ASSAULT_STAR_FIGHTER("Assault Starfighter", R.drawable.assault_star_fighter),
    STAR_FIGHTER("Starfighter", R.drawable.star_fighter),
    STAR_DREADNOUGHT("Star Dreadnought", R.drawable.star_dreadnought),
    MEDIUM_TRANSPORT("Medium Transport", R.drawable.medium_transport),
//    PATROL_CRAFT("Patrol craft"),
//    ARMED_GOVERNMENT_TRANSPORT("Armed government transport"),
//    ESCORT_SHIP("Escort ship"),
//    STAR_CRUISER("Star Cruiser"),
//    SPACE_CRUISER("Space Cruiser"),
//    DROID_CONTROL_SHIP("Droid Control Ship"),
//    YATCH("Yacht"),
//    SPACE_TRANSPORT("Space Transport"),
//    DIPLOMATIC_BARGE("Diplomatic Barge"),
//    FREIGHTER("Freighter"),
//    ASSAULT_SHIP("Assault Ship"),
//    CAPITAL_SHIP("Capital Ship"),
//    TRANSPORT("Transport"),
//    CRUISER("cruiser"),
    UNKNOWN("Unknown", R.drawable.unknown),
}