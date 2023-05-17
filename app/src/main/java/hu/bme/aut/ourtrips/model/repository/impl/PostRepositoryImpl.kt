package hu.bme.aut.ourtrips.model.repository.impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PostRepository {


    private val authUID = auth.currentUser?.uid
    private val uploadedPostState = MutableSharedFlow<Boolean>()

    override val createdPostState: Flow<Boolean>
        get() = uploadedPostState.asSharedFlow()

    override suspend fun createPost(post: FireStorePost) {
        collectionReference().add(post).addOnCompleteListener {
            CoroutineScope(Dispatchers.IO).launch {
                uploadedPostState.emit(true)
            }
        }.addOnFailureListener {
            CoroutineScope(Dispatchers.IO).launch {
                uploadedPostState.emit(false)
            }
        }
    }

    override suspend fun deletePost(post: FireStorePost) {
        collectionReference().document(post.postId).delete().await()
    }
    override suspend fun downLoadFriendsPosts(friendUIDs: List<String?>): Flow<PagingData<FireStorePost>> {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        Log.d("PAGEDATA", friendUIDs.toString())
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { FriendsPostPagingSource(fireStore, friendUIDs) }
        ).flow.cachedIn(coroutineScope)
    }
    override suspend fun downloadUsersPosts(myUID: String): Flow<PagingData<FireStorePost>> {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { FriendsPostPagingSource(fireStore, listOf(myUID)) }
        ).flow.cachedIn(coroutineScope)
    }
    override suspend fun likePost(post: FireStorePost) {
        val postDocRef = collectionReference().document(post.postId)
        postDocRef.update("likes", FieldValue.arrayUnion(authUID)).await()
    }
    override suspend fun dislikePost(post: FireStorePost) {
        val postDocRef = collectionReference().document(post.postId)
        postDocRef.update("likes", FieldValue.arrayRemove(authUID)).await()
    }
    private fun collectionReference(): CollectionReference =
        fireStore.collection("posts")

}