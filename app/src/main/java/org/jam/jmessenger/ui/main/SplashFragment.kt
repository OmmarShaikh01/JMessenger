package org.jam.jmessenger.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.databinding.SplashFragmentBinding


/**
 * Splash fragment, Splash Screen for the Application
 *
 * @constructor Create empty Splash fragment
 */
class SplashFragment : Fragment() {
    private lateinit var bindings: SplashFragmentBinding
    private lateinit var navController: NavController
    private val auth = AuthenticationRepository(false)
    private val TAG = "SplashFragment"

    /**
     * User auth, Check for user authentication and navigates to valid destination
     *
     */
    private fun userAuth() {
        val user = auth.getValidUser()// also considers persistence over sessions
        if (user != null) {
            navController.navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
        } else {
            navController.navigate(SplashFragmentDirections.actionSplashFragmentToStartFragment())
        }
    }

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return ConstraintLayout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindings = SplashFragmentBinding.inflate(inflater)

        // Animation
        bindings.splashImageView.alpha = 0f
        bindings.splashTextView.alpha = 0f
        bindings.splashTextView.animate().setDuration(100).alpha(1f).start()
        bindings.splashImageView.animate().setDuration(1500).alpha(1f).withEndAction(){
            userAuth()
        }.start()
        return bindings.root
    }

    /**
     * On view created
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }
}