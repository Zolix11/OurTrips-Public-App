package hu.bme.aut.ourtrips.model

import com.google.firebase.Timestamp

data class FriendWithStatus(var friendShipId : String="",
                            var created : Timestamp = Timestamp.now(),
                            var status: String,
                            var requesterIsMe : Boolean,
                            var friend : PostUser,
) {
    data class PostUser(
        var profilePictureUrl: String? = null,
        var userUID: String? = null,
        var username: String? = null
    )
}