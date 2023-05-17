package hu.bme.aut.ourtrips.model

import com.google.firebase.Timestamp

data class FireStoreFriendShip(
    var friendShipId : String="",
    var created : Timestamp= Timestamp.now(),
    var status: String,
    var requester : PostUser,
    var accepter : PostUser
) {
    constructor() : this("", Timestamp.now(), "", PostUser(), PostUser())

    data class PostUser(
        var profilePictureUrl: String? = null,
        var userUID: String? = null,
        var username: String? = null
    )
}
