package org.jabm.jabmessenger.userauth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jabm.jabmessenger.R
import org.jabm.jabmessenger.databinding.SignInFragmentBinding

class SignInFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: SignInViewModel
    private lateinit var binding: SignInFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignInFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.signInButtonGmail.setOnClickListener(this)
        binding.signInButtonSignin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!) {
            binding.signInButtonGmail -> {
                navController.navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
            }
            binding.signInButtonSignin -> {
                navController.navigate(SignInFragmentDirections.actionSignInFragmentToMainFragment())
            }
        }
    }
}