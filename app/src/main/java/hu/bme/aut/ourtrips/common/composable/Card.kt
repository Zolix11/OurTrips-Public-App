package hu.bme.aut.ourtrips.common.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.model.FireStorePost
import hu.bme.aut.ourtrips.model.FireStoreUser
import hu.bme.aut.ourtrips.screens.profile.ProfileUiState

@Composable
fun ProfileCard(
    profileUiState: ProfileUiState,
    editImage: (Boolean) -> Unit,
    editProfile: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            var viewableBio by remember {
                mutableStateOf(false)
            }

            if (viewableBio) {
                BioDialog(bio = profileUiState.bio) {
                    viewableBio = false
                }
            }

            ImageViewWithChangeIcon(
                model = profileUiState.profilePictureUri,
                contentDescription = "Profile pic",
                editImage = editImage,
            )

            if (profileUiState.fullName.isNotEmpty()) {
                Text(
                    text = profileUiState.fullName,
                    fontFamily = MaterialTheme.typography.headlineMedium.fontFamily,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "@" + profileUiState.userName,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
            )

            GenericActionButton(
                modifier = Modifier.fillMaxWidth(0.2f),
                onClick = { viewableBio = !viewableBio },
                text = stringResource(id = R.string.bio)
            )
            GenericActionButton(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = { editProfile() },
                text = stringResource(id = R.string.profile_edit)
            )


        }

    }
}
@Composable
fun FeedCard(
    post: FireStorePost,
    userID: String,
    openLocation: (Boolean) -> Unit,
    likePost: () -> Unit,
    dislikePost: () -> Unit
) {

    var openUserDetails by remember {
        mutableStateOf(false)
    }
    if (openUserDetails) {
        UserProfileDialog(
            post.user.username!!,
            post.user.profilePictureUrl!!,
            closeDialog = { openUserDetails = false },
        )
    }
    Card(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 10.dp)
                    .fillMaxWidth()
                    .clickable {
                        openUserDetails = true
                    }

            ) {
                FeedProfilePicture(post.user.profilePictureUrl!!)
                Text(text = post.user.username!!)
            }
            FeedImageViewWithLocationIcon(
                model = post.photo_url!!,
                contentDescription = "postphotourl",
                openLocation = openLocation
            )
            if(post.description.isNotEmpty()){
                Text(
                    text = post.description,
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            PostCardBottomDetails(
                isLiked = post.likes.contains(userID),
                likePost = likePost,
                dislikePost = dislikePost,
                post = post
            )
        }
    }
}

@Composable
fun MyPostsFeedCard(
    post: FireStorePost,
    userID: String,
    openLocation: (Boolean) -> Unit,
    deletePost: () -> Unit
) {
    var openDeleteDialog by remember {
        mutableStateOf(false)
    }
    var openDeleted by remember {
        mutableStateOf(false)
    }

    if (openDeleteDialog) {
        ConfirmDialog(onDismissRequest = { openDeleteDialog = false }) {
            deletePost()
            openDeleted = !openDeleted
            openDeleteDialog = !openDeleteDialog
        }
    }

    if (!openDeleted) {
        Card(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
            colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(bottom = 10.dp, top = 10.dp)
                        .fillMaxWidth()

                ) {
                    FeedProfilePicture(post.user.profilePictureUrl!!)
                    Icon(
                        painter = painterResource(id = R.drawable.delete_48px),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .clickable {
                                openDeleteDialog = !openDeleteDialog
                            }
                    )

                }
                FeedImageViewWithLocationIcon(
                    model = post.photo_url!!,
                    contentDescription = "postphotourl",
                    openLocation = openLocation
                )
                PostCardBottomDetails(
                    isLiked = post.likes.contains(userID),
                    likePost = {},
                    dislikePost = { },
                    post = post
                )
            }
        }
    }

}

@Composable
fun UserNameSearchResultCard(user: FireStoreUser, addUserToFriends: () -> Unit) {
    var openUserDetails by remember {
        mutableStateOf(false)
    }
    if (openUserDetails) {
        UserProfileDialogWithAddFriendButton(
            user.userName,
            user.profilePictureUrl,
            addUserToFriends = addUserToFriends,
            closeDialog = { openUserDetails = false },
        )
    }
    Card(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .padding(bottom = 10.dp, top = 10.dp)
                .fillMaxWidth()
                .clickable {
                    openUserDetails = true
                }

        ) {
            FriendsProfilePicture(user.profilePictureUrl)
            Text(text = user.userName, fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        }
    }
}

