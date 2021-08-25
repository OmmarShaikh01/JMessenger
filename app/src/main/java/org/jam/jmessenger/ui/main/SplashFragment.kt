package org.jam.jmessenger.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import org.jam.jmessenger.databinding.SplashFragmentBinding


/**
 * Splash fragment, Splash Screen for the Application
 *
 * @constructor Create empty Splash fragment
 */
class SplashFragment : Fragment() {
    private lateinit var bindings: SplashFragmentBinding
    private lateinit var navController: NavController
    private val auth = FirebaseAuth.getInstance()

    /**
     * User auth, Check for user authentication and navigates to valid destination
     *
     */
    private fun userAuth() {
        // Firebase.auth.signOut()
        val user = auth.currentUser // also considers persistence over sessions
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
        // (activity as? AppCompatActivity)?.supportActionBar?.hide()
        bindings = SplashFragmentBinding.inflate(inflater)

        // Animation
        bindings.splashImageView.alpha = 0f
        bindings.splashTextView.alpha = 0f
        bindings.splashTextView.animate().setDuration(100).alpha(1f).start()
        bindings.splashImageView.animate().setDuration(2000).alpha(1f).withEndAction(){
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