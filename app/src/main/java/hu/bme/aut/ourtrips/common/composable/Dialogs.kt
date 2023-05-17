package hu.bme.aut.ourtrips.common.composable

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.screens.profile.ProfileUiState

@Composable
fun ResetPasswordDialog(
    value: String,
    onNewValue: (String) -> Unit,
    dialogOpen: (Boolean) -> Unit,
    action: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            dialogOpen(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dialogOpen(false)
                    action()
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // close the dialog
                    dialogOpen(false)
                }
            ) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.send_reset_password_email),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
        },
        text = {
            TextField(value = value, onValueChange = { onNewValue(it) })
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        shape = RoundedCornerShape(5.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        textContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        iconContentColor = MaterialTheme.colorScheme.primary,
    )
}


@Composable
fun EditProfileDialog(
    profileUiState: ProfileUiState,
    dialogOpen: (Boolean) -> Unit,
    action: (ProfileUiState) -> Unit
) {

    val updatedProfileUiState = remember { mutableStateOf(profileUiState) }
    val maxLines = 8
    val maxChar = 256

    var maxCharText by remember {
        mutableStateOf("Bio: ")
    }

    Dialog(
        onDismissRequest = { dialogOpen(false) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier.padding(8.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 2.dp,
            shape = CardDefaults.elevatedShape
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxHeight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                TextField(value = updatedProfileUiState.value.fullName,
                    label = {
                        Text(text = stringResource(id = R.string.full_name))
                    },
                    onValueChange = {
                        updatedProfileUiState.value =
                            updatedProfileUiState.value.copy(fullName = it)
                    })
                TextField(value = updatedProfileUiState.value.userName,
                    label = {
                        Text(text = stringResource(id = R.string.username))
                    }, onValueChange = {
                        updatedProfileUiState.value =
                            updatedProfileUiState.value.copy(userName = it)
                    })
                TextField(value = updatedProfileUiState.value.bio,
                    maxLines = maxLines,
                    modifier = Modifier.defaultMinSize(),
                    label = {
                        Text(text = maxCharText)
                    },
                    onValueChange = {
                        maxCharText = "Bio: (Remaining character ${maxChar - it.length})"
                        if (it.length < maxChar) {
                            updatedProfileUiState.value =
                                updatedProfileUiState.value.copy(bio = it)
                        }
                    })

                GenericActionButton(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    onClick = {
                        action(updatedProfileUiState.value)
                        dialogOpen(false)
                    },
                    text = stringResource(id = R.string.save)
                )
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDialogWithAddFriendButton(
    userName: String,
    userUrl: String,
    closeDialog: () -> Unit,
    addUserToFriends: () -> Unit,
) {
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
                        model = Uri.parse(userUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 10))
                            .fillMaxHeight(0.7f)
                    )

                    Text(
                        text = "@$userName",
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )

                    GenericActionButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            addUserToFriends()
                            closeDialog()
                        },
                        text = stringResource(id = R.string.add_user_to_friends)
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDialog(
    userName: String,
    userUrl: String,
    closeDialog: () -> Unit,
) {
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
                        model = Uri.parse(userUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 10))
                            .fillMaxHeight(0.7f)
                    )
                    Text(
                        text = "@$userName",
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun BioDialog(bio: String, action: (Boolean) -> Unit) {
    Dialog(
        onDismissRequest = { action(false) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 2.dp,
            shape = CardDefaults.elevatedShape
        ) {
            Text(
                text = bio,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    onDismissRequest: (Boolean) -> Unit,
    action: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest(false) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {

        Surface(
            modifier = Modifier.padding(8.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 2.dp,
            shape = CardDefaults.elevatedShape
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.areyousure),
                    fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(10.dp),
                )

                DialogConfirmButton(text = R.string.confirm) {
                    action()
                }
            }
        }
    }
}