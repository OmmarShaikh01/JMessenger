package org.jabm.jabmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jabm.jabmessenger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Private Declarations -------------------------------------------------------------------------------------------

    // Reimplemented Public Functions ---------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Public Declarations --------------------------------------------------------------------------------------------

    // Slots ----------------------------------------------------------------------------------------------------------

    // Signals --------------------------------------------------------------------------------------------------------
}
