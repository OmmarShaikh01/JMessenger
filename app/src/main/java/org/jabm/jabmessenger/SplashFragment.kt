package org.jabm.jabmessenger

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jabm.jabmessenger.databinding.SplashFragmentBinding

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var bindings: SplashFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindings = SplashFragmentBinding.inflate(inflater)

        // Animation
        bindings.splashImageView.alpha = 0f
        bindings.splashTextView.alpha = 0f
        bindings.splashTextView.animate().setDuration(100).alpha(1f).start()
        bindings.splashImageView.animate().setDuration(2000).alpha(1f).withEndAction(){
            navController.navigate(SplashFragmentDirections.actionSplashFragmentToGetStartedFragment())
        }.start()

        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }
}