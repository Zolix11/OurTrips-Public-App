package hu.bme.aut.ourtrips

import android.content.res.Resources
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import hu.bme.aut.ourtrips.common.composable.bottomnav.MyBottomBar
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarManager
import hu.bme.aut.ourtrips.screens.NavGraphs
import hu.bme.aut.ourtrips.screens.appCurrentDestinationAsState
import hu.bme.aut.ourtrips.screens.destinations.*
import hu.bme.aut.ourtrips.screens.startAppDestination
import hu.bme.aut.ourtrips.ui.theme.OurTripsTheme
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun OurTripsApp() {
    OurTripsTheme {
        val appState = rememberAppState()
        val currentDestination: Destination = appState.navController.appCurrentDestinationAsState().value
            ?: NavGraphs.root.startAppDestination

        fun showBottomBar() = when (currentDestination.route) {
            WelcomeDestination.route->false
            LoginScreenDestination.route -> false
            SignupScreenDestination.route -> false
            else -> true
        }

        Scaffold(
            bottomBar = { if (showBottomBar()) MyBottomBar(navController = appState.navController) },
            snackbarHost = {
                SnackbarHost(
                    hostState = appState.snackbarHostState,
                    modifier = Modifier.padding(8.dp),
                    snackbar = { snackbarData ->
                        Snackbar(snackbarData, contentColor = MaterialTheme.colorScheme.onPrimary)
                    }
                )
            },
        ) { innerPaddingModifier ->

            val bottomSheetNavigator = rememberBottomSheetNavigator()
            appState.navController.navigatorProvider.addNavigator(bottomSheetNavigator)

            ModalBottomSheetLayout(
                bottomSheetNavigator = bottomSheetNavigator,
                sheetShape = RoundedCornerShape(16.dp),
            ) {
                DestinationsNavHost(
                    navController = appState.navController as NavHostController,
                    engine = rememberAnimatedNavHostEngine(),
                    navGraph = NavGraphs.root,
                    modifier = Modifier.padding(innerPaddingModifier)
                )
            }

        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    navController: NavController = rememberAnimatedNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, snackbarManager, resources, navController, coroutineScope) {
        OurTripsAppState(
            snackbarHostState,
            snackbarManager,
            resources,
            navController,
            coroutineScope
        )
    }


@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}