package com.example.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val profile: UserProfile) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Cancelled : AuthResult()
}

/**
 * Implements Google Sign-In WITHOUT Firebase using Google's official
 * Credential Manager API & Google Identity Services SDK (androidx.credentials & googleid).
 */
class GoogleAuthManager(
    private val context: Context,
    private val creditsManager: CreditsManager
) {
    private val credentialManager = CredentialManager.create(context)

    /**
     * Executes official Google One-Tap / Google Sign-In via Credential Manager.
     * Extracts Name, Email, Profile Picture URL, and ID Token.
     */
    suspend fun signInWithGoogle(serverClientId: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId.ifBlank { DEFAULT_WEB_CLIENT_ID })
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val profile = UserProfile(
                    displayName = googleIdTokenCredential.displayName ?: "Creator",
                    email = googleIdTokenCredential.id,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    idToken = googleIdTokenCredential.idToken.take(32) + "...",
                    isSignedIn = true
                )

                creditsManager.onUserLogin(profile)
                AuthResult.Success(profile)
            } else {
                AuthResult.Error("Unexpected credential format received: ${credential.type}")
            }
        } catch (e: GetCredentialException) {
            // If running on emulator without configured Google Play Services or OAuth client,
            // provide clear guidance and fallback option
            AuthResult.Error("Google Sign-In failed: ${e.message}")
        } catch (e: Exception) {
            AuthResult.Error("Authentication error: ${e.message}")
        }
    }

    /**
     * Development / Sandbox login helper for emulator verification,
     * ensuring the 50 coins SharedPreferences storage can be tested immediately.
     */
    fun signInWithDemoAccount(): UserProfile {
        val demoProfile = UserProfile(
            displayName = "Alex Vance (Klipz Pro)",
            email = "creator.klips@gmail.com",
            photoUrl = "https://lh3.googleusercontent.com/a/demo-user",
            idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ij...",
            isSignedIn = true
        )
        creditsManager.onUserLogin(demoProfile)
        return demoProfile
    }

    fun signOut() {
        creditsManager.signOut()
    }

    companion object {
        // Placeholder Web Client ID from Google Cloud Console
        const val DEFAULT_WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
    }
}
