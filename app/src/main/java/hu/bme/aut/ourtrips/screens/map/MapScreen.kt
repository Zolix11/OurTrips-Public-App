package hu.bme.aut.ourtrips.screens.map

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import hu.bme.aut.ourtrips.common.composable.CustomClusterItemPointer
import hu.bme.aut.ourtrips.common.composable.CustomClusterItemsPointer
import hu.bme.aut.ourtrips.common.composable.ExpandableSearchView
import hu.bme.aut.ourtrips.common.composable.FeedCard
import hu.bme.aut.ourtrips.common.composable.NoPostsFound
import hu.bme.aut.ourtrips.common.composable.ProfileTransitions
import hu.bme.aut.ourtrips.common.composable.SearchResult
import hu.bme.aut.ourtrips.common.composable.ext.toLatLng
import kotlinx.coroutines.launch


@Destination(style = ProfileTransitions::class)
@Composable
fun MapScreen(navigator: DestinationsNavigator) {
    MapScreenMain(navigator)
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun MapScreenMain(
    navigator: DestinationsNavigator,
    viewModel: MapScreenViewModel = hiltViewModel(),
) {
    val uiState by remember {
        mutableStateOf(viewModel.uiState)
    }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val height = 30.dp
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.value.userLocation, 15f)
    }
    val feedItems = viewModel.feedpost.collectAsState(initial = emptyList()).value
    val items = remember { mutableStateListOf<CustomClusterItem>() }
    LaunchedEffect(Unit) {
        viewModel.post.collect { list ->
            for (item in list) {
                items.add(
                    CustomClusterItem(
                        fireStorePost = item,
                        item.location.toLatLng(),
                        "Post by: "+item.user.username!!,
                        "♡"+item.likes.size.toString(),
                    )
                )
            }
        }

    }

    if (!cameraPositionState.isMoving) {
        if (uiState.value.southwest != null && uiState.value.northeast != null) {
            viewModel.setSearchBounds(
                cameraPositionState.projection?.visibleRegion?.latLngBounds?.southwest,
                cameraPositionState.projection?.visibleRegion?.latLngBounds?.northeast
            )
            viewModel.performSearchForPostsOnScreen()
        }
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = height,
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = lazyListState
            ) {
                item {
                    if (feedItems.isEmpty()) {
                        NoPostsFound()
                    }
                }
                items(
                    feedItems,
                ) { post->
                    FeedCard(
                        post =post,
                        userID =post.postId ,
                        openLocation = {
                            coroutineScope.launch {
                                cameraPositionState.animate(update = CameraUpdateFactory.newLatLngZoom(post.location.toLatLng(),15f))
                                bottomSheetScaffoldState.bottomSheetState.partialExpand()
                            }
                        } ,
                        likePost = {    viewModel.likePost(post) },
                        dislikePost = {
                            viewModel.dislikePost(post)
                        })
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMapClustering(
                items = items,
                cameraPositionState = cameraPositionState,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun GoogleMapClustering(
    items: SnapshotStateList<CustomClusterItem>,
    cameraPositionState: CameraPositionState,
    viewModel: MapScreenViewModel,
) {
    GoogleMap(
        uiSettings = MapUiSettings(
            scrollGesturesEnabled = true,
            rotationGesturesEnabled = true
        ),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true
        ),
        onMapLoaded = {
            viewModel.setSearchBounds(
                cameraPositionState.projection?.visibleRegion?.latLngBounds?.southwest,
                cameraPositionState.projection?.visibleRegion?.latLngBounds?.northeast
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) {

        Clustering(
            items = items,
            // Optional: Handle clicks on clusters, cluster items, and cluster item info windows
            onClusterClick = {
                Log.d(TAG, "Cluster clicked! $it")
                false
            },
            onClusterItemClick = {
                Log.d(TAG, "Cluster item clicked! $it")
                false
            },
            onClusterItemInfoWindowClick = {
                Log.d(TAG, "Cluster item info window clicked! $it")
            },
            clusterContent = { cluster ->
                Log.d(TAG, "clusterContent")
                CustomClusterItemsPointer(cluster)
            },
            clusterItemContent = {
                CustomClusterItemPointer()
            }


        )
    }
}


private val TAG = "Cluter"


