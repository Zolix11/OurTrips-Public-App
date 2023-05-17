package hu.bme.aut.ourtrips.screens.post

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.*
import hu.bme.aut.ourtrips.screens.destinations.HomeScreenDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination
@Composable
fun PostScreen(navigator: DestinationsNavigator) {
    PostScreenMain(navigator)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreenMain(
    navigator: DestinationsNavigator,
    viewModel: PostViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by remember {
        mutableStateOf(viewModel.uiState)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.value.userLocation, 15f)
    }
    val markerState = rememberMarkerState()
    val coroutineScope = rememberCoroutineScope()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.onAddPhoto(context, uri)
            viewModel.setPhotoPicker(false)
            if (uiState.value.photoHasLocationInfo) {
                coroutineScope.launch {
                    val position = LatLng(uiState.value.photoLatitude, uiState.value.photoLongitude)
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            position,
                            15f
                        )
                    )
                    markerState.position = position
                }
            } else {
                viewModel.setPickLocation(true)
            }
        } else {
            viewModel.setPhotoPicker(false)
            navigator.popBackStack(HomeScreenDestination, false, false)
        }
    }


    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val height = 30.dp
    Box {
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = height,
            sheetDragHandle = {
                DragHandle()
            },
            sheetContent = {
                Box(contentAlignment = Alignment.Center) {
                    MapView(
                        viewModel,
                        cameraPositionState,
                        markerState,
                        uiState.value,
                        coroutineScope
                    )
                }
            }) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                ImageViewWithChangeAndRemoveIcon(
                    model = uiState.value.photoUri,
                    contentDescription = "Post photo",
                    editImage = viewModel::setPhotoPicker,
                    removeImage = viewModel::removePhoto,
                )

                TextField(
                    label = {
                        Text(text = stringResource(id = R.string.description))
                    },
                    value = uiState.value.postDescription,
                    onValueChange = viewModel::onPhotoDescriptionChange
                )

                GenericActionButton(text = "Post", modifier = Modifier, onClick = {
                    Log.d("POST", uiState.value.photoUri + "ez az uri")
                    if (uiState.value.photoUri != "") {
                        if (uiState.value.photoHasLocationInfo) {
                            viewModel.setPostPhotoDialog(true)
                            viewModel.setPickLocation(false)
                        } else {
                            viewModel.setPickLocation(true)
                        }
                    } else {
                        viewModel.setPostCheckPhotoDialog(true)
                    }
                })

                if (uiState.value.PhotoPicker) {
                    pickImageLauncher.launch("image/*")
                }

                if (uiState.value.postCheckPhotoDialog) {
                    AlertDialog(onDismissRequest = {
                        viewModel.setPostCheckPhotoDialog(false)
                        viewModel.setPhotoPicker(false)
                    }) {
                        Column(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Please add a photo")
                            Button(onClick = {
                                viewModel.setPhotoPicker(true)
                                viewModel.setPostCheckPhotoDialog(false)
                            }) {
                                Text("Select photo")
                            }
                        }
                    }
                }

                if (uiState.value.pickLocation) {
                    AlertDialog(onDismissRequest = { viewModel.setPickLocation(false) }) {
                        Text(stringResource(id = R.string.setlocationalert))
                    }
                }

                if (uiState.value.postPhotoDialog) {
                    Column() {
                        ConfirmDialog(
                            onDismissRequest = viewModel::setPostPhotoDialog,
                        ) {
                            viewModel.onPostPhoto(startUpload = {
                                viewModel.setPostPhotoDialog(false)
                                viewModel.setCircularProgress(true)
                            }, popNav = { navigator.popBackStack() })
                        }
                    }

                }
                if (uiState.value.circularProgress) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MapView(
    viewModel: PostViewModel,
    cameraPositionState: CameraPositionState,
    markerState: MarkerState,
    uiState: PostUIState,
    coroutineScope: CoroutineScope
) {
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
            .fillMaxHeight(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = {
            coroutineScope.launch {
                cameraPositionState.animate(update = CameraUpdateFactory.newLatLngZoom(it, 15f))
                markerState.position = it
            }
            viewModel.setPhotoLocationInfo(true)
            viewModel.setPhotoLocation(it)
        }
    ) {
        if (uiState.photoHasLocationInfo) {
            Marker(
                state = markerState
            )
        }

    }
}



