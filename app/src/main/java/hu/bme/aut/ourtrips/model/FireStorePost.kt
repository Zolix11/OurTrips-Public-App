package hu.bme.aut.ourtrips.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import kotlin.random.Random

data class FireStorePost(

var postId: String,
    var description: String = "",
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    var photo_url: String? = null,
    var postdate: Timestamp = Timestamp.now(),
    var user: PostUser = PostUser(),
    var likes: List<String> = listOf()
) {
    data class PostUser(
        var profilePictureUrl: String? = null,
        var userUID: String? = null,
        var username: String? = null
    )
    constructor() : this(postId ="", description = "",location= GeoPoint(47.46267+ Random.nextFloat(),19.029022 + Random.nextFloat()), photo_url = "https://firebasestorage.googleapis.com/v0/b/flash-spot-355510.appspot.com/o/posts%2F20230427_000801-4f440934-1ac0-4d68-ab43-e23ac2a9a1e2.jpg?alt=media&token=29b87249-d413-456e-a9c5-61f44ff6284c", postdate = Timestamp.now(), user = PostUser(profilePictureUrl = "",userUID = "", username = "Zolix"), likes = listOf())

}


