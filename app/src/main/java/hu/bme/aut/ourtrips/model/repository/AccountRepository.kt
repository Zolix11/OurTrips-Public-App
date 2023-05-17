package hu.bme.aut.ourtrips.model.repository

import hu.bme.aut.ourtrips.model.FireStoreUser
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val currentFireStoreUser : Flow<FireStoreUser>

    suspend fun updateUser(fireStoreUser : FireStoreUser)
    fun searchForFriend(username: String): Flow<List<FireStoreUser>>
}