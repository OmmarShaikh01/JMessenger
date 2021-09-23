package org.jam.jmessenger.ui.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.FriendState
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.ui.misc.FriendInfoPopupFragment
import org.jam.jmessenger.widgets.ProfileRoundImageView

class ChatsRecyclerViewAdapter(
    private var userFriendsList: HashMap<String, UserFriend>,
    private val parentViewModel: ChatsHomeViewModel,
    private val parentNavController: NavController
): RecyclerView.Adapter<ChatsRecyclerViewAdapter.ChatsViewHolder>() {

    private val TAG = "ChatsRVAdapter"
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

    // START REGION: overrides
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ChatsRecyclerViewAdapter.ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chats_recycler_view_item, parent, false)
        popupFragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsRecyclerViewAdapter.ChatsViewHolder, position: Int) {
        val data = userFriendsList[userFriendsList.keys.elementAt(position)]
        if (data != null){
            holder.userFriend = data
            holder.constLayout.setOnClickListener {
               Log.i(TAG, "CHAT WITH ${data.name}")
            }
            holder.imageviewProfile.setOnClickListener {
                Log.i(TAG, "VIEW INFO ${data.name}")
            }
        }
    }

    override fun getItemCount(): Int {
        return userFriendsList.size
    }
    // END REGION

    inner class ChatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var TAG = "ChatsViewHolder"
        var imageviewProfile: ProfileRoundImageView = itemView.findViewById(R.id.chatsItem_ImageView_Profile)
        var textviewName: TextView = itemView.findViewById(R.id.chatsItem_TextView_Name)
        var textviewStatus: TextView = itemView.findViewById(R.id.chatsItem_TextView_status)
        var imageviewMute: ImageView = itemView.findViewById(R.id.chatsItem_ImageView_mute)
        var textviewUnread: TextView = itemView.findViewById(R.id.chatsItem_TextView_unreadcount)
        var constLayout: ConstraintLayout = itemView.findViewById(R.id.chatsItem_ConstLayout)
        lateinit var userFriend: UserFriend
    }
}