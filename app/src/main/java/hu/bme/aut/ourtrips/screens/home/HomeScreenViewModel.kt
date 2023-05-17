package hu.bme.aut.ourtrips.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.model.FireStoreFriendShip
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.impl.FriendRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.PostRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val friendRepository: FriendRepositoryImpl,
    private val postRepository: PostRepositoryImpl,
    private val accountPreferences: AccountPreferences
) : MyTripsViewModel() {

    var uiState =
        mutableStateOf(HomeUiState(userId = accountPreferences.getFireStoreUser().userUID))
        private set

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private val _friendPosts = MutableStateFlow<PagingData<FireStorePost>>(PagingData.empty())
    val friendsPosts: StateFlow<PagingData<FireStorePost>>
        get() = _friendPosts

    private var friendsUIDs: List<String?> = emptyList()

    fun refresh() {
        launchCatching {
            if (friendsUIDs.isNotEmpty()) {
                _isRefreshing.emit(true)
                val firstPage = postRepository.downLoadFriendsPosts(friendsUIDs).first()
                _friendPosts.value = firstPage
                delay(1000)
                _isRefreshing.emit(false)
            }
        }
    }

    init {
        viewModelScope.launch {
            val localUID = accountPreferences.getFireStoreUser().userUID
            friendRepository.getFriends(localUID).collect { fireStoreFriendShips ->
                friendsUIDs = fireStoreFriendShips
                    .flatMap { listOf(it.requester.userUID, it.accepter.userUID) }
                    .distinct()
                    .toMutableList()
                Log.d("TEST",friendsUIDs.toString())
                if(friendsUIDs.isNotEmpty()){
                    postRepository.downLoadFriendsPosts(friendsUIDs)
                        .cachedIn(viewModelScope)
                        .collectLatest { pagingData ->
                            _friendPosts.value = pagingData
                        }
                }
                else{
                    postRepository.downLoadFriendsPosts(listOf(localUID))
                        .cachedIn(viewModelScope)
                        .collectLatest { pagingData ->
                            _friendPosts.value = pagingData
                        }
                }

                if (friendsUIDs.isEmpty()) {
                    setAddFriendsDialog(true)

                }
            }

        }
    }

    fun setAddFriendsDialog(newValue: Boolean) {
        uiState.value = uiState.value.copy(addFriendsDialog = newValue)
    }

    fun setOpenSheetMap(newValue: Boolean) {
        uiState.value = uiState.value.copy(openSheetMap = newValue)
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

    fun setPhotoLocation(
        photoLoc: GeoPoint,
    ) {
        Log.d("LOCATION", photoLoc.toString())
        uiState.value = uiState.value.copy(
            photoLatitude = photoLoc.latitude,
            photoLongitude = photoLoc.longitude
        )
    }
}