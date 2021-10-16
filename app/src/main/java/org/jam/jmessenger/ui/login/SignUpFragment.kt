package org.jam.jmessenger.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.data.db.repository.AuthenticationRepository
import org.jam.jmessenger.data.db.repository.ChatsRepository
import org.jam.jmessenger.data.db.repository.DatabaseRepository
import org.jam.jmessenger.databinding.SignUpFragmentBinding
import org.jam.jmessenger.ui.hideKeyboard
import org.jam.jmessenger.ui.unregisterUser


/**
 * Sign up fragment
 *
 * @constructor Create empty Sign up fragment
 */
@SuppressLint("SetTextI18n")
class SignUpFragment : Fragment(), View.OnClickListener {
    private lateinit var bindings: SignUpFragmentBinding
    private lateinit var navController: NavController

    private val repository: DatabaseRepository = DatabaseRepository()
    private val auth = AuthenticationRepository(false)
    private var user: User = User()


    private fun signInTextClickable(textView: TextView) {
        val spannable = SpannableString("Already Have an Account? Sign In")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
            }
        }
        spannable.setSpan(clickableSpan, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun createAccount(name: String?, email: String?, password: String?) {
        if ((email?.length != 0) and (password?.length != 0) and (name?.length != 0)) {
            // all fields are available, try user creation on Firebase Authentication system
             auth.createUserWithEmail(email!!, password!!)
                .addOnSuccessListener(requireActivity()) { result ->
                    user.info.uid = auth.useruid!!
                    user.info.name = name!!
                    user.info.email = email

                    repository.createNewUser(user)
                    onSuccessHandlerSignUp(result)
                }
                .addOnFailureListener { exception -> onFailureHandlerSignUp(exception) }
        } else {
            // NUll check
            if (name?.length == 0) {
                bindings.signupTextView3.error = "Empty User Name"
                bindings.signupTextView3.text = "Empty User Name"
            }
            if (email?.length == 0) {
                bindings.signupTextView4.error = "Empty Email"
                bindings.signupTextView4.text = "Empty Email"
            }
            if (password?.length == 0) {
                bindings.signupTextView5.error = "Empty Password"
                bindings.signupTextView5.text = "Empty Password"
            }
        }
        hideKeyboard()
    }

    private fun onSuccessHandlerSignUp(result: AuthResult){
        // Sign in success, update UI with the signed-in user's information
        Toast.makeText(requireContext(), "Authentication Successful.", Toast.LENGTH_SHORT).show()
        navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToHomeFragment())
        unregisterUser()
    }

    private fun onFailureHandlerSignUp(exception: Exception) {
        Log.e("FireBaseCreateUser", exception.toString())

        bindings.signupTextView3.text = "Enter User Name"
        bindings.signupTextView3.error = null

        bindings.signupTextView4.text = "Enter User Email"
        bindings.signupTextView4.error = null

        bindings.signupTextView5.text = "Enter User Password"
        bindings.signupTextView5.error = null

        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                bindings.signupTextView5.error = "Invalid Credentials"
                bindings.signupTextView5.text = "Invalid Credentials"
            }
            is FirebaseAuthUserCollisionException -> {
                bindings.signupTextView3.error = "User Already Exists"
                bindings.signupTextView3.text = "User Already Exists"
            }
            is FirebaseAuthWeakPasswordException -> {
                bindings.signupTextView5.error = "Minimum Length is 8"
                bindings.signupTextView5.text = "Minimum Length is 8"
            }
        }
        Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindings = SignUpFragmentBinding.inflate(inflater)
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        bindings.signupButton.setOnClickListener(this)
        signInTextClickable(bindings.signupTextView2)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(view: View?) {
        when(view!!) {
            bindings.signupButton -> createAccount(
                name = bindings.signupEditTextName.text.toString(),
                email = bindings.signupEditTextEmail.text.toString(),
                password = bindings.signupEditTextPassword.text.toString(),
            )
        }
    }
}