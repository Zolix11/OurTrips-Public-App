package hu.bme.aut.ourtrips.model.repository.impl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.ourtrips.model.AuthUser
import hu.bme.aut.ourtrips.model.FireStoreUser
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val accountPreferences: AccountPreferences
) :
    AuthRepository {

    override suspend fun currentAuthUser(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            CoroutineScope(Dispatchers.IO).launch {
                trySend(auth.currentUser?.let { it ->
                    AuthUser(id = it.uid, email = it.email!!)
                })
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun createAccount(
        userName: String,
        email: String,
        password: String
    ): Flow<Boolean> {
        val flow = MutableSharedFlow<Boolean>()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { user ->
                    val token = accountPreferences.getFCMToken()
                    val fireStoreUser = FireStoreUser(userName = userName, email = email, fcmToken = mutableListOf(token))
                    val docRef = fireStore.collection("users").document(user.uid)
                    val setTask = docRef.set(fireStoreUser)

                    CoroutineScope(Dispatchers.IO).launch {
                        setTask.await()
                        flow.emit(true)
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    flow.emit(false)
                }
            }
        }
        return flow.asSharedFlow()
    }


    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AUTH", "Deleted")
            }
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}