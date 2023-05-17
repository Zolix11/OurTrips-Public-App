package hu.bme.aut.ourtrips.model.repository.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import hu.bme.aut.ourtrips.model.AuthUser
import hu.bme.aut.ourtrips.model.FireStoreUser
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.AccountRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val accountPreferences: AccountPreferences,
    private val authRepository: AuthRepositoryImpl,
    ) : AccountRepository {

    private var authUser: AuthUser? = null

    var fireStoreUser = FireStoreUser()
    private val _currentFireStoreUser = MutableSharedFlow<FireStoreUser>()
    override val currentFireStoreUser: Flow<FireStoreUser>
        get() = _currentFireStoreUser.asSharedFlow().onEach { user ->
            Log.d("USER", "New user: $user")
        }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.currentAuthUser().collect { user ->
                if (user != null) {
                    authUser = user
                    collectionReference().document(authUser!!.id).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val newfireStoreUser = task.result.toObject<FireStoreUser>()
                                if (newfireStoreUser != null) {
                                    fireStoreUser = newfireStoreUser
                                    if (!newfireStoreUser.fcmToken.contains(accountPreferences.getFCMToken())) {
                                        newfireStoreUser.fcmToken.add(accountPreferences.getFCMToken())
                                        Log.d("MyFire",newfireStoreUser.fcmToken.toString())
                                        CoroutineScope(Dispatchers.IO).launch {
                                            updateUserFCMToken(accountPreferences.getFCMToken())
                                        }
                                    }
                                    accountPreferences.setFireStoreUser(newfireStoreUser)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        Log.d("URI", "emit user flow")
                                        _currentFireStoreUser.emit(newfireStoreUser)
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    override fun searchForFriend(username: String): Flow<List<FireStoreUser>> {
        val query = collectionReference()
            .whereEqualTo("userName", username)

        return flow {
            val snapshot = query.get().await()
            val users = snapshot.toObjects(FireStoreUser::class.java)
            emit(users)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateUser(fireStoreUser: FireStoreUser) {
        accountPreferences.setFireStoreUser(fireStoreUser)
        collectionReference().document(authUser!!.id).set(fireStoreUser).await()
    }

    suspend fun deleteUserFCMToken(token : String){
        fireStoreUser.fcmToken.remove(token)
        collectionReference().document(authUser!!.id).update("fcmToken",FieldValue.arrayRemove(token)).await()
    }
    suspend fun updateUserFCMToken(token: String) {
        if (authUser != null) {
            accountPreferences.setFCMToken(token)
            collectionReference().document(authUser!!.id).update("fcmToken", FieldValue.arrayUnion(token)).await()
        }
    }

    private fun collectionReference(): CollectionReference =
        fireStore.collection("users")

}