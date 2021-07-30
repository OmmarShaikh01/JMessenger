package org.jabm.jabmessenger.userregisteration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jabm.jabmessenger.databinding.ActivityGetStartedBinding

class GetStartedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}