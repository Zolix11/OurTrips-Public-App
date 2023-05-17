package hu.bme.aut.ourtrips.screens.login

data class LoginUiState(
    val email : String = "",
    val password : String ="",
    val resetPasswordEmail : String="",
    val forgotPasswordDialog : Boolean=false
)
