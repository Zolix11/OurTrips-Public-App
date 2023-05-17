import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.model.FireStorePost

@Composable
fun PostMarker(post: FireStorePost) {

    val postLatLong = LatLng(post.location.latitude, post.location.longitude)
    Marker(
        state = MarkerState(postLatLong),
        title = post.user.username,

    )

}