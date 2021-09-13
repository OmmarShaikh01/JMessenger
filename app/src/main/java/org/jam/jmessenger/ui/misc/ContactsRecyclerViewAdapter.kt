package org.jam.jmessenger.ui.misc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.ui.contacts.ContactsHomeViewModel
import org.jam.jmessenger.widgets.ProfileRoundImageView


class ContactsRecyclerViewAdapter(
    private var userFriendsList: HashMap<String, UserFriend>,
    private val parentViewModel: ContactsHomeViewModel,
    private var parentNavController: NavController
) :
    RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactsViewHolder>() {

    private val TAG = "ContactsRVAdapter"
    private lateinit var popupFragment: FriendInfoPopupFragment
    private lateinit var popupFragmentManager: FragmentManager


    fun updateList(data: HashMap<String, UserFriend>) {
        val tempList: HashMap<String, UserFriend> = data.clone() as HashMap<String, UserFriend>
        for ((k, v) in data.iterator()) {
            if (v.state?.name.toString() != FriendState.FRIEND.name) {
                tempList.remove(k)
            }
        }
        userFriendsList = tempList
    }

    private fun setProfileImagetoRoundView(data: UserFriend, imageView: ProfileRoundImageView) {
        parentViewModel.loadUserFriendProfileImage(data.uid, imageView)
    }

    private fun setUserInfotoItem(data: UserFriend, name: TextView, status: TextView) {
        parentViewModel.loadUserFriendInfo(data.uid, name, status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_recycler_view_item, parent, false)
        popupFragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        popupFragment = FriendInfoPopupFragment(parentNavController, parentViewModel)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val data = userFriendsList[userFriendsList.keys.elementAt(position)]
        if (data != null){
            setProfileImagetoRoundView(data, holder.imageviewProfile)
            setUserInfotoItem(data, holder.textviewName, holder.textviewStatus)
            holder.userFriend = data
            holder.constLayout.setOnClickListener {
                popupFragment.userFriend = data
                popupFragment.show(popupFragmentManager, "")
            }
        }
    }

    override fun getItemCount(): Int {
        return userFriendsList.size
    }

    inner class ContactsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var TAG = "ContactsViewHolder"
        var imageviewProfile: ProfileRoundImageView = itemView.findViewById(R.id.contactsItem_ImageView_Profile)
        var textviewName: TextView = itemView.findViewById(R.id.contactsItem_TextView_Name)
        var textviewStatus: TextView = itemView.findViewById(R.id.contactsItem_TextView_status)
        var constLayout: ConstraintLayout = itemView.findViewById(R.id.contactsItem_ConstLayout)
        lateinit var userFriend: UserFriend
    }
}