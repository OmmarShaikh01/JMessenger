package org.jam.jmessenger.data.db.repository

import android.net.wifi.hotspot2.pps.Credential
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationRepository(private val validateUser: Boolean = true) {
    private val authenticator = FirebaseAuth.getInstance()
    val currentUser: FirebaseUser?
        get() = getValidUser()
    val useruid: String?
        get() = currentUser?.uid
    val useremail: String?
        get() = currentUser?.email

    fun getValidUser(): FirebaseUser? {
        if (authenticator.currentUser == null) {
            if (validateUser) {
                throw FirebaseAuthInvalidCredentialsException("AuthenticationRepository", "Illegal User")
            }
            return null
        } else {
            return authenticator.currentUser
        }
    }

    fun checkUserAvailable(): Boolean {
        return authenticator.currentUser != null
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

    fun updateUserEmail(email: String, password: String? = null): Task<Void>? {
        return authenticator.currentUser?.updateEmail(email)
    }

    fun reauthenticate(email: String, password: String): Task<Void>? {
        val creds = EmailAuthProvider.getCredential(email, password)
        return authenticator.currentUser?.reauthenticate(creds)
    }

    fun signout(){
        authenticator.signOut()
    }
}