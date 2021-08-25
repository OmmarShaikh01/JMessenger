package org.jam.jmessenger.ui.chats

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jam.jmessenger.R

class ChatsHomeFragment : Fragment() {

    companion object {
        fun newInstance() = ChatsHomeFragment()
    }

    private lateinit var viewModel: ChatsHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.chats_home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatsHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}