package hu.bme.aut.ourtrips.screens.welcome

import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.model.repository.impl.AuthRepositoryImpl
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import javax.inject.Inject


@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
) : MyTripsViewModel() {
    fun navigateIfUserLoggedIn(alreadyLoggedIn: () -> Unit){
        launchCatching {
            authRepository.currentAuthUser().collect{user ->
                if(user != null){
                    alreadyLoggedIn()
                }
            }
        }
    }
}