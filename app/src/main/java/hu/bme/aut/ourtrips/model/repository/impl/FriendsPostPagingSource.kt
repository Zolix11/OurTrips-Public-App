package hu.bme.aut.ourtrips.model.repository.impl

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import hu.bme.aut.ourtrips.model.FireStorePost
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendsPostPagingSource @Inject constructor(
    private val fireStore: FirebaseFirestore, private val friendsUIDs: List<String?>
) : PagingSource<QuerySnapshot, FireStorePost>() {

    private val pageSize = 5

    override fun getRefreshKey(state: PagingState<QuerySnapshot, FireStorePost>): QuerySnapshot? {
        // We need to define this method as well,
        // but we don't need it for now so we can return null
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FireStorePost> {
        Log.d("PAGEDATA",friendsUIDs.toString())

        return try {
            val query = if(friendsUIDs.isEmpty()){
                fireStore.collection("posts")
                    .orderBy("postdate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
            } else{
                fireStore.collection("posts")
                    .whereIn("user.userUID", friendsUIDs)
                    .orderBy("postdate", Query.Direction.DESCENDING)
                    .limit(pageSize.toLong())
            }

            val currentPage = params.key ?: query.get().await()

            Log.d("PAGEDATA", currentPage.documents[0].toString())
            val currentPageData = currentPage.toObjects(FireStorePost::class.java)

            val lastDocumentSnapshot = currentPage.documents.lastOrNull()
                ?: // There are no more items to load
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )

            val nextQuery = if(friendsUIDs.isEmpty()){
                fireStore.collection("posts")
                    .orderBy("postdate", Query.Direction.DESCENDING)
                    .startAfter(lastDocumentSnapshot)
                    .limit(pageSize.toLong())
            }else{
                fireStore.collection("posts")
                    .whereIn("user.userUID", friendsUIDs)
                    .orderBy("postdate", Query.Direction.DESCENDING)
                    .startAfter(lastDocumentSnapshot)
                    .limit(pageSize.toLong())
            }

            val nextPage =  nextQuery.get().await()

            LoadResult.Page(
                data = currentPageData,
                prevKey = params.key,
                nextKey = nextPage
            )

        } catch (e: Exception) {

            Log.e("POST", "Error loading page ${params.key ?: "initial"}", e)
            LoadResult.Error(e)
        }
    }

}