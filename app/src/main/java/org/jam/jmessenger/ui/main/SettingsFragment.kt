package org.jam.jmessenger.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.jam.jmessenger.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}