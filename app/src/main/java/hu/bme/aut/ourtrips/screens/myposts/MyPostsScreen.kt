package hu.bme.aut.ourtrips.screens.myposts

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import hu.bme.aut.ourtrips.common.composable.MyPostsFeedCard
import hu.bme.aut.ourtrips.common.composable.ProfileTransitions
import kotlinx.coroutines.launch

@Destination(style = ProfileTransitions::class)
@Composable
fun AnimatedVisibilityScope.MyPostsScreen(navigator: DestinationsNavigator) {
    MyPostScreenMain(navigator = navigator)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostScreenMain(
    navigator: DestinationsNavigator,
    viewModel: MyPostsScreenViewModel = hiltViewModel()
) {

    val uiState by remember {
        mutableStateOf(viewModel.uiState)
    }
    val postListState = viewModel.myPosts.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val lazyState = rememberLazyListState()
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
                Marker(state = MarkerState(location))
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyState,
        content = {
            items(
                count = postListState.itemCount,
                key = postListState.itemKey(),
                contentType = postListState.itemContentType(
                )
            ) { index ->
                val item = postListState[index]
                if (item != null) {
                    MyPostsFeedCard(userID = uiState.value.userId!!, post = item,
                        deletePost = {
                            viewModel.deletePost(item)
                        }, openLocation = {
                            viewModel.setOpenSheetMap(true)
                            viewModel.setPhotoLocation(item.location)
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        })
                }

            }

        })

}