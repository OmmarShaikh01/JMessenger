package org.jam.jmessenger.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.databinding.UserProfileFragmentBinding
import android.graphics.Bitmap





class UserProfileFragment : Fragment(), View.OnClickListener {
    private var auth_user = Firebase.auth.currentUser

    private lateinit var loadedUser: User
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var bindings: UserProfileFragmentBinding
    private lateinit var startGetImageFromGallery: ActivityResultLauncher<Intent?>

    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            UserProfileViewModelFactory(auth_user?.uid.toString())
        ).get(UserProfileViewModel::class.java)
        observeViewModel()
    }

    private fun initStartGetImageFromGallery () {
        startGetImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val uri = Uri.parse(data?.dataString)
                bindings.uprofileImageViewProfileback.setImageURI(uri)
                bindings.uprofileImageViewProfilefront.setImageURI(uri)
            }
        }
    }

    private fun observeViewModel() {
        // Observers user Data Changes
        viewModel.user.observe(this.viewLifecycleOwner, Observer { user ->
            if (user != null) { updateUI(user) }
        })
        viewModel.profile.observe(this.viewLifecycleOwner, Observer { profile ->
            if (profile != null) { updateProfileUI(profile) }
        })
    }

    private fun updateUI(user: User) {
        loadedUser = user
        bindings.uprofileEditTextEmail.setText(user.info.email)
        bindings.uprofileEditTextName.setText(user.info.name)
        bindings.uprofileEditTextStatus.setText(user.info.status)
        bindings.uprofileEditTextMisc.setText(user.info.last_seen)
    }

    private fun updateProfileUI(profile: Profile) {
        val bmp = BitmapFactory.decodeByteArray(profile.data, 0, profile.data.size)
        bindings.uprofileImageViewProfileback.setImageBitmap(bmp)
        bindings.uprofileImageViewProfilefront.setImageBitmap(bmp)
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startGetImageFromGallery.launch(Intent.createChooser(intent, "Select Picture"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindings = UserProfileFragmentBinding.inflate(inflater)
        initViewModel()
        initStartGetImageFromGallery()
        return bindings.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindings.uprofileButtonSave.setOnClickListener(this)
        bindings.uprofileButtonDiscard.setOnClickListener(this)
        bindings.uprofileButtonProfileedit.setOnClickListener(this)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(view: View?) {
        when(view!!) {
            bindings.uprofileButtonProfileedit -> {
                selectImageFromGallery()
            }
            bindings.uprofileButtonSave -> {
                val email = bindings.uprofileEditTextEmail.text.toString()
                val name = bindings.uprofileEditTextName.text.toString()
                val status = bindings.uprofileEditTextStatus.text.toString()
                if (email.isNotEmpty() and name.isNotEmpty() and status.isNotEmpty()) {
                    if (auth_user!!.email.toString() != email) {
                        auth_user!!.updateEmail(email) // update firebase auth email
                    }
                    loadedUser.info.email = email
                    loadedUser.info.name = name
                    loadedUser.info.status = status
                    viewModel.updateUserInfo(loadedUser) // updates firebase datastore info
                    Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show()
                }
            }
            bindings.uprofileButtonDiscard -> {
                updateUI(loadedUser)
            }
        }
    }
}