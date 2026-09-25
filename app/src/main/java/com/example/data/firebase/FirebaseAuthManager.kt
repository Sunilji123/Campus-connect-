package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {
  private const val TAG = "FirebaseAuthManager"
  private val scope = CoroutineScope(Dispatchers.Main)

  private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

  private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
  val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _authSuccessMessage = MutableStateFlow<String?>(null)
  val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

  private var onUserSignedInCallback: ((FirebaseUser) -> Unit)? = null

  fun setOnUserSignedInCallback(callback: (FirebaseUser) -> Unit) {
    onUserSignedInCallback = callback
  }

  fun initialize(context: Context) {
    try {
      if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
      }
    } catch (e: Exception) {
      Log.w(TAG, "FirebaseApp initialization check: ${e.message}")
    }

    _currentUser.value = auth.currentUser

    auth.addAuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      _currentUser.value = user
      if (user != null) {
        onUserSignedInCallback?.invoke(user)
      }
    }
  }

  fun clearError() {
    _errorMessage.value = null
  }

  fun clearSuccessMessage() {
    _authSuccessMessage.value = null
  }

  /**
   * Interactive Sign-In with Google using Credential Manager.
   * In accordance with Firebase setup invariants:
   * 1. GetCredentialRequest carries EXACTLY ONE option: GetSignInWithGoogleOption
   * 2. Catches GetCredentialCancellationException separately, logs diagnostic warning,
   *    and resets isLoading = false for retry.
   */
  fun signInWithGoogle(context: Context, onComplete: ((Boolean) -> Unit)? = null) {
    scope.launch {
      _isLoading.value = true
      _errorMessage.value = null
      val credentialManager = CredentialManager.create(context)

      val serverClientId = try {
        context.getString(R.string.default_web_client_id)
      } catch (e: Exception) {
        ""
      }

      val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(serverClientId)
        .build()

      // Exact single credential option per request
      val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

      try {
        val result = credentialManager.getCredential(
          context = context,
          request = request
        )

        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
          val idToken = googleIdTokenCredential.idToken
          val authCredential = GoogleAuthProvider.getCredential(idToken, null)
          val authResult = auth.signInWithCredential(authCredential).await()
          val user = authResult.user
          _currentUser.value = user
          _authSuccessMessage.value = "Welcome, ${user?.displayName ?: user?.email ?: "Student"}!"
          if (user != null) {
            onUserSignedInCallback?.invoke(user)
          }
          onComplete?.invoke(true)
        } else {
          Log.w(TAG, "Unexpected credential type received: ${credential.type}")
          _errorMessage.value = "Unexpected credential received from Google Sign-In"
          onComplete?.invoke(false)
        }
      } catch (e: GetCredentialCancellationException) {
        // Separately caught per specification
        Log.w(TAG, "Google Sign-In was cancelled by user: ${e.message}", e)
        _errorMessage.value = "Google Sign-In was cancelled."
        onComplete?.invoke(false)
      } catch (e: GetCredentialException) {
        Log.w(TAG, "Credential Manager error during Google Sign-In: ${e.message}", e)
        _errorMessage.value = "Google Sign-In: ${e.localizedMessage ?: e.message}"
        onComplete?.invoke(false)
      } catch (e: Exception) {
        Log.e(TAG, "Authentication error during Google Sign-In", e)
        _errorMessage.value = "Sign-In error: ${e.localizedMessage ?: e.message}"
        onComplete?.invoke(false)
      } finally {
        _isLoading.value = false
      }
    }
  }

  /**
   * Silent Auto Sign-In with Google using Credential Manager.
   * Carries EXACTLY ONE option: GetGoogleIdOption
   */
  fun silentSignInWithGoogle(context: Context) {
    scope.launch {
      val credentialManager = CredentialManager.create(context)
      val serverClientId = try {
        context.getString(R.string.default_web_client_id)
      } catch (e: Exception) {
        ""
      }

      val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId(serverClientId)
        .setAutoSelectEnabled(true)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      try {
        val result = credentialManager.getCredential(
          context = context,
          request = request
        )
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
          val idToken = googleIdTokenCredential.idToken
          val authCredential = GoogleAuthProvider.getCredential(idToken, null)
          val authResult = auth.signInWithCredential(authCredential).await()
          val user = authResult.user
          _currentUser.value = user
          if (user != null) {
            onUserSignedInCallback?.invoke(user)
          }
        }
      } catch (e: Exception) {
        // Silent sign-in fails silently
        Log.d(TAG, "Silent auto sign-in not available: ${e.message}")
      }
    }
  }

  fun signOut(context: Context) {
    scope.launch {
      try {
        auth.signOut()
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        _currentUser.value = null
        _authSuccessMessage.value = "Signed out of Google account"
      } catch (e: Exception) {
        Log.e(TAG, "Error during sign out", e)
      }
    }
  }
}
