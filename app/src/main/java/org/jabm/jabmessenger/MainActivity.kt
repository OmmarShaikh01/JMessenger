/* CODING CONVENTIONS --------------------------------------------------------------------------------------------------
Imports:
    Imports should be declared in this format with a line space between each format stdlib, external, internal

Classes:
    Naming: PascalCase
    Structure: Private Declarations, Reimplemented Public Functions, Public Declarations, Slots, Signals

Methods:
    Naming: TitleCase(function name must start with a lower case character)
    Structure: Variables, Logic

Variables:
    Naming-constants: <LowerCase>_const
    Naming-normal: LowerCase

Activity Classes:
    Naming: <PascalCase>Activity
    Structure: Private Declarations, Reimplemented Public Functions, Public Declarations, Slots, Signals

User Interface:
    Naming: <lowercase info><type>

--------------------------------------------------------------------------------------------------------------------- */
package org.jabm.jabmessenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jabm.jabmessenger.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    // Private Declarations --------------------------------------------------------------------------------------------

    // Reimplemented Public Functions ----------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Public Declarations ---------------------------------------------------------------------------------------------

    // Slots -----------------------------------------------------------------------------------------------------------

    // Signals ---------------------------------------------------------------------------------------------------------
}
