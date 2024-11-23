package np.com.bimalkafle.firebaseauthdemoapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import np.com.bimalkafle.firebaseauthdemoapp.AuthViewModel

@Composable
fun ResultPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val recipe by authViewModel.selectedRecipe.observeAsState()

    recipe?.let {
        val imageUrl = it["image_url"] as? String
        val recipeName = it["name"] as? String ?: "Unknown Recipe"
        val ingredients = it["ingredients"] as? Map<String, Map<String, String>> ?: emptyMap()
        val instructions = it["instructions"] as? String ?: "No instructions provided."

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = recipeName, fontSize = 32.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Ingredients", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            ingredients.forEach { (_, details) ->
                Text("- ${details["name"]}: ${details["quantity"]}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Instructions", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = instructions)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back to Filter")
            }
        }
    } ?: run {
        Text(text = "No recipe found.", fontSize = 20.sp)
    }
}
