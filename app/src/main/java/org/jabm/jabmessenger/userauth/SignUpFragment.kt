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
import org.jabm.jabmessenger.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: SignUpViewModel
    private lateinit var binding: SignUpFragmentBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SignUpFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.signUpButtonGmail.setOnClickListener(this)
        binding.signUpButtonSignup.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view!!) {
            binding.signUpButtonGmail -> {
                navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToMainFragment())
            }
            binding.signUpButtonSignup -> {
                navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
            }
        }
    }
}