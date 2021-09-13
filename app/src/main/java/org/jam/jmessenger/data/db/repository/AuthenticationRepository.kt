package org.jam.jmessenger.data.db.repository

import android.net.wifi.hotspot2.pps.Credential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationRepository { // TODO: NEEDS TO BE PUT IN VIEW MODELS
    private val authenticator = Firebase.auth
    private val currentUser = authenticator.currentUser
    val useruid = currentUser?.uid
    val useremail = currentUser?.uid

    fun getValidUser(): FirebaseUser {
        if (currentUser == null) {
            throw FirebaseAuthInvalidCredentialsException("AuthenticationRepository", "Illegal User")
        } else {
            return currentUser
        }
    }

    fun useEmulator(host: String, port: Int) {
        authenticator.useEmulator(host, port)
    }

    fun createUserWithEmail(email: String, password: String): Task<AuthResult> {
        return authenticator.createUserWithEmailAndPassword(email, password)
    }

    fun signinUserWithEmail(email: String, password: String): Task<AuthResult> {
        return authenticator.signInWithEmailAndPassword(email, password)
    }

    fun updateUserEmail(email: String, password: String): Task<Void>? {
        if (currentUser != null) {
            return currentUser.updateEmail(email)
        }
        return null
    }
}