package hu.bme.aut.ourtrips.screens.profile

data class ProfileUiState constructor(
    var fullName: String="",
    var userName: String="",
    var email: String="",
    var bio: String="",
    var profilePictureUri: String="",
    var profileIsLoading : Boolean=true,
    var editProfileDialog : Boolean=false,
    var newProfilePicturePicker : Boolean=false,
)

