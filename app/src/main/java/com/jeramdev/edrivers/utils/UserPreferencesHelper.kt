package com.jeramdev.edrivers.utils

import android.content.Context
import com.jeramdev.edrivers.data.model.UserPrefsModel
import com.jeramdev.edrivers.utils.PreferenceHelper.get
import com.jeramdev.edrivers.utils.PreferenceHelper.set
import com.jeramdev.edrivers.utils.PreferenceHelper.wipe

class UserPreferencesHelper(val context: Context) {

    private val prefs = PreferenceHelper.prefs(context, Constants.AUTH_PREFS)

    fun saveUserPrefs(userPrefs: UserPrefsModel) {
        prefs[Constants.AUTH_PREF_EMAIL] = userPrefs.email
        prefs[Constants.AUTH_PREF_ROL] = userPrefs.rol
        prefs[Constants.AUTH_PREF_DRIVING_SCHOOL] = userPrefs.drivingSchoolName
        prefs[Constants.AUTH_PREF_STAY] = userPrefs.stayLoggedIn
    }

    fun getEmailPref(): String? {
        return prefs[Constants.AUTH_PREF_EMAIL]
    }

    fun getRolPref(): String? {
        return prefs[Constants.AUTH_PREF_ROL]
    }

    fun getDrivingSchoolPref(): String? {
        return prefs[Constants.AUTH_PREF_DRIVING_SCHOOL]
    }

    fun getUserPrefs(): UserPrefsModel {
        return UserPrefsModel(
            email = prefs[Constants.AUTH_PREF_EMAIL] ?: "",
            rol = prefs[Constants.AUTH_PREF_ROL] ?: "",
            drivingSchoolName = prefs[Constants.AUTH_PREF_DRIVING_SCHOOL] ?: "",
            stayLoggedIn = prefs[Constants.AUTH_PREF_STAY] ?: false
        )
    }

    fun deleteUserPrefs() {
        prefs.wipe()
    }
}