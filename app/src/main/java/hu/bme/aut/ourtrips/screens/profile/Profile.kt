package hu.bme.aut.ourtrips.screens.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.EditProfileDialog
import hu.bme.aut.ourtrips.common.composable.ProfileButton
import hu.bme.aut.ourtrips.common.composable.ProfileCard
import hu.bme.aut.ourtrips.common.composable.ProfileTransitions
import hu.bme.aut.ourtrips.screens.destinations.FriendsScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.MyPostsScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.WelcomeDestination

@Destination(style = ProfileTransitions::class)
@Composable
fun ProfileScreen(navigator: DestinationsNavigator) {
    ProfileScreenMain(navigator = navigator)
}

@Composable
fun ProfileScreenMain(
    navigator: DestinationsNavigator,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val uiState by remember {
        mutableStateOf(viewModel.uiState)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.onEditProfilePicture(false)
            if (uri != null) {
                Log.d("URI",uri.toString())
                viewModel.onUpdateProfilePicture(uri)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {

        if (uiState.value.editProfileDialog) {
            EditProfileDialog(
                uiState.value,
                dialogOpen = viewModel::onEditDialog,
                action = viewModel::onUpdateUser
            )
        }
        if (!uiState.value.profileIsLoading) {
            ProfileCard(
                uiState.value,
                editImage = viewModel::onEditProfilePicture,
            ) {
                viewModel.onEditDialog(true)
            }
        }
        else {
            CircularProgressIndicator()
        }

        if (uiState.value.newProfilePicturePicker) {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().wrapContentSize()) {

            ProfileButton(
                text = R.string.friends,
                modifier = Modifier,
                R.drawable.group_48px,
                "Friends"
            ) {
                navigator.navigate(FriendsScreenDestination)
            }
            ProfileButton(
                text = R.string.profile_posts,
                modifier = Modifier,
                R.drawable.photo_library_48px,
                "Posts"
            ) {
                navigator.navigate(MyPostsScreenDestination)
            }
            ProfileButton(
                text = R.string.log_out,
                modifier = Modifier,
                R.drawable.logout_48px,
                "Log out"
            ) {
                viewModel.signOutUser {
                    navigator.popBackStack(WelcomeDestination, inclusive = false, false)
                }
            }
        }


    }
}
