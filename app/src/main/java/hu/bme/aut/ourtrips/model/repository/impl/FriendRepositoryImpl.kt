package hu.bme.aut.ourtrips.model.repository.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.ourtrips.model.FireStoreFriendShip
import hu.bme.aut.ourtrips.model.repository.FriendRepostiory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
) : FriendRepostiory {

    override suspend fun addFriend(friendShip: FireStoreFriendShip) {
        collectionReference().add(friendShip).await()
    }

    override suspend fun acceptFriend(friendShip: FireStoreFriendShip) {
        Log.d("FRIENDS", friendShip.toString())
        val id = friendShip.friendShipId
        collectionReference().document(id).set(friendShip).await()
    }

    override suspend fun declineFriend(friendShip: FireStoreFriendShip) {
        val id = friendShip.friendShipId
        collectionReference().document(id).set(friendShip).await()
    }

    fun getFriends(userId: String): Flow<List<FireStoreFriendShip>> {
        val query1 = collectionReference()
            .whereEqualTo("accepter.userUID", userId)
            .whereEqualTo("status", "accepted")
        val query2 = collectionReference()
            .whereEqualTo("requester.userUID", userId)
            .whereEqualTo("status", "accepted")

        return flow {
            val snapshot1 = query1.get().await()
            val snapshot2 = query2.get().await()
            val friendships = snapshot1.toObjects(FireStoreFriendShip::class.java)
            friendships.addAll(snapshot2.toObjects(FireStoreFriendShip::class.java))
            emit(friendships)
        }.flowOn(Dispatchers.IO)
    }

    override fun getFriendsWithStatus(userId: String): Flow<List<FireStoreFriendShip>> {
        Log.d("FRIENDS", userId)
        val query1 = collectionReference()
            .whereEqualTo("accepter.userUID", userId)

        val query2 = collectionReference()
            .whereEqualTo("requester.userUID", userId)


        return flow {
            val snapshot1 = query1.get().await()
            val snapshot2 = query2.get().await()
            val friendships = snapshot1.toObjects(FireStoreFriendShip::class.java)
            friendships.addAll(snapshot2.toObjects(FireStoreFriendShip::class.java))
            emit(friendships)
        }.flowOn(Dispatchers.IO)
    }


    private fun collectionReference(): CollectionReference =
        fireStore.collection("friendships")

}