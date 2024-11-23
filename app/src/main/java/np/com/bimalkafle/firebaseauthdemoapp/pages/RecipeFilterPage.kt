package np.com.bimalkafle.firebaseauthdemoapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*
import np.com.bimalkafle.firebaseauthdemoapp.AuthViewModel

@Composable
fun RecipeFilterPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val database = FirebaseDatabase.getInstance().reference

    var ingredientQuery by remember { mutableStateOf("") }
    var ingredientSuggestions by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var selectedIngredients by remember { mutableStateOf(mutableListOf<Pair<String, String>>()) }
    var cultureOptions by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var selectedCultureId by remember { mutableStateOf<String?>(null) }
    var matchingRecipes by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    val context = LocalContext.current

    // Fetch ingredients and cultures from Firebase
    LaunchedEffect(Unit) {
        database.child("ingredients").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ingredientSuggestions = snapshot.children.mapNotNull {
                    it.key?.let { id -> id to (it.child("name").value as? String ?: "Unknown") }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading ingredients: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        database.child("culture").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cultureOptions = snapshot.children.mapNotNull {
                    it.key?.let { id -> id to (it.child("name").value as? String ?: "Unknown") }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error loading cultures: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recipe Filter", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Input field for ingredient search
        OutlinedTextField(
            value = ingredientQuery,
            onValueChange = { query ->
                ingredientQuery = query
                ingredientSuggestions = ingredientSuggestions.filter {
                    it.second.contains(query, ignoreCase = true)
                }
            },
            label = { Text("Search Ingredients") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display ingredient suggestions
        Text(text = "All Ingredients", fontSize = 20.sp)
        ingredientSuggestions.forEach { (id, name) ->
            Button(
                onClick = {
                    if (!selectedIngredients.any { it.first == id }) {
                        selectedIngredients.add(id to name)
                    }
                    ingredientQuery = ""
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected ingredients
        Text(text = "Selected Ingredients: ${selectedIngredients.joinToString(", ") { it.second }}")

        Spacer(modifier = Modifier.height(16.dp))

        // Culture dropdown
        Text(text = "Select a Culture", fontSize = 20.sp)
        cultureOptions.forEach { (id, name) ->
            Button(
                onClick = { selectedCultureId = id },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = name)
            }
        }

        Text(text = "Selected Culture: ${cultureOptions.find { it.first == selectedCultureId }?.second ?: "None"}")

        Spacer(modifier = Modifier.height(16.dp))

        // Search button
        Button(
            onClick = {
                if (selectedCultureId != null && selectedIngredients.isNotEmpty()) {
                    database.child("recipes").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val recipes = snapshot.children.filter { recipeSnapshot ->
                                val recipeCultureId = recipeSnapshot.child("culture").children.firstOrNull()?.key
                                val recipeIngredients = recipeSnapshot.child("ingredients").children.mapNotNull { it.key }
                                selectedCultureId == recipeCultureId &&
                                        selectedIngredients.any { recipeIngredients.contains(it.first) }
                            }.mapNotNull { recipeSnapshot ->
                                recipeSnapshot.value as? Map<String, Any>
                            }

                            matchingRecipes = recipes

                            if (recipes.isEmpty()) {
                                Toast.makeText(context, "No matching recipes found.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(context, "Select a culture and at least one ingredient.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Recipes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display matching recipes
        Text(text = "Matching Recipes", fontSize = 24.sp)
        matchingRecipes.forEach { recipe ->
            val recipeName = recipe["name"] as? String ?: "Unknown Recipe"
            Button(
                onClick = {
                    authViewModel.setSelectedRecipe(recipe)
                    navController.navigate("result")
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(text = recipeName)
            }
        }
    }
}
