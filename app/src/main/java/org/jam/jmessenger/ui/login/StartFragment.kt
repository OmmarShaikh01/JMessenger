package org.jam.jmessenger.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import org.jam.jmessenger.databinding.StartFragmentBinding


/**
 * Start fragment
 *
 * @constructor Create empty Start fragment
 */
class StartFragment : Fragment(), View.OnClickListener {
    private lateinit var bindings: StartFragmentBinding
    private lateinit var navController: NavController

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
        bindings = StartFragmentBinding.inflate(inflater)
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
        bindings.startButtonSignup.setOnClickListener(this)
        bindings.startButtonSignin.setOnClickListener(this)
    }

    /**
     * On click
     *
     * @param view
     */
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(view: View?) {
        when(view!!) {
            bindings.startButtonSignup -> {
                navController.navigate(StartFragmentDirections.actionStartFragmentToSignUpFragment())
            }
            bindings.startButtonSignin -> {
                navController.navigate(StartFragmentDirections.actionStartFragmentToSignInFragment())
            }
        }
    }
}
