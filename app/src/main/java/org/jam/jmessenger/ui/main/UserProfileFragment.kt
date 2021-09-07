package org.jam.jmessenger.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.Profile
import org.jam.jmessenger.data.db.entity.User
import org.jam.jmessenger.databinding.UserProfileFragmentBinding
import java.io.ByteArrayOutputStream


/*
* TODO: create a value function to handle new users
*
* */


class UserProfileFragment : Fragment(), View.OnClickListener {
    private var authdUser = Firebase.auth.currentUser
    private var TAG = "UserProfileFragment"

    // loaded locally
    private var loadedUser: User = User()
    private var loadedProfile: Profile = Profile()

    // loaded from firebase: NOTE-PREVENT STALE DATA
    private var cacheUser: User = User()
    private var cacheProfile: Profile = Profile()

    // lateinit Vars
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var bindings: UserProfileFragmentBinding
    private lateinit var startGetImageFromGallery: ActivityResultLauncher<Intent?>
    private lateinit var picassoPaintTarget: Target


    // START REGION: VIewModel Related Methods
    private fun initViewModel() {
        // Declaring Fragment ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            UserProfileViewModelFactory(authdUser?.uid.toString())
        ).get(UserProfileViewModel::class.java)
        observeViewModel()
    }

    private fun observeViewModel() {
        // Observers user Data Changes
        viewModel.user.observe(this.viewLifecycleOwner, Observer { user ->
            if (user != null) { cacheUser = user; updateUserInfoUI(user) }
        })
        viewModel.profile.observe(this.viewLifecycleOwner, Observer { profile ->
            if (profile != null) { cacheProfile = profile; updateProfileUI(profile) }
        })
    }
    // END REGION

    // START REGION: User Info related Functions
    private fun updateUserInfoUI(user: User) {
        bindings.uprofileEditTextEmail.setText(user.info.email)
        bindings.uprofileEditTextName.setText(user.info.name)
        bindings.uprofileEditTextStatus.setText(user.info.status)
    }

    private fun updateUserInfo(email: String, name: String, status: String){
        if (email.isNotEmpty() and name.isNotEmpty() and status.isNotEmpty()) {
            // update firebase auth email
            if (authdUser!!.email.toString() != email) {
                // reauths if credentials are stale
                if (bindings.uprofileTextInputLayoutPassword.isVisible and bindings.uprofileEditTextPassword.text.toString().isNotEmpty()) {
                    val cred = EmailAuthProvider.getCredential(email, bindings.uprofileEditTextPassword.text.toString())
                    authdUser!!.reauthenticate(cred)
                }

                // updates te email in authenticator
                authdUser!!.updateEmail(email).addOnSuccessListener {
                    // updates firebase datastore info on auth successful
                    cacheUser.info.email = email
                    cacheUser.info.name = name
                    cacheUser.info.status = status
                    viewModel.updateUserInfo(cacheUser)
                    Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show()
                } .addOnFailureListener() {
                    Log.e(TAG, it.toString()) // LOGGER TODO: implement and test stale cred
                    when(it){
                        is FirebaseAuthRecentLoginRequiredException -> {
                            bindings.uprofileTextInputLayoutPassword.visibility = View.VISIBLE
                            bindings.uprofileTextInputLayoutPassword.error = "Sensitive Operation, Enter Password"
                        }
                    }
                    Toast.makeText(requireContext(), "Update Unsuccessful", Toast.LENGTH_SHORT).show()
                }
            } else {
                // updates if email ins unchanged
                cacheUser.info.name = name
                cacheUser.info.status = status
                viewModel.updateUserInfo(cacheUser)
                Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // END REGION


    // START REGION: User Profile related Functions
    private fun initStartGetImageFromGallery () {
        startGetImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                picassoUriLoader(Uri.parse(result.data?.dataString))
            }
        }

        // implements the picassoPintTarget
        picassoPaintTarget = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                if (bitmap != null) picassoBitmapLoaded(bitmap)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                if (errorDrawable != null) {
                    loadBitmapProfileFront(bitmap = errorDrawable.toBitmap(512, 512))
                    loadBitmapProfileBack(bitmap = errorDrawable.toBitmap(512, 512))
                }
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                if (placeHolderDrawable != null) {
                    loadBitmapProfileFront(bitmap = placeHolderDrawable.toBitmap(512, 512))
                    loadBitmapProfileBack(bitmap = placeHolderDrawable.toBitmap(512, 512))
                }
            }
        }
    }

    private fun picassoBitmapLoaded(bitmap: Bitmap) {
        // loads picasso bitmap to a bytearray
        val bitmapNewScaled = bitmap.scale(512, 512)
        val baos = ByteArrayOutputStream()
        bitmapNewScaled.compress(Bitmap.CompressFormat.PNG, 80, baos)
        val data = baos.toByteArray()
        val profile = Profile(data = data)

        // imageview Update for ImageView
        loadBitmapProfileFront(bitmap)
        loadBitmapProfileBack(bitmap)

        // Firebase Update for userProfile
        viewModel.updateUserProfile(authdUser!!.uid, profile)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),
                    "Profile Update Successful",
                    Toast.LENGTH_SHORT).show()

                cacheProfile.data = profile.data
            }
            .addOnFailureListener() {
                Toast.makeText(requireContext(),
                    "Profile Update Unsuccessful",
                    Toast.LENGTH_SHORT).show()

                // imageview Update for ImageView if update failed
                updateProfileUI(cacheProfile)
            }
    }

    private fun updateProfileUI(profile: Profile) {
        if (profile.data.isNotEmpty()) {
            val bmp = BitmapFactory.decodeByteArray(profile.data, 0, profile.data.size)
            loadBitmapProfileBack(bmp)
            loadBitmapProfileFront(bmp)
        }
    }

    private fun picassoUriLoader(uri: Uri) {
        Picasso.get().load(uri)
            .placeholder(R.drawable.ic_user_default_profile)
            .error(R.drawable.ic_user_default_profile)
            .into(picassoPaintTarget)
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startGetImageFromGallery.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private fun loadBitmapProfileFront(bitmap: Bitmap?){
        bindings.uprofileImageViewProfilefront.setImageBitmap(bitmap)
    }

    private fun loadBitmapProfileBack(bitmap: Bitmap?){
        bindings.uprofileImageViewProfileback.setImageBitmap(bitmap)
    }
    // END REGION


    // START REGION: overrides
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        xsavedInstanceState: Bundle?,
    ): View? {
        bindings = UserProfileFragmentBinding.inflate(inflater)
        bindings.uprofileTextInputLayoutPassword.visibility = View.INVISIBLE
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
                updateUserInfo(email, name, status)
            }
            bindings.uprofileButtonDiscard -> {
                // considers the condition where the data has been modified but needs to be discarded
                // uses the last modified data of cache data
                updateUserInfoUI(cacheUser)
            }
        }
    }
    // END REGION

}