package np.com.bimalkafle.firebaseauthdemoapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FriendsPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Friends",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Placeholder for friends list or options
        Text(
            text = "Friends list coming soon!",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Button to go back to the HomePage
        TextButton(onClick = { navController.popBackStack() }) {
            Text(text = "Back to Home")
        }
    }
}
