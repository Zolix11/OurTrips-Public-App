package hu.bme.aut.ourtrips.model.repository.impl

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.repository.MapPostRepostiory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MapPostRepostioryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore
): MapPostRepostiory  {


    private val _mapPosts = MutableStateFlow<List<FireStorePost>>(emptyList())
    val mapPosts: Flow<List<FireStorePost>> = _mapPosts.asStateFlow()

    override suspend fun fetchPostsOnMapScreen(
        southwest: LatLng,
        northeast: LatLng
    ){
        val bottomLeft = GeoPoint(southwest.latitude,southwest.longitude)
        val topRight = GeoPoint(northeast.latitude, northeast.longitude)

        val query = collectionReference().whereGreaterThan("location", bottomLeft)
            .whereLessThan("location", topRight)

        val snapshot = query.get().await()
        val posts = snapshot.toObjects(FireStorePost::class.java)
        _mapPosts.value = posts
    }

    private fun collectionReference(): CollectionReference =
        fireStore.collection("posts")


}