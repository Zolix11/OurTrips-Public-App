package hu.bme.aut.ourtrips.common.composable.bottomnav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.screens.NavGraphs
import hu.bme.aut.ourtrips.screens.appCurrentDestinationAsState
import hu.bme.aut.ourtrips.screens.destinations.Destination
import hu.bme.aut.ourtrips.screens.destinations.HomeScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.MapScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.PostScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.ProfileScreenDestination
import hu.bme.aut.ourtrips.screens.startAppDestination

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: Int,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination, R.drawable.ic_home, R.string.home_screen),
    Map(MapScreenDestination, R.drawable.map_48px, R.string.map_screen),
    Post(PostScreenDestination, R.drawable.add_photo_alternate_48px, R.string.post_screen),
    Profile(ProfileScreenDestination, R.drawable.account_circle_48px, R.string.profile_screen),
}

@Composable
fun MyBottomBar(
    navController: NavController
) {
    val currentDestination: Destination? = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination


    NavigationBar(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        tonalElevation = NavigationBarDefaults.Elevation,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    if (currentDestination != destination.direction) {
                        navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                            launchSingleTop = true
                        })
                    }
                },
                icon = {
                    Icon(
                        painterResource(destination.icon),
                        contentDescription = stringResource(destination.label),
                        modifier = Modifier.fillMaxSize(0.4f),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                },
                label = { Text(stringResource(destination.label), color = MaterialTheme.colorScheme.onSecondaryContainer) },
            )
        }
    }
}



