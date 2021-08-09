package org.jabm.jabmessenger.userauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jabm.jabmessenger.R
import org.jabm.jabmessenger.SplashViewModel
import org.jabm.jabmessenger.databinding.FragmentGetStartedBinding


class GetStartedFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: SplashViewModel
    private lateinit var bindings: FragmentGetStartedBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindings = FragmentGetStartedBinding.inflate(inflater)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        bindings.startedButtonSignUp.setOnClickListener(this)
        bindings.startedButtonSignIn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view!!) {
            bindings.startedButtonSignIn -> {
                navController.navigate(GetStartedFragmentDirections.actionGetStartedFragmentToSignInFragment())
            }
            bindings.startedButtonSignUp -> {
                navController.navigate(GetStartedFragmentDirections.actionGetStartedFragmentToSignUpFragment())
            }
        }
    }
}