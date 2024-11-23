package np.com.bimalkafle.firebaseauthdemoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _username = MutableLiveData<String>("User")
    val username: LiveData<String> get() = _username

    private val _selectedRecipe = MutableLiveData<Map<String, Any>?>()
    val selectedRecipe: LiveData<Map<String, Any>?> get() = _selectedRecipe

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated(currentUser.uid)
            loadUsername(currentUser.uid)
        }
    }

    private fun loadUsername(userId: String) {
        database.child("users").child(userId).child("username").get()
            .addOnSuccessListener { snapshot ->
                _username.value = snapshot.value as? String ?: "User"
            }
            .addOnFailureListener {
                _username.value = "User"
            }
    }

    fun setSelectedRecipe(recipe: Map<String, Any>) {
        _selectedRecipe.value = recipe
    }

    fun signupWithUsername(username: String, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = mapOf("username" to username, "email" to email)
                        database.child("users").child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Authenticated(userId)
                            }
                            .addOnFailureListener { error ->
                                _authState.value = AuthState.Error(error.message ?: "Failed to save user data")
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        _authState.value = AuthState.Authenticated(userId)
                        loadUsername(userId)
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

// Sealed class to represent authentication states
sealed class AuthState {
    data class Authenticated(val userId: String) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
