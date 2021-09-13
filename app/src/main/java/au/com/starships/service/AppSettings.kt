package au.com.starships.service

import android.content.SharedPreferences
import au.com.starships.SHIP_ORDER
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class AppSettings {
    private object HOLDER {
        val INSTANCE = AppSettings()
    }

    @Inject
    lateinit var prefs: SharedPreferences

    companion object {
        val instance: AppSettings by lazy { HOLDER.INSTANCE }
        private val kFavourite = "kFavourite"
        private val kOrdering = "kOrdering"
    }

    private fun readFromPrefs(key: String, type: Type): Any? {
        if (prefs.contains(key)) {
            val gson = Gson()
            val value = prefs.getString(key, null)
            value?.let {
                return gson.fromJson(it, type)
            }
        }
        return null
    }

    private fun saveToPrefs(key: String, value: Any?) {
        val editor = prefs.edit()
        val json = Gson().toJson(value)
        editor.putString(key, json)
        editor.commit()
    }

    fun isShipFavourite(name: String): Boolean {
        val ships =
            readFromPrefs(kFavourite, object : TypeToken<HashSet<String>>() {}.type) as? Set<*>
        ships?.let {
            if (it.contains(name)) return true
        }
        return false
    }

    fun setShipFavourite(name: String, isFavourite: Boolean) {
        val ships = readFromPrefs(
            kFavourite,
            object : TypeToken<HashSet<String>>() {}.type
        ) as? HashSet<String> ?: run { HashSet<String>() }
        if (isFavourite) {
            ships.add(name)
        } else {
            ships.remove(name)
        }
        saveToPrefs(kFavourite, ships)
    }

    var shipOrdering: SHIP_ORDER
        get() {
            val value = prefs.getString(kOrdering, null)
            return value?.let {
                SHIP_ORDER.valueOf(value)
            } ?: run { SHIP_ORDER.NoOrdering }
        }
        set(newValue) {
            val editor = prefs.edit()
            editor.putString(kOrdering, newValue.name)
            editor.commit()
        }
}