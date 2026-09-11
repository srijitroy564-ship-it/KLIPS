package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val idToken: String? = null,
    val isSignedIn: Boolean = true
)

data class CoinTransaction(
    val id: String,
    val description: String,
    val amount: Int, // positive for addition, negative for deduction
    val timestamp: String
)

/**
 * Manages local credit storage (50 default coins upon first login)
 * using Android SharedPreferences as requested.
 */
class CreditsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _coins = MutableStateFlow(loadCoins())
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _transactions = MutableStateFlow(loadTransactions())
    val transactions: StateFlow<List<CoinTransaction>> = _transactions.asStateFlow()

    private fun loadCoins(): Int {
        val hasLoggedInBefore = prefs.getBoolean(KEY_HAS_LOGGED_IN_BEFORE, false)
        return if (!hasLoggedInBefore) {
            // Default 50 coins initialized upon first login
            DEFAULT_INITIAL_COINS
        } else {
            prefs.getInt(KEY_USER_COINS, DEFAULT_INITIAL_COINS)
        }
    }

    private fun loadUserProfile(): UserProfile? {
        val isSignedIn = prefs.getBoolean(KEY_IS_SIGNED_IN, false)
        if (!isSignedIn) return null
        val name = prefs.getString(KEY_USER_NAME, "Creator") ?: "Creator"
        val email = prefs.getString(KEY_USER_EMAIL, "creator@gmail.com") ?: "creator@gmail.com"
        val photo = prefs.getString(KEY_USER_PHOTO, null)
        val token = prefs.getString(KEY_USER_ID_TOKEN, null)
        return UserProfile(
            displayName = name,
            email = email,
            photoUrl = photo,
            idToken = token,
            isSignedIn = true
        )
    }

    private fun loadTransactions(): List<CoinTransaction> {
        val raw = prefs.getString(KEY_TRANSACTIONS, null) ?: return listOf(
            CoinTransaction("tx_welcome", "Welcome Creator Bonus", DEFAULT_INITIAL_COINS, "On Sign-In")
        )
        return raw.split(";").mapNotNull { line ->
            val parts = line.split("|")
            if (parts.size >= 4) {
                CoinTransaction(
                    id = parts[0],
                    description = parts[1],
                    amount = parts[2].toIntOrNull() ?: 0,
                    timestamp = parts[3]
                )
            } else null
        }
    }

    /**
     * Initializes default 50 coins upon user login if first time.
     */
    fun onUserLogin(profile: UserProfile) {
        val hasLoggedInBefore = prefs.getBoolean(KEY_HAS_LOGGED_IN_BEFORE, false)
        val initialCoins = if (!hasLoggedInBefore) {
            DEFAULT_INITIAL_COINS
        } else {
            prefs.getInt(KEY_USER_COINS, DEFAULT_INITIAL_COINS)
        }

        prefs.edit()
            .putBoolean(KEY_HAS_LOGGED_IN_BEFORE, true)
            .putBoolean(KEY_IS_SIGNED_IN, true)
            .putString(KEY_USER_NAME, profile.displayName)
            .putString(KEY_USER_EMAIL, profile.email)
            .putString(KEY_USER_PHOTO, profile.photoUrl)
            .putString(KEY_USER_ID_TOKEN, profile.idToken)
            .putInt(KEY_USER_COINS, initialCoins)
            .apply()

        _userProfile.value = profile
        _coins.value = initialCoins

        if (!hasLoggedInBefore) {
            recordTransaction("Welcome Creator Bonus (50 Coins)", DEFAULT_INITIAL_COINS)
        }
    }

    fun signOut() {
        prefs.edit()
            .putBoolean(KEY_IS_SIGNED_IN, false)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_PHOTO)
            .remove(KEY_USER_ID_TOKEN)
            .apply()
        _userProfile.value = null
    }

    /**
     * Deducts coins locally from SharedPreferences for an AI operation.
     * Returns true if successful, false if insufficient credits.
     */
    fun deductCoins(amount: Int, reason: String): Boolean {
        if (amount <= 0) return true
        val current = _coins.value
        if (current < amount) return false

        val updated = current - amount
        prefs.edit().putInt(KEY_USER_COINS, updated).apply()
        _coins.value = updated
        recordTransaction(reason, -amount)
        return true
    }

    fun addCoins(amount: Int, reason: String) {
        if (amount <= 0) return
        val updated = _coins.value + amount
        prefs.edit().putInt(KEY_USER_COINS, updated).apply()
        _coins.value = updated
        recordTransaction(reason, amount)
    }

    fun resetToDefault() {
        prefs.edit().clear().apply()
        _coins.value = DEFAULT_INITIAL_COINS
        _userProfile.value = null
        _transactions.value = emptyList()
    }

    private fun recordTransaction(desc: String, amount: Int) {
        val list = _transactions.value.toMutableList()
        val newTx = CoinTransaction(
            id = "tx_${System.currentTimeMillis()}",
            description = desc,
            amount = amount,
            timestamp = "Just now"
        )
        list.add(0, newTx)
        val serialized = list.take(15).joinToString(";") { "${it.id}|${it.description}|${it.amount}|${it.timestamp}" }
        prefs.edit().putString(KEY_TRANSACTIONS, serialized).apply()
        _transactions.value = list
    }

    companion object {
        const val PREFS_NAME = "klipz_user_prefs"
        const val KEY_HAS_LOGGED_IN_BEFORE = "has_logged_in_before"
        const val KEY_IS_SIGNED_IN = "is_signed_in"
        const val KEY_USER_COINS = "user_coins"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_PHOTO = "user_photo"
        const val KEY_USER_ID_TOKEN = "user_id_token"
        const val KEY_TRANSACTIONS = "user_transactions"
        const val DEFAULT_INITIAL_COINS = 50
    }
}
