package com.example.fairshare.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.UUID

class GoogleSignInHelper(private val context: Context) {
    private val credentialManager = CredentialManager.Companion.create(context)

    // Silent login for returning users
    suspend fun signInAuthorized(webClientId: String): Result<String> = runCatching {
        val nonce = UUID.randomUUID().toString()
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val response = credentialManager.getCredential(context, request)
        handleCredentialResponse(response)
    }

    // Fallback login for first-time users
    suspend fun signInFallback(webClientId: String): Result<String> = runCatching {
        val nonce = UUID.randomUUID().toString()
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val response = credentialManager.getCredential(context, request)
        handleCredentialResponse(response)
    }

    private fun handleCredentialResponse(resp: GetCredentialResponse): String {
        val cred = resp.credential
        if (cred is CustomCredential &&
            cred.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val gitc = GoogleIdTokenCredential.Companion.createFrom(cred.data)
            return gitc.idToken
        } else {
            throw Exception("Unexpected credential type")
        }
    }
}