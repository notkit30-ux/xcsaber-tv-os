package com.xcsaber.tv

import android.content.Context
import android.content.SharedPreferences

/** Favorites, hidden apps and recent apps. Stored privately on the device only. */
class LauncherPrefs(context: Context) {

    private val sp: SharedPreferences =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    val favorites: List<String> get() = read(KEY_FAVORITES)
    val hidden: Set<String> get() = read(KEY_HIDDEN).toSet()
    val recents: List<String> get() = read(KEY_RECENTS)

    fun isFavorite(key: String): Boolean = key in favorites

    fun toggleFavorite(key: String) {
        val list = favorites.toMutableList()
        if (!list.remove(key)) list.add(key)
        write(KEY_FAVORITES, list)
    }

    fun hide(key: String) {
        val list = read(KEY_HIDDEN).toMutableList()
        if (key !in list) list.add(key)
        write(KEY_HIDDEN, list)
    }

    fun clearHidden() = write(KEY_HIDDEN, emptyList())

    fun addRecent(key: String) {
        val list = recents.toMutableList()
        list.remove(key)
        list.add(0, key)
        write(KEY_RECENTS, list.take(MAX_RECENTS))
    }

    fun clearRecents() = write(KEY_RECENTS, emptyList())

    private fun read(key: String): List<String> {
        val raw = sp.getString(key, "") ?: ""
        return raw.split('\n').filter { it.isNotBlank() }
    }

    private fun write(key: String, values: List<String>) {
        sp.edit().putString(key, values.joinToString("\n")).apply()
    }

    private companion object {
        const val FILE = "xcsaber_launcher"
        const val KEY_FAVORITES = "favorites"
        const val KEY_HIDDEN = "hidden"
        const val KEY_RECENTS = "recents"
        const val MAX_RECENTS = 12
    }
}
