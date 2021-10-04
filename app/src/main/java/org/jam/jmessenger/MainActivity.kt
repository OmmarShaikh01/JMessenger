package org.jam.jmessenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity() {

    private fun initEmulator() {
        //TODO: testing connects to emulator
        try {
            val mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            mAuth.useEmulator("10.0.2.2", 9099)

            val mFirestore = FirebaseFirestore.getInstance()
            mFirestore.clearPersistence()
            mFirestore.useEmulator("10.0.2.2", 8080)

            val mStorage = FirebaseStorage.getInstance()
            mStorage.useEmulator("10.0.2.2", 9199)
        } catch (e: Exception) {
            Log.e("MainActivity", e.toString())
        }
    }

    /**
     * On create
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initEmulator()
        setContentView(R.layout.activity_main)
    }
}