package hu.bme.aut.ourtrips.model.locationutils

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class LocationSaver @Inject constructor(
    private val appPreferences: AppPreferences,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    init {
        deviceLocation()
    }


    private fun deviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        val userLocation = LatLng(task.result.latitude, task.result.longitude)
                        appPreferences.setLatitude(userLocation.latitude)
                        appPreferences.setLongitude(userLocation.longitude)
                    }
                }
            }
        } catch (e: SecurityException) {
            // Show error or something
        }

    }

    fun getDeviceLocation(): LatLng {
        return LatLng(
            appPreferences.getLatitude().toDouble(),
            appPreferences.getLongitude().toDouble()
        )
    }


}