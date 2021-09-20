package com.amherickeyboard.voicetyping.translator

import android.content.Context
import android.content.SharedPreferences

class ThemesSharedPreference(context: Context) {
    private val PREFS_NAME = "AMHERIC_KEYBOARD"
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var editor: SharedPreferences.Editor = sharedPreferences.edit()
    var themePosition: Int
        get() = sharedPreferences.getInt(ThemePosition, 0)
        set(ConfirmPassword) {
            editor.putInt(ThemePosition, ConfirmPassword)
            editor.commit()
        }

    companion object {
        const val ThemePosition = "ThemePosition"
    }

}