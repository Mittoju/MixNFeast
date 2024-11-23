package np.com.bimalkafle.firebaseauthdemoapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.bimalkafle.firebaseauthdemoapp.AuthState
import np.com.bimalkafle.firebaseauthdemoapp.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class) // For TopAppBar
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Observing authState and username LiveData
    val authState by authViewModel.authState.observeAsState(AuthState.Unauthenticated)
    val username by authViewModel.username.observeAsState("User")

    // Check the authState and navigate if the user is unauthenticated
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mix N' Feast", fontSize = 24.sp) },
                actions = {
                    TextButton(onClick = { authViewModel.signout() }) {
                        Text(text = "Sign Out")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween, // Align children with spacing
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome text
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // Takes available space
                ) {
                    Text(text = "Welcome,", fontSize = 20.sp)
                    Text(text = username, fontSize = 32.sp)
                }

                // Buttons for navigation
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("recipeFilter") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(text = "Recipe Filter")
                    }

                    Button(
                        onClick = { navController.navigate("friends") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(text = "Friends")
                    }
                }
            }
        }
    )
}

