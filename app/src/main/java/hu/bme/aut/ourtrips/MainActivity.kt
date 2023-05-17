package hu.bme.aut.ourtrips

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarManager
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.locationutils.AppPreferences
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {


    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var accountPreferences: AccountPreferences

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askPermissions() {
        val locationPermission = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
        val notificationPermission = ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)

        if (locationPermission == PackageManager.PERMISSION_GRANTED && notificationPermission == PackageManager.PERMISSION_GRANTED) {
            // Both permissions are already granted.
            return
        }

        val permissionsToRequest = mutableListOf<String>()
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(ACCESS_FINE_LOCATION)
        }
        if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(POST_NOTIFICATIONS)
        }

        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[ACCESS_FINE_LOCATION] == true) {
                appPreferences.setLocationPermissionGranted(true)
            } else {
                SnackbarManager.showMessage(R.string.refused_location_permission)
            }
            if (permissions[POST_NOTIFICATIONS] == true) {
                appPreferences.setNotificationGranted(true)
            } else {
                SnackbarManager.showMessage(R.string.refused_notification_permission)
            }
        }


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        askPermissions()
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.d("MyFirebaseMessagingService",token)
            accountPreferences.setFCMToken(token = token)
        }
        setContent {
            OurTripsApp()
        }
    }
}
