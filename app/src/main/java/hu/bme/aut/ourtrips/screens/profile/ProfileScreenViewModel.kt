package hu.bme.aut.ourtrips.screens.profile

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.ourtrips.model.FireStoreUser
import hu.bme.aut.ourtrips.model.locationutils.AccountPreferences
import hu.bme.aut.ourtrips.model.repository.impl.AccountRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.AuthRepositoryImpl
import hu.bme.aut.ourtrips.model.repository.impl.StorageForProfileRepositoryImp
import hu.bme.aut.ourtrips.screens.MyTripsViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepositoryImpl,
    private val accountRepository: AccountRepositoryImpl,
    private val storageRepository: StorageForProfileRepositoryImp,
    private val accountPreferences: AccountPreferences,
) : MyTripsViewModel() {

    var uiState = mutableStateOf(ProfileUiState())

    init {
        launchCatching{
            accountRepository.currentFireStoreUser.collect { user ->
                uiState.value = ProfileUiState(
                    fullName = user.fullName,
                    userName = user.userName,
                    email = user.email,
                    bio = user.bio,
                    profilePictureUri = user.profilePictureUrl,
                    editProfileDialog = false,
                    profileIsLoading = false,
                )
            }

        }
    }

    fun onUpdateProfilePicture(uri: Uri) {
        launchCatching {
            storageRepository.upLoadProfileImage(uri = uri)
            val userUid = accountPreferences.getFireStoreUser().userUID
            storageRepository.currentProfilePictureUri.collect {newUri->
                uiState.value = uiState.value.copy(profilePictureUri = newUri.toString())
                accountRepository.updateUser(FireStoreUser(
                    fullName = uiState.value.fullName,
                    userName = uiState.value.userName,
                    email = uiState.value.email,
                    bio = uiState.value.bio,
                    profilePictureUrl = uiState.value.profilePictureUri,
                    userUID = userUid
                )
                )
            }
        }
    }

    fun onEditDialog(newValue: Boolean) {
        uiState.value = uiState.value.copy(editProfileDialog = newValue)
    }

    fun onEditProfilePicture(newValue: Boolean) {
        uiState.value = uiState.value.copy(newProfilePicturePicker = newValue)
    }

    fun onUpdateUser(newProfile: ProfileUiState) {
        if (uiState.value == newProfile) {
            return
        }
        val userUid = accountPreferences.getFireStoreUser().userUID
        uiState.value = newProfile
        launchCatching {
            accountRepository.updateUser(
                FireStoreUser(
                    fullName = newProfile.fullName,
                    userName = newProfile.userName,
                    email = newProfile.email,
                    bio = newProfile.bio,
                    profilePictureUrl = newProfile.profilePictureUri,
                    userUID = userUid
                )
            )
        }
    }


    fun signOutUser(signOutNav: () -> Unit) {
        launchCatching {
            accountRepository.deleteUserFCMToken(accountPreferences.getFCMToken())
            authRepository.signOut()
            accountPreferences.deleteFireStoreUser()
            signOutNav()
        }
    }

}