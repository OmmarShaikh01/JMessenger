package org.jam.jmessenger.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.databinding.SignInFragmentBinding
import org.jam.jmessenger.ui.hideKeyboard
import org.jam.jmessenger.ui.unregisterUser


/**
 * Sign in fragment
 *
 * @constructor Create empty Sign in fragment
 */
class SignInFragment : Fragment(), View.OnClickListener {
    private lateinit var bindings: SignInFragmentBinding
    private lateinit var navController: NavController
    private var auth =  AuthenticationRepository(false)


    /**
     * Sign Up Text clickable, that navigates to the fragment
     *
     * @param textView: Text view to add clickable text to
     */
    private fun signUptextClickable(textView: TextView) {
        val spannable = SpannableString("Dont Have an Account? Sign Up",)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navController.navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
            }
        }
        spannable.setSpan(clickableSpan, 22, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Sign in user, validates and signs in the user to use the app
     *
     * @param email
     * @param password
     */
    private fun signInUser(email: String?, password: String?){
        // resets the helper texts to default values
        bindings.signinTextView3.text = "Enter User Email"
        bindings.signinTextView4.text = "Enter User Password"
        bindings.signinTextView3.error = null
        bindings.signinTextView4.error = null

        if ((email?.length != 0) and (password?.length != 0)) {
            // validates the user when email and password is passed in
            auth.signinUserWithEmail(email!!, password!!)
                .addOnSuccessListener(requireActivity()) {
                    Toast.makeText(requireContext(), "Authentication Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment())
                    unregisterUser()
                }. addOnFailureListener() { exception ->
                    onUserSigningFailure(exception)
                }
        } else {
            // NUll check
            if (email?.length == 0) {
                bindings.signinTextView3.error = "Empty Credentials"
                bindings.signinTextView3.text = "Empty Credentials"
            }
            if (password?.length == 0) {
                bindings.signinTextView4.error = "Empty Password"
                bindings.signinTextView4.text = "Empty Password"
            }
        }
        hideKeyboard()
    }

    /**
     * On user signing failure, checks for the exception raised and updates the UI to notify the user
     *
     * @param exception
     */
    private fun onUserSigningFailure(exception: Exception) {
        when (exception) {
            is FirebaseAuthInvalidUserException -> {
                bindings.signinTextView3.error = "Invalid User Credentials"
                bindings.signinTextView3.text = "Invalid User Credentials"
            }
            is FirebaseAuthInvalidCredentialsException -> {
                bindings.signinTextView4.error = "Invalid User Credentials"
                bindings.signinTextView4.text = "Invalid User Credentials"
            }
        }
        Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show()
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
        savedInstanceState: Bundle?,
    ): View? {
        bindings = SignInFragmentBinding.inflate(inflater)
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

        // Set Listeners
        bindings.signinButton.setOnClickListener(this)
        bindings.signinTextViewForgotPass.setOnClickListener(this)
        signUptextClickable(textView = bindings.signinTextView2)
    }

    /**
     * On click
     *
     * @param view
     */
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(view: View?) {
        when(view!!) {
            bindings.signinButton -> signInUser(
                email = bindings.signinEditTextEmail.text.toString(),
                password = bindings.signinEditTextPassword.text.toString(),
            )
            bindings.signinTextViewForgotPass -> {
                TODO("IMPLEMENT")
            }
            bindings.signinTextView2 -> {
                navController.navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
            }
        }
    }
}