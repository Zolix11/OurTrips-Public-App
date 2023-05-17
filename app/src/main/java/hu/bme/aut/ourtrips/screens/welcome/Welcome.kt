package hu.bme.aut.ourtrips.screens.welcome

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import hu.bme.aut.ourtrips.common.composable.BasicButton
import hu.bme.aut.ourtrips.common.composable.ProfileTransitions
import hu.bme.aut.ourtrips.common.composable.ext.fieldModifier
import hu.bme.aut.ourtrips.screens.destinations.LoginScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.ProfileScreenDestination
import hu.bme.aut.ourtrips.screens.destinations.SignupScreenDestination
import hu.bme.aut.ourtrips.R.string as AppText

@RootNavGraph(start = true)
@Destination(style = ProfileTransitions::class)
@Composable
fun AnimatedVisibilityScope.Welcome(navigator: DestinationsNavigator) {
    WelcomeScreen(navigator = navigator)
}


@Composable
fun WelcomeScreen(navigator: DestinationsNavigator, viewModel: WelcomeViewModel = hiltViewModel()) {


    Scaffold(
        content = { innerPadding ->
            viewModel.navigateIfUserLoggedIn {
                navigator.navigate(ProfileScreenDestination)
            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 50.dp, vertical = 25.dp)
                    .background(color = MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                BasicButton(text = AppText.login, modifier = Modifier.fieldModifier()) {
                    navigator.navigate(LoginScreenDestination)
                }
                BasicButton(text = AppText.signup, modifier = Modifier.fieldModifier()) {
                    navigator.navigate(SignupScreenDestination)
                }
            }
        })
}

@Preview
@Composable
fun WelcomePreview() {
    WelcomeScreen(navigator = EmptyDestinationsNavigator)
}