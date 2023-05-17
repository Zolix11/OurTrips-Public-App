package hu.bme.aut.ourtrips.screens.friends

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import hu.bme.aut.ourtrips.common.composable.AcceptedFriends
import hu.bme.aut.ourtrips.common.composable.PendingFriends
import hu.bme.aut.ourtrips.common.composable.ProfileTransitions
import hu.bme.aut.ourtrips.common.composable.SearchBar
import hu.bme.aut.ourtrips.common.composable.UserNameSearchResultCard

@Destination(style = ProfileTransitions::class)
@Composable
fun AnimatedVisibilityScope.FriendsScreen(navigator: DestinationsNavigator) {
    FriendScreenMain(navigator = navigator)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreenMain(
    navigator: DestinationsNavigator,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val uiState by remember {
        mutableStateOf(viewModel.uiState)
    }

    val acceptedFriends =
        viewModel.acceptedFriendships.collectAsState(initial = emptyList()).value.toMutableList()
    val pendingFriends = viewModel.pendingFriendships.collectAsState(initial = emptyList()).value
    val searchUserNameResult =
        viewModel.searchUserNameResult.collectAsState(initial = emptyList()).value
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        SearchBar(
            searchDisplay = uiState.value.searchText,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
            onSearchDisplayChanged = { viewModel.setSearchText(it) },
            onSearchClicked = { viewModel.searchForFriends() })

        if (uiState.value.dialogForSearchResult) {
            AlertDialog(onDismissRequest = { viewModel.setDialogForSearchResult(false) }) {
                LazyColumn(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp)
                ) {
                    items(searchUserNameResult) { result ->
                        UserNameSearchResultCard(user = result) {
                            viewModel.addFriend(result)
                        }
                    }
                }

            }
        }


        if (uiState.value.isAcceptedFriendsVisible && !uiState.value.isPendingFriendsVisible) {
            LazyColumn(modifier = Modifier.align(Alignment.Center)) {
                items(acceptedFriends) { friends ->
                    Log.d("FRIENDS", "col1")
                    AcceptedFriends(friend = friends)
                }
            }
        }
        if (uiState.value.isPendingFriendsVisible && !uiState.value.isAcceptedFriendsVisible) {
            LazyColumn(modifier = Modifier.align(Alignment.Center)) {
                items(pendingFriends) { pendingFriend ->
                    Log.d("FRIENDS", "col2")
                    PendingFriends(
                        friend = pendingFriend,
                        accept = {
                            viewModel.acceptFriend(pendingFriend)
                        },
                        decline = { viewModel.decline(pendingFriend) })
                }
            }
        }

        Row(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
                .padding(15.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50.dp)
                )
        ) {
            Button(onClick = {
                viewModel.setAcceptedFriendsList(true)
                viewModel.setPendingFriendsList(false)
            }) {
                Text(text = "Friends")
            }
            Button(onClick = {
                viewModel.setPendingFriendsList(true)
                viewModel.setAcceptedFriendsList(false)
            }) {
                Text(text = "Pending")
            }
        }
    }
}


