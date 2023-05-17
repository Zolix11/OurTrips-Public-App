package hu.bme.aut.ourtrips.model.locationutils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "AppPreferences",
        Context.MODE_PRIVATE
    )

    fun isLocationPermissionGranted(): Boolean {
        return sharedPreferences.getBoolean("isLocationPermissionGranted", false)
    }

    fun setLocationPermissionGranted(isGranted: Boolean) {
        sharedPreferences.edit().putBoolean("isLocationPermissionGranted", isGranted).apply()
    }

    fun setNotificationGranted(isGranted: Boolean) {
        sharedPreferences.edit().putBoolean("isNotificationPermissionGranted", isGranted).apply()
    }

    fun isNotificationGranted() : Boolean{
        return  sharedPreferences.getBoolean("isNotificationPermissionGranted", false)
    }

    fun getLatitude(): Float {
        return sharedPreferences.getFloat("latitude", 0f)
    }

    fun setLatitude(latitude: Double) {
        sharedPreferences.edit().putFloat("latitude", latitude.toFloat()).apply()
    }

    fun getLongitude(): Float {
        return sharedPreferences.getFloat("longitude", 0f)
    }

    fun setLongitude(longitude: Double) {
        sharedPreferences.edit().putFloat("longitude", longitude.toFloat()).apply()
    }


}
