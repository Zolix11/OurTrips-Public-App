package hu.bme.aut.ourtrips.screens.login


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.*
import hu.bme.aut.ourtrips.common.composable.ext.fieldModifier
import hu.bme.aut.ourtrips.screens.destinations.HomeScreenDestination

@Destination
@Composable
fun AnimatedVisibilityScope.LoginScreen(navigator: DestinationsNavigator) {
    LoginScreenMain(navigator = navigator)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenMain(navigator: DestinationsNavigator, viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by remember { mutableStateOf(viewModel.uiState) }

    val fieldModifier = Modifier.fieldModifier()

    Scaffold(
        content = { innerPadding ->

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
                    text = "Log in",
                    modifier = Modifier
                        .wrapContentSize(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(100.dp))
                EmailField(
                    value = uiState.value.email, onNewValue = viewModel::onEmailChange, fieldModifier
                )
                PasswordField(
                    value = uiState.value.password,
                    onNewValue = viewModel::onPasswordChange,
                    fieldModifier
                )
                Spacer(modifier = Modifier.padding(50.dp))
                BasicButton(text = R.string.login, modifier = fieldModifier) {
                    viewModel.onLogInClick {
                        navigator.navigate(HomeScreenDestination)
                    }
                }

                BasicButton(text = R.string.forgotten_password, modifier = Modifier.scale(0.8f)) {
                    viewModel.onForgotPassword(true)
                }
                if (uiState.value.forgotPasswordDialog) {
                    ResetPasswordDialog(
                        value = uiState.value.resetPasswordEmail,
                        onNewValue = viewModel::onResetPasswordEmailChange,
                        dialogOpen = viewModel::onForgotPassword

                    ) {
                        viewModel.sendPasswordResetEmail()
                    }
                }



            }
        }
    )
}


@Preview
@Composable
fun LoginScreenPrev() {
    LoginScreenMain(navigator = EmptyDestinationsNavigator)
}

