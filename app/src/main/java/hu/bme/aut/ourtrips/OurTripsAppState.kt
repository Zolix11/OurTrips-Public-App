package hu.bme.aut.ourtrips

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarManager
import hu.bme.aut.ourtrips.common.composable.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class OurTripsAppState(
    val snackbarHostState : SnackbarHostState,
    val snackbarManager: SnackbarManager,
    private val resources: Resources,
    val navController: NavController,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                snackbarHostState.showSnackbar(text)
            }
        }
    }
}