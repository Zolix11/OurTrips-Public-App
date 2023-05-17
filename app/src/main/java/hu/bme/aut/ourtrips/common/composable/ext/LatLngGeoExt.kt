package hu.bme.aut.ourtrips.common.composable.ext

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

fun GeoPoint.toLatLng() : LatLng{
    return LatLng(this.latitude,this.longitude)
}

fun LatLng.toGeoPoint() : GeoPoint{
    return GeoPoint(this.latitude,this.longitude)
}