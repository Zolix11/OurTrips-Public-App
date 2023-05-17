package hu.bme.aut.ourtrips.screens.myposts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.impl.PostRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class MyPostsScreenViewModel @Inject constructor(
    private val postRepository: PostRepositoryImpl,
    private val accountPreferences: AccountPreferences
) : MyTripsViewModel() {

    var uiState =
        mutableStateOf(MyPostsUiState(userId = accountPreferences.getFireStoreUser().userUID))
        private set

    private val _myPosts = MutableStateFlow<PagingData<FireStorePost>>(PagingData.empty())
    val myPosts: StateFlow<PagingData<FireStorePost>>
        get() = _myPosts

    init {

        launchCatching {
            postRepository.downloadUsersPosts(accountPreferences.getFireStoreUser().userUID)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _myPosts.value = pagingData
                }
        }
    }

    fun setOpenSheetMap(newValue: Boolean) {
        uiState.value = uiState.value.copy(openSheetMap = newValue)
    }

    fun deletePost(post : FireStorePost){
        launchCatching {
            postRepository.deletePost(post)
        }
    }

    fun setPhotoLocation(
        photoLoc: GeoPoint,
    ) {
        uiState.value = uiState.value.copy(
            photoLatitude = photoLoc.latitude,
            photoLongitude = photoLoc.longitude
        )
    }

}
