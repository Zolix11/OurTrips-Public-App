package hu.bme.aut.ourtrips.screens.friends

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.model.FireStoreFriendShip
import hu.bme.aut.ourtrips.model.FireStoreUser
import hu.bme.aut.ourtrips.model.FriendWithStatus
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.impl.AccountRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.FriendRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendRepository: FriendRepositoryImpl,
    private val accountRepository: AccountRepositoryImpl,
    private val accountPreferences: AccountPreferences
) : MyTripsViewModel() {

    private val _acceptedFriendships = MutableStateFlow<List<FriendWithStatus>>(emptyList())
    private val _pendingFriendships = MutableStateFlow<List<FriendWithStatus>>(emptyList())
    private val _searchUserNameResult = MutableStateFlow<List<FireStoreUser>>(emptyList())
    var uiState = mutableStateOf(FriendsUiState())
        private set

    val acceptedFriendships: StateFlow<List<FriendWithStatus>>
        get() = _acceptedFriendships

    val pendingFriendships: StateFlow<List<FriendWithStatus>>
        get() = _pendingFriendships

    val searchUserNameResult: StateFlow<List<FireStoreUser>>
        get() = _searchUserNameResult

    init {
        loadDataFromFireStore()
    }

    fun searchForFriends() {
        launchCatching {
            accountRepository.searchForFriend(uiState.value.searchText).collect { searchResult ->
                setDialogForSearchResult(true)
                _searchUserNameResult.value = searchResult
            }
        }

    }

    fun setDialogForSearchResult(newValue: Boolean) {
        uiState.value = uiState.value.copy(dialogForSearchResult = newValue)
    }

    fun setSearchText(newValue: String) {
        uiState.value = uiState.value.copy(searchText = newValue)
    }

    fun setAcceptedFriendsList(newValue: Boolean) {
        uiState.value = uiState.value.copy(isAcceptedFriendsVisible = newValue)
    }

    fun setPendingFriendsList(newValue: Boolean) {
        uiState.value = uiState.value.copy(isPendingFriendsVisible = newValue)
    }

    fun acceptFriend(friendWithStatus: FriendWithStatus) {
        val fireStoreUser = accountPreferences.getFireStoreUser()
        val friendship = FireStoreFriendShip(
            created = friendWithStatus.created,
            friendShipId = friendWithStatus.friendShipId,
            status = "accepted",
            requester = FireStoreFriendShip.PostUser(
                profilePictureUrl = friendWithStatus.friend.profilePictureUrl,
                userUID = friendWithStatus.friend.userUID,
                username = friendWithStatus.friend.username
            ),
            accepter = FireStoreFriendShip.PostUser(
                fireStoreUser.profilePictureUrl,
                fireStoreUser.userUID,
                fireStoreUser.userName
            )
        )
        Log.d("FRIENDS",friendship.toString())
        launchCatching {
            friendRepository.acceptFriend(friendship)
        }
        updateLocalLists(friendWithStatus)
    }

    private fun updateLocalLists(friendWithStatus: FriendWithStatus){
        var currentList = _pendingFriendships.value.toMutableList()
        currentList.remove(friendWithStatus)
        _pendingFriendships.value = currentList

        currentList = _acceptedFriendships.value.toMutableList()
        currentList.add(friendWithStatus)
        _acceptedFriendships.value = currentList
    }

    fun decline(friendWithStatus: FriendWithStatus) {
        val friendship = FireStoreFriendShip(
            created = friendWithStatus.created,
            friendShipId = friendWithStatus.friendShipId,
            status = "decline",
            requester = FireStoreFriendShip.PostUser(),
            accepter = FireStoreFriendShip.PostUser()
        )
        launchCatching {
            friendRepository.declineFriend(friendship)
        }
        updateLocalLists(friendWithStatus)
    }

    private fun filterFriendAdd(acceptedFriends : List<FriendWithStatus>, pendingFriends: List<FriendWithStatus>,post: FireStoreUser) : Boolean{
        for(accepted in acceptedFriends){
            if(accepted.friend.userUID.equals(post.userUID)){
                return false
            }
        }
        for (pending in pendingFriends){
            if(pending.friend.userUID.equals(post.userUID)){
                return false
            }
        }
        return true

    }
    fun addFriend(post: FireStoreUser) {
        if(!filterFriendAdd(_acceptedFriendships.value,_pendingFriendships.value,post)){
            return
        }
        if(post.userUID == accountPreferences.getFireStoreUser().userUID){
            return
        }
        val fireStoreUser = accountPreferences.getFireStoreUser()
        val user1 = FireStoreFriendShip.PostUser(
            profilePictureUrl = fireStoreUser.profilePictureUrl,
            userUID = fireStoreUser.userUID,
            username = fireStoreUser.userName
        )
        val user2 = FireStoreFriendShip.PostUser(
            profilePictureUrl = post.profilePictureUrl,
            userUID = post.userUID,
            username = post.userName
        )
        launchCatching {
            friendRepository.addFriend(
                FireStoreFriendShip(
                    status = "pending",
                    requester = user1,
                    accepter = user2
                )
            )
            loadDataFromFireStore()
        }
    }


    private fun loadDataFromFireStore() {
        viewModelScope.launch {
            val localUID = accountPreferences.getFireStoreUser().userUID
            val accepted = mutableListOf<FriendWithStatus>()
            val pending = mutableListOf<FriendWithStatus>()
            friendRepository.getFriendsWithStatus(localUID)
                .collect { friendshipsWithStatus ->
                    for (friendsShip in friendshipsWithStatus) {
                        val requesterStatus =
                            friendsShip.requester.userUID.equals(localUID) && friendsShip.status == "pending"
                        val friend = if (friendsShip.requester.userUID.equals(localUID)) {
                            FriendWithStatus.PostUser(
                                friendsShip.accepter.profilePictureUrl,
                                friendsShip.accepter.userUID,
                                friendsShip.accepter.username
                            )
                        } else {
                            FriendWithStatus.PostUser(
                                friendsShip.requester.profilePictureUrl,
                                friendsShip.requester.userUID,
                                friendsShip.requester.username
                            )
                        }
                        val newdata = FriendWithStatus(
                            friendShipId = friendsShip.friendShipId,
                            created = friendsShip.created,
                            status = friendsShip.status,
                            requesterIsMe = requesterStatus,
                            friend = friend
                        )
                        Log.d("FRIENDS",friendsShip.friendShipId)
                        when (newdata.status) {
                            "accepted" -> {
                                accepted.add(newdata)
                            }
                            "pending" -> {
                                pending.add(newdata)
                            }
                        }
                    }
                }
            _acceptedFriendships.value = accepted
            _pendingFriendships.value = pending
        }
    }
}