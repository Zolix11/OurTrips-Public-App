package hu.bme.aut.ourtrips.screens.map

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.model.FireStoreFriendShip
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.locationutils.LocationSaver
import hu.bme.aut.ourtrips.model.repository.impl.FriendRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.MapPostRepostioryImpl
import hu.bme.aut.ourtrips.model.repository.impl.PostRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val locationSaver: LocationSaver,
    private val mapPostRepository: MapPostRepostioryImpl,
    private val postRepository: PostRepositoryImpl,
    private val accountPreferences: AccountPreferences,
) : MyTripsViewModel(

) {
    var uiState = mutableStateOf(
        MapUiState(
            userLocation = locationSaver.getDeviceLocation(),
            markerLocation = null,
            northeast = null,
            southwest = null,
            userId = accountPreferences.getFireStoreUser().userUID
        )
    )
        private set

    private var searchJob: Job? = null

    private var _posts = mapPostRepository.mapPosts
    var post = _posts

    var feedpost = mapPostRepository.mapPosts


    fun performSearchForPostsOnScreen() {
        searchJob?.cancel()
        searchJob = launchCatching {
            delay(500)
            mapPostRepository.fetchPostsOnMapScreen(
                uiState.value.southwest!!,
                uiState.value.northeast!!
            )
        }

    }

    fun likePost(post: FireStorePost) {
        launchCatching {
            postRepository.likePost(post)
        }
    }

    fun dislikePost(post: FireStorePost) {
        launchCatching {
            postRepository.dislikePost(post)
        }
    }
    fun setSearchBounds(southwest: LatLng?, northeast: LatLng?) {
        uiState.value = uiState.value.copy(northeast = northeast)
        uiState.value = uiState.value.copy(southwest = southwest)
    }

}