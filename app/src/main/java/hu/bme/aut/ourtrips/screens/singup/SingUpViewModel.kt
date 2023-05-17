package hu.bme.aut.ourtrips.screens.singup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.common.composable.ext.isValidEmail
import hu.bme.aut.ourtrips.common.composable.ext.isValidPassword
import hu.bme.aut.ourtrips.common.composable.ext.passwordMatches
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarManager
import hu.bme.aut.ourtrips.model.repository.impl.AuthRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import javax.inject.Inject
import hu.bme.aut.ourtrips.R.string as AppText

@HiltViewModel
class SingUpViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
): MyTripsViewModel(){
    var uiState = mutableStateOf(SingUpUiState())
        private set

    private val userName
        get() = uiState.value.username
    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password


    fun onNameChange(newValue : String){
        uiState.value = uiState.value.copy(username = newValue)
    }

    fun onEmailChange(newValue: String){
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String){
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String){
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick(signInNav: ()-> Unit){
        if(userName.isBlank()){
            SnackbarManager.showMessage(AppText.username_error)
            return
        }
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.password_error)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            SnackbarManager.showMessage(AppText.password_match_error)
            return
        }

        launchCatching {
            authRepository.createAccount(userName, email, password).collect{
                if(it){
                    Log.d("CREATION","succes")
                    signInNav()
                }
                else{
                    Log.d("CREATION","fail")
                    SnackbarManager.showMessage(AppText.generic_error)
                }
            }
        }
    }
}