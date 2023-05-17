package hu.bme.aut.ourtrips.screens.post

import android.content.ContentResolver
import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarManager
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.locationutils.LocationSaver
import hu.bme.aut.ourtrips.model.repository.impl.AccountRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.PostRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.StorageForPostRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val storageForPostRepository: StorageForPostRepositoryImpl,
    private val postRepository: PostRepositoryImpl,
    private val accountRepository: AccountRepositoryImpl,
    private val locationSaver: LocationSaver
) : MyTripsViewModel() {

    var uiState = mutableStateOf(
        PostUIState(
            userLocation = locationSaver.getDeviceLocation()
        )
    )
        private set


    fun onPhotoDescriptionChange(newValue: String) {
        uiState.value = uiState.value.copy(postDescription = newValue)
    }

    init {
        viewModelScope.launch {
            accountRepository.currentFireStoreUser.collect {
                uiState.value = uiState.value.copy(userName = it.userName)
                Log.d("USER", uiState.value.userName)
            }
        }
    }

    fun onPostPhoto(startUpload: ()-> Unit, popNav : () -> Unit) {
        viewModelScope.launch {
            startUpload()
            storageForPostRepository.uploadPostPicture(Uri.parse(uiState.value.photoUri))
            storageForPostRepository.currentPostPicTureUri.collect { newUri ->
                Log.d("USER",newUri.toString())
                val geoPoint = GeoPoint( uiState.value.photoLatitude,   uiState.value.photoLongitude)
                val fireStoreUser = accountRepository.fireStoreUser
                val fireStorePost = FireStorePost(
                    "",
                    uiState.value.postDescription,
                    geoPoint,
                    newUri.toString(),
                    Timestamp.now(),
                    FireStorePost.PostUser(
                        username = fireStoreUser.userName,
                        profilePictureUrl = fireStoreUser.profilePictureUrl,
                        userUID = fireStoreUser.userUID
                    )
                )
                postRepository.createPost(fireStorePost)
                postRepository.createdPostState.collect{
                    if(it){
                        setCircularProgress(false)
                        popNav()
                    }
                    else{
                        setCircularProgress(false)
                        popNav()
                        SnackbarManager.showMessage(R.string.generic_error)
                    }
                }
            }
        }
    }


    fun makeMapVisible(newValue: Boolean) {
        uiState.value = uiState.value.copy(visibleMap = newValue)
    }


    fun setPostPhotoDialog(newValue: Boolean) {
        uiState.value = uiState.value.copy(postPhotoDialog = newValue)
    }

    fun setCircularProgress(newValue: Boolean) {
        uiState.value = uiState.value.copy(circularProgress = newValue)
    }

    fun setPhotoLocationInfo(newValue: Boolean) {
        uiState.value = uiState.value.copy(photoHasLocationInfo = newValue)
    }

    fun setPickLocation(newValue: Boolean) {
        uiState.value = uiState.value.copy(pickLocation = newValue)
    }

    fun setPhotoPicker(newValue: Boolean) {
        uiState.value = uiState.value.copy(PhotoPicker = newValue)
    }

    fun removePhoto() {
        uiState.value = uiState.value.copy(photoUri = "")
        uiState.value = uiState.value.copy(photoHasLocationInfo = false)
    }

    fun setPostCheckPhotoDialog(newValue: Boolean) {
        uiState.value = uiState.value.copy(postCheckPhotoDialog = newValue)
    }

    fun setPhotoLocation(newLatLng: LatLng) {
        uiState.value = uiState.value.copy(photoLongitude = newLatLng.longitude)
        uiState.value = uiState.value.copy(photoLatitude = newLatLng.latitude)
    }

    fun onAddPhoto(context: Context, uri: Uri) {
        val contentResolver: ContentResolver = context.contentResolver

        val inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {

            val exif = ExifInterface(inputStream)
            val latLong = FloatArray(2)
            val latLng = exif.getLatLong(latLong)

            if (latLng) {
                val latitude = latLong[0]
                val longitude = latLong[1]
                uiState.value = uiState.value.copy(photoLatitude = latitude.toDouble())
                uiState.value = uiState.value.copy(photoLongitude = longitude.toDouble())
                uiState.value = uiState.value.copy(photoUri = uri.toString())
                uiState.value = uiState.value.copy(photoHasLocationInfo = true)

            } else {
                uiState.value = uiState.value.copy(photoUri = uri.toString())
                uiState.value = uiState.value.copy(photoHasLocationInfo = false)
            }
        }
    }

}