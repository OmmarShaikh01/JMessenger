package org.jam.jmessenger.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.jam.jmessenger.R

class GroupsHomeFragment : Fragment() {

    companion object {
        fun newInstance() = GroupsHomeFragment()
    }

    private lateinit var viewModel: GroupsHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewModel = ViewModelProvider(this).get(GroupsHomeViewModel::class.java)
        return inflater.inflate(R.layout.groups_home_fragment, container, false)
    }
}