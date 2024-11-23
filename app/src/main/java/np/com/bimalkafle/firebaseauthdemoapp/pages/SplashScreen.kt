package np.com.bimalkafle.firebaseauthdemoapp.pages

import android.view.LayoutInflater
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import np.com.bimalkafle.firebaseauthdemoapp.R


@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    // Navigate to "login" after a delay
    LaunchedEffect(Unit) {
        delay(1500) // 3 seconds delay
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Use the logo.xml layout
    AndroidView(
        factory = {LayoutInflater.from(it).inflate(R.layout.logo, null) },
        modifier = modifier
    )
}
