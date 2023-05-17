package hu.bme.aut.ourtrips.screens.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import hu.bme.aut.ourtrips.model.FireStorePost

data class CustomClusterItem(
    val fireStorePost: FireStorePost,
    val location : LatLng,
    val username : String,
    val data : String,
) : ClusterItem {
    override fun getPosition(): LatLng =
        location
    override fun getTitle(): String =
        username
    override fun getSnippet(): String =
        data
}
