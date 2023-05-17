package hu.bme.aut.ourtrips.model

data class FireStoreUser(
    val userUID: String="",
    val fullName: String="",
    val userName: String="",
    val email: String="",
    val bio: String="",
    val profilePictureUrl: String="",
    var fcmToken : MutableList<String> = mutableListOf()
)
