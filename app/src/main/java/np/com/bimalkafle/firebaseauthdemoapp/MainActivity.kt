package np.com.bimalkafle.firebaseauthdemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import np.com.bimalkafle.firebaseauthdemoapp.ui.theme.FirebaseAuthDemoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel: AuthViewModel by viewModels()

        setContent {
            FirebaseAuthDemoAppTheme {
                Scaffold { paddingValues ->
                    MyAppNavigation(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
