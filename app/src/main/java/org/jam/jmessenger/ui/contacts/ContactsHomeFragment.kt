package org.jam.jmessenger.ui.contacts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jam.jmessenger.R

class ContactsHomeFragment : Fragment() {

    companion object {
        fun newInstance() = ContactsHomeFragment()
    }

    private lateinit var viewModel: ContactsHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this).get(ContactsHomeViewModel::class.java)
        return inflater.inflate(R.layout.contacts_home_fragment, container, false)
    }
}