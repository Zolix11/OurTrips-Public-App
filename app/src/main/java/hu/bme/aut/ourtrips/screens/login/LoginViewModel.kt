package hu.bme.aut.ourtrips.screens.login

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.ext.isValidEmail
import hu.bme.aut.ourtrips.common.composable.ext.isValidPassword
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarManager
import hu.bme.aut.ourtrips.model.repository.impl.AuthRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val authRepository: AuthRepositoryImpl
):MyTripsViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val resetPasswordEmail
        get() = uiState.value.resetPasswordEmail



    fun onEmailChange(newValue: String){
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String){
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onResetPasswordEmailChange(newValue: String){
        uiState.value=uiState.value.copy(resetPasswordEmail = newValue)
    }
    fun onForgotPassword(newValue : Boolean){
        uiState.value=uiState.value.copy(forgotPasswordDialog = newValue)

    }

    fun onLogInClick(logInNav: ()->Unit){
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(R.string.password_error)
            return
        }
        launchCatching {
            authRepository.authenticate(email,password)
            logInNav()
        }
    }



    fun sendPasswordResetEmail(){
        if(!resetPasswordEmail.isValidEmail()){
            SnackbarManager.showMessage(R.string.reset_password_error)
            return
        }
        launchCatching {
            authRepository.resetPassword(resetPasswordEmail)
        }
    }
}