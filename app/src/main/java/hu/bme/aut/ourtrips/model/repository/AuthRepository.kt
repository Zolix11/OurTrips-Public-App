package hu.bme.aut.ourtrips.model.repository

import hu.bme.aut.ourtrips.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun currentAuthUser(): Flow<AuthUser?>
    suspend fun authenticate(email: String, password: String)
    suspend fun createAccount(userName : String ,email: String, password: String) : Flow<Boolean>

    suspend fun resetPassword(email: String)
    suspend fun deleteAccount()
    suspend fun signOut()
}