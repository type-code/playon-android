package ga.chrom_web.player.multiplayer


import android.content.SharedPreferences
import android.graphics.Color

import javax.inject.Inject

import ga.chrom_web.player.multiplayer.di.App

class SharedPreferenceHelper {

    companion object {
        private val PREF_NICK = "nick"
        private val PREF_COLOR = "color"
    }

    @Inject
    lateinit var prefs: SharedPreferences

    val nick: String
        get() = prefs.getString(PREF_NICK, "DEFAULT")

    val color: Int
        get() = prefs.getInt(PREF_COLOR, 0)

    val hexColor: String
        get() = "#" + Integer.toHexString(color)

    init {
        App.getComponent().inject(this)
    }

    fun saveUser(nick: String, color: Int) {
        // TODO: maybe save color in String ???
        val editor = prefs.edit()
        editor.putString(PREF_NICK, nick)
        editor.putInt(PREF_COLOR, removeAlpha(color))
        editor.apply()
    }

    private fun removeAlpha(color: Int): Int {
        val hexColor = Integer.toHexString(color)
        if (hexColor.length <= 6) {
            return color
        }
        val colorWithoutAlpha = "#" + hexColor.substring(2)
        return Color.parseColor(colorWithoutAlpha)
    }
}
