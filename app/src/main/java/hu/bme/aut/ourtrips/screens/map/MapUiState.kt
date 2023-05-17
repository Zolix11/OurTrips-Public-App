package hu.bme.aut.ourtrips.screens.map

import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    var userLocation : LatLng,
    var predictionList : Boolean = false,
    var markerLocation : LatLng?,
    var southwest : LatLng?,
    var northeast : LatLng?,
    var userId : String?
)
