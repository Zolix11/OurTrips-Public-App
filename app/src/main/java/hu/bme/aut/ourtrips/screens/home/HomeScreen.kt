package hu.bme.aut.ourtrips.screens.home


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.FeedCard
import hu.bme.aut.ourtrips.common.composable.ProfileTransitions
import hu.bme.aut.ourtrips.common.composable.pulltorefresh.pullRefresh
import kotlinx.coroutines.launch


@Destination(style = ProfileTransitions::class)
@Composable
fun AnimatedVisibilityScope.HomeScreen(navigator: DestinationsNavigator) {
    HomeScreenMain(navigator = navigator)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenMain(
    navigator: DestinationsNavigator,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {

    val uiState by remember {
        mutableStateOf(viewModel.uiState)
    }
    val postListState = viewModel.friendsPosts.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val context = LocalContext.current
    val lazyState = rememberLazyListState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        viewModel.refresh()
    })

    if (uiState.value.openSheetMap) {
        ModalBottomSheet(sheetState = sheetState,
            onDismissRequest = { viewModel.setOpenSheetMap(false) }) {
            val location = LatLng(
                uiState.value.photoLatitude,
                uiState.value.photoLongitude
            )
            val mapProperties = MapProperties(
                isMyLocationEnabled = true
            )
            GoogleMap(
                uiSettings = MapUiSettings(
                    scrollGesturesEnabled = true,
                    rotationGesturesEnabled = true
                ),
                properties = mapProperties,
                modifier = Modifier
                    .fillMaxSize(),
                cameraPositionState = CameraPositionState(
                    CameraPosition.fromLatLngZoom(
                        location,
                        10f
                    )
                ),
                ) {
                Marker(
                    state = MarkerState(location),
                    title = "p",
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullState),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyState
    ) {
        items(
            count = postListState.itemCount,
            key = postListState.itemKey(),
            contentType = postListState.itemContentType(
            )
        ) { index ->
            val item = postListState[index]
            if (item != null) {
                FeedCard(userID = uiState.value.userId!!, post = item, openLocation = {
                    viewModel.setOpenSheetMap(true)
                    viewModel.setPhotoLocation(item.location)
                    coroutineScope.launch {
                        sheetState.show()
                    }
                }, likePost = {
                    viewModel.likePost(item)
                }, dislikePost = {
                    viewModel.dislikePost(item)
                })
            }
        }

    }
    if(isRefreshing){
        AlertDialog(onDismissRequest = { }) {
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullState,
            )
        }
    }

    if(uiState.value.addFriendsDialog){
        Box(modifier = Modifier.fillMaxSize()){
            AlertDialog(onDismissRequest = { viewModel.setAddFriendsDialog(false) }, modifier = Modifier
                .fillMaxSize()
                .align(
                    Alignment.Center
                )) {
                Text(stringResource(id = R.string.home_screen_to_see_more))
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {


}

