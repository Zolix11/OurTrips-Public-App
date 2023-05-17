package hu.bme.aut.ourtrips.common.composable

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.model.FriendWithStatus

@Composable
fun AcceptedFriends(friend: FriendWithStatus) {
    var openUserDetails by remember {
        mutableStateOf(false)
    }
    if (openUserDetails) {
        UserProfileDialog(
            userName =friend.friend.username!!,
            userUrl = friend.friend.profilePictureUrl!!,
            closeDialog = {
                openUserDetails = false
            })
    }
    Card(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth()
            .clickable {
                       openUserDetails = true
            },
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

        ) {
            FriendsProfilePicture(friend.friend.profilePictureUrl!!)
            Text(
                text = friend.friend.username!!,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }
    }
}

@Composable
fun PendingFriends(friend: FriendWithStatus, accept: () -> Unit, decline: () -> Unit) {
    var openUserDetails by remember {
        mutableStateOf(false)
    }
    if (openUserDetails) {
        PendingFriendsCard(
            friend = friend,
            accept = accept,
            decline = decline,
            closeDialog = {
                openUserDetails = false
            })
    }
    Card(
        modifier = Modifier
            .padding(bottom = 5.dp)
            .fillMaxWidth()
            .clickable {
                openUserDetails = true
            },
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
                .padding(vertical = 5.dp)
                .fillMaxWidth()

        ) {
            Spacer(modifier = Modifier.padding(end = 10.dp))
            FriendsProfilePicture(friend.friend.profilePictureUrl!!)
            Spacer(modifier = Modifier.padding(end = 10.dp))
            Text(
                text = friend.friend.username!!,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
            ) {

            }
            if (!friend.requesterIsMe) {
                Icon(painter = painterResource(id = R.drawable.done__tick_48px),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable {
                            accept()
                        })
                Icon(painter = painterResource(id = R.drawable.remove_48px),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable {
                            decline()
                        })

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingFriendsCard(friend: FriendWithStatus, accept: () -> Unit, decline: () -> Unit, closeDialog : () -> Unit) {
    AlertDialog(onDismissRequest = closeDialog) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                elevation = CardDefaults.cardElevation(),
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
                    AsyncImage(
                        model = Uri.parse(friend.friend.profilePictureUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 10))
                            .fillMaxHeight(0.7f)
                    )

                    Text(
                        text = "@${friend.friend.username}",
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )

                    if (!friend.requesterIsMe) {
                        Row {
                            Icon(painter = painterResource(id = R.drawable.done__tick_48px),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .clickable {
                                        accept()
                                    })
                            Icon(painter = painterResource(id = R.drawable.remove_48px),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .padding(8.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .clickable {
                                        decline()
                                    })
                        }
                    }
                }
            }
        }
    }
}



