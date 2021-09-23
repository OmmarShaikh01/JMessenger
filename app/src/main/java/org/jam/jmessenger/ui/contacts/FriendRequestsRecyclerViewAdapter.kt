package org.jam.jmessenger.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.Event
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.widgets.ProfileRoundImageView
import java.lang.ref.WeakReference


class FriendRequestsRecyclerViewAdapter(
    private var userFriendsList: HashMap<String, UserFriend>,
    private val parentViewModel: FriendRequestsViewModel
) :
    RecyclerView.Adapter<FriendRequestsRecyclerViewAdapter.FriendRequestsViewHolder>() {

    private val TAG = "FriendRequestsRVAdapter"

    // LiveData
    private val mDataLoading = MutableLiveData<Event<Boolean>>()
    val dataLoading: LiveData<Event<Boolean>> = mDataLoading

    fun updateList(data: HashMap<String, UserFriend>) {
        val tempList: HashMap<String, UserFriend> = data.clone() as HashMap<String, UserFriend>
        for ((k, v) in data.iterator()) {
            if (v.state?.name.toString() != FriendState.INREQUESTED.name) {
                tempList.remove(k)
            }
        }
        userFriendsList = tempList
    }

    private fun setProfileImagetoView(data: UserFriend, imageView: ProfileRoundImageView) {
        parentViewModel.loadUserProfileImage(data.uid, imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.requests_recycler_view_item, parent, false)
        val holder = FriendRequestsViewHolder(view, true)
        return holder
    }

    override fun onBindViewHolder(holder: FriendRequestsViewHolder, position: Int) {
        val data = userFriendsList[userFriendsList.keys.elementAt(position)]
        if (data != null){
            setProfileImagetoView(data, holder.imageviewProfile)
            holder.userFriend = data
            holder.textviewName.text = data.name
        }
    }

    override fun getItemCount(): Int {
        return userFriendsList.size
    }

    inner class FriendRequestsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var TAG = "FriendRequestsViewHolder"
        var imageviewProfile: ProfileRoundImageView = itemView.findViewById(R.id.requestItem_ImageView)
        var textviewName: TextView = itemView.findViewById(R.id.requestItem_TextView_Name)
        var textviewStatus: TextView = itemView.findViewById(R.id.requestItem_TextView_status)
        var userFriend: UserFriend? = null

        private lateinit var buttonAcceptRequest: Button
        private lateinit var buttonDeclineRequest: Button

        constructor(itemView: View, clickable: Boolean): this(itemView) {
            if (clickable) {
                buttonAcceptRequest = itemView.findViewById(R.id.requestItem_Button_Accept)
                buttonDeclineRequest = itemView.findViewById(R.id.requestItem_Button_Decline)
                val weakThis = WeakReference(this)
                buttonDeclineRequest.setOnClickListener(weakThis.get())
                buttonAcceptRequest.setOnClickListener(weakThis.get())
            }
        }

        private fun acceptRequest(userFriend: UserFriend) {
            parentViewModel.acceptRequest(userFriend)
        }

        private fun declineRequest(userFriend: UserFriend) {
            parentViewModel.rejectRequest(userFriend)
        }

        override fun onClick(v: View?) {
            when (v!!) {
                buttonAcceptRequest -> {
                    userFriend?.let { userFriend -> acceptRequest(userFriend) }
                }
                buttonDeclineRequest -> {
                    userFriend?.let { userFriend -> declineRequest(userFriend) }
                }
            }
        }
    }
}