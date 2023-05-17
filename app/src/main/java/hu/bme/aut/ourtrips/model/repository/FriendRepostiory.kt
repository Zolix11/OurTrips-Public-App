package hu.bme.aut.ourtrips.model.repository

import hu.bme.aut.ourtrips.model.FireStoreFriendShip
import kotlinx.coroutines.flow.Flow

interface FriendRepostiory {

    suspend fun addFriend(friendShip: FireStoreFriendShip)
    suspend fun acceptFriend(friendShip: FireStoreFriendShip)
    suspend fun declineFriend(friendShip: FireStoreFriendShip)
    fun getFriendsWithStatus(userId: String) : Flow<List<FireStoreFriendShip>>
}