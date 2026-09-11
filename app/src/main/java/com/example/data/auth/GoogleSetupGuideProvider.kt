package com.example.data.auth

/**
 * Step-by-Step Developer Guide for Google Cloud Console Setup & Credential Manager.
 */
object GoogleSetupGuideProvider {

    val guideMarkdown = """
# 🚀 Google Sign-In WITHOUT Firebase (Official Android Guide)

### 1. Google Cloud Console Setup & SHA-1 Fingerprint
1. **Get your SHA-1 Fingerprint**:
   Run in your terminal for debug keystore:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```
   For Windows:
   ```cmd
   keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
   ```
   Or via Gradle:
   ```bash
   gradle signingReport
   ```
2. **Open Google Cloud Console**:
   - Go to [Google Cloud Console](https://console.cloud.google.com/) -> **APIs & Services** -> **Credentials**.
   - Configure your **OAuth Consent Screen** (App Name: "Klips", Support Email, User Type: External).
3. **Create Android OAuth Client ID**:
   - Click **Create Credentials** -> **OAuth client ID**.
   - Application Type: **Android**.
   - Package name: `com.aistudio.klipz.videoeditor` (or your app's `applicationId`).
   - SHA-1 certificate fingerprint: Paste the 20-byte hex SHA-1 from Step 1.
4. **Create Web Application OAuth Client ID**:
   - Create a second OAuth Client ID with type **Web application**.
   - Copy this Web Client ID string (`xyz.apps.googleusercontent.com`).
   - This `serverClientId` is passed to `GetGoogleIdOption.setServerClientId(...)` to securely retrieve user ID Tokens without Firebase!

---

### 2. build.gradle Dependencies
Add Google's official Credential Manager & Google ID library:
```kotlin
// Modern Google Identity Services (No Firebase needed)
implementation("androidx.credentials:credentials:1.5.0")
implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
```

---

### 3. Clean Code (Kotlin with Coroutines)
```kotlin
val credentialManager = CredentialManager.create(context)

val googleIdOption = GetGoogleIdOption.Builder()
    .setFilterByAuthorizedAccounts(false)
    .setServerClientId("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
    .setAutoSelectEnabled(false)
    .build()

val request = GetCredentialRequest.Builder()
    .addCredentialOption(googleIdOption)
    .build()

val response = credentialManager.getCredential(request, context)
val credential = response.credential

if (credential is CustomCredential && 
    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
    
    val name = googleIdToken.displayName
    val email = googleIdToken.id
    val photoUrl = googleIdToken.profilePictureUri
    val idToken = googleIdToken.idToken
}
```

---

### 4. Local 50 Coins Storage via SharedPreferences
```kotlin
val prefs = context.getSharedPreferences("klipz_user_prefs", Context.MODE_PRIVATE)
val hasLoggedIn = prefs.getBoolean("has_logged_in_before", false)

if (!hasLoggedIn) {
    prefs.edit()
        .putBoolean("has_logged_in_before", true)
        .putInt("user_coins", 50) // Default 50 coins on first login
        .apply()
}
```
    """.trimIndent()
}
