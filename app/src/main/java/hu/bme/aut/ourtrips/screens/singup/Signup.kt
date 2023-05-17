package hu.bme.aut.ourtrips.screens.singup


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import hu.bme.aut.ourtrips.common.composable.*
import hu.bme.aut.ourtrips.common.composable.ext.fieldModifier
import hu.bme.aut.ourtrips.screens.destinations.HomeScreenDestination
import hu.bme.aut.ourtrips.R.string as AppText

@Destination
@Composable
fun SignupScreen(navigator: DestinationsNavigator) {
    SignUp(navigator = navigator)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(
    navigator: DestinationsNavigator, viewModel: SingUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val fieldModifier = Modifier.fieldModifier()

    Scaffold(content = { innerPadding ->
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
                text = "Sign Up",
                modifier = Modifier.wrapContentSize(),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            UserNameField(
                value = uiState.username, onNewValue = viewModel::onNameChange, fieldModifier
            )
            EmailField(
                value = uiState.email, onNewValue = viewModel::onEmailChange, fieldModifier
            )
            PasswordField(
                value = uiState.password, onNewValue = viewModel::onPasswordChange, fieldModifier
            )
            RepeatPasswordField(
                value = uiState.repeatPassword,
                onNewValue = viewModel::onRepeatPasswordChange,
                fieldModifier
            )

            BasicButton(text = AppText.signup, modifier = fieldModifier) {
                viewModel.onSignUpClick{
                    navigator.navigate(HomeScreenDestination)
                }
            }
        }
    })
}


@Preview
@Composable
fun SignUpScreenPreView() {
    SignUp(EmptyDestinationsNavigator)
}

