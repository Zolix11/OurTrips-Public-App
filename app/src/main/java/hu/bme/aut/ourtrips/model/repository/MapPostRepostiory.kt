package hu.bme.aut.ourtrips.model.repository

import com.google.android.gms.maps.model.LatLng

interface MapPostRepostiory {

    suspend fun fetchPostsOnMapScreen(
        southwest: LatLng,
        northeast: LatLng
    )

}