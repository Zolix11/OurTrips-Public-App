package hu.bme.aut.ourtrips.screens.post

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

data class PostUIState constructor(
    var photoUri : String="",
    var photoLatitude : Double=0.0,
    var photoLongitude : Double=0.0,
    var postDate : Timestamp=Timestamp.now(),
    var postDescription : String="",
    var photoHasLocationInfo : Boolean = false,
    var PhotoPicker : Boolean = false,
    var visibleMap : Boolean = false,
    var postCheckPhotoDialog : Boolean = false,
    var postPhotoDialog : Boolean = false,
    var pickLocation : Boolean = false,
    var circularProgress : Boolean = false,
    var userLocation : LatLng,
    val userName : String ="",
)
