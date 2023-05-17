package hu.bme.aut.ourtrips.model.locationutils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import hu.bme.aut.ourtrips.model.FireStoreUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val accountPreferences: SharedPreferences = context.getSharedPreferences(
        "AppPreferences",
        Context.MODE_PRIVATE
    )

    fun setFCMToken(token: String) {
        accountPreferences.edit().apply {
            putString("fcmToken", token)
            apply()
        }
    }
    fun getFCMToken(): String {
        return accountPreferences.getString("fcmToken", "") ?: ""
    }
    fun getFireStoreUser(): FireStoreUser {
        val userUID = accountPreferences.getString("userUID", "") ?: ""
        val fullName = accountPreferences.getString("fullName", "") ?: ""
        val userName = accountPreferences.getString("userName", "") ?: ""
        val email = accountPreferences.getString("email", "") ?: ""
        val bio = accountPreferences.getString("bio", "") ?: ""
        val profilePictureUrl = accountPreferences.getString("profilePictureUrl", "") ?: ""
        return FireStoreUser(
            userUID = userUID,
            fullName = fullName,
            userName = userName,
            email = email,
            bio = bio,
            profilePictureUrl = profilePictureUrl
        )
    }

    fun setFireStoreUser(user: FireStoreUser) {
        accountPreferences.edit().apply {
            putString("userUID", user.userUID)
            putString("fullName", user.fullName)
            putString("userName", user.userName)
            putString("email", user.email)
            putString("bio", user.bio)
            putString("profilePictureUrl", user.profilePictureUrl)
            apply()
        }
    }

    fun deleteFireStoreUser() {
        val token = getFCMToken()
        accountPreferences.edit().clear().apply()
        setFCMToken(token)
    }

}