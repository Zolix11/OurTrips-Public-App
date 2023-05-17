package hu.bme.aut.ourtrips.model.repository

import androidx.paging.PagingData
import hu.bme.aut.ourtrips.model.FireStorePost
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val createdPostState : Flow<Boolean>

    suspend fun createPost(post :FireStorePost)

    suspend fun deletePost(post : FireStorePost)
    suspend fun downLoadFriendsPosts(friendUIDs : List<String?>) : Flow<PagingData<FireStorePost>>

    suspend fun downloadUsersPosts(myUID : String) : Flow<PagingData<FireStorePost>>
    suspend fun likePost(post: FireStorePost)

    suspend fun dislikePost(post: FireStorePost)

}