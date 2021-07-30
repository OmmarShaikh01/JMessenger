package org.jabm.jabmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jabm.jabmessenger.databinding.ActivitySplashScreenBinding
import org.jabm.jabmessenger.userregisteration.GetStartedActivity


class SplashScreenActivity : AppCompatActivity() {
    // Private Declarations --------------------------------------------------------------------------------------------
    private fun checkUserAuth(): Unit{
        // user auth then launch the main chat app
        if (true) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // user auth failed try to get teh user to sign in or register
        else {
            val intent = Intent(this, GetStartedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    // Reimplemented Public Functions ----------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animation and Main Intent Launch
        binding.splashscreenImageview.alpha = 0f
        binding.splashscreenTextview.alpha = 0f
        binding.splashscreenTextview.animate().setDuration(100).alpha(1f).start()
        binding.splashscreenImageview.animate().setDuration(2000).alpha(1f).withEndAction {
            this.checkUserAuth()
        }.start()
    }

    // Public Declarations ---------------------------------------------------------------------------------------------

    // Slots -----------------------------------------------------------------------------------------------------------

    // Signals ---------------------------------------------------------------------------------------------------------
}

