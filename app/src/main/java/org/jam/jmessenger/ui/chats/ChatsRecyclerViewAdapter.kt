package org.jam.jmessenger.ui.chats

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.j2objc.annotations.Weak
import kotlinx.coroutines.*
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.Result
import org.jam.jmessenger.data.db.entity.*
import org.jam.jmessenger.ui.main.HomeFragmentDirections
import org.jam.jmessenger.ui.misc.FriendInfoPopupFragment
import org.jam.jmessenger.widgets.ProfileRoundImageView
import java.lang.ref.PhantomReference
import java.lang.ref.WeakReference
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatsRecyclerViewAdapter(
    private var userConversationList: HashMap<String, RoomUser>,
    private val parentViewModel: ChatsHomeViewModel,
    private val parentNavController: NavController
): RecyclerView.Adapter<ChatsRecyclerViewAdapter.ChatsViewHolder>() {

    private val TAG = "ChatsRVAdapter"
    private lateinit var popupFragment: FriendChatInfoPopupFragment
    private lateinit var popupFragmentManager: FragmentManager
    private var userUID: String? = null

    fun updateList(data: HashMap<String, RoomUser>) {
        val tempList: HashMap<String, RoomUser> = data.clone() as HashMap<String, RoomUser>
        userConversationList = tempList
    }

    // START REGION: overrides
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ChatsRecyclerViewAdapter.ChatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chats_recycler_view_item, parent, false)
        popupFragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        popupFragment = FriendChatInfoPopupFragment(WeakReference(parentNavController), WeakReference(parentViewModel))
        userUID = parentViewModel.userInfo.value?.info?.uid
        return ChatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsRecyclerViewAdapter.ChatsViewHolder, position: Int) {
        val data = userConversationList[userConversationList.keys.elementAt(position)]
        if (data != null) {
            val weakHolder = WeakReference(holder)
            holder.userFriend = UserFriend(data.user)
            CoroutineScope(Dispatchers.IO).launch {
                val recentMessage = parentViewModel.getRecentMessage(data.user)
                val unreadMessages = parentViewModel.getUnreadCount(data.user)
                MainScope().launch {
                    populateUI(weakHolder.get()!!, data, recentMessage, unreadMessages)
                }
            }.start()
            holder.imageviewMute.visibility = View.INVISIBLE
            holder.textviewUnread.visibility = View.INVISIBLE
            holder.constLayout.setOnClickListener {
                if (userUID != null) {
                    parentNavController.navigate(
                        HomeFragmentDirections.actionHomeFragmentToChatFragment(
                            userUID.toString(), data.user
                        )
                    )
                }
            }
            holder.imageviewProfile.setOnClickListener {
                popupFragment.show(popupFragmentManager, holder.userFriend)
            }
        }
    }

    override fun getItemCount(): Int {
        return userConversationList.size
    }

    @SuppressLint("SimpleDateFormat")
    fun populateUI(holder: ChatsViewHolder, user: RoomUser, umessage: RoomMessage, unreadCount: Int) {
        holder.textviewStatus.text = umessage.text
        val format = SimpleDateFormat("yyyy/MM/dd HH:mm a")
        holder.textviewSendTime.text = format.format(Date(umessage.sendtime!!))
        parentViewModel.loadUserFriendInfo(user.user, holder.textviewName)
        parentViewModel.loadUserFriendProfileImage(user.user, holder.imageviewProfile)
        parentViewModel.checkIsMute(user.user, holder.imageviewMute)
        if (unreadCount > 0) {
            holder.textviewUnread.text = unreadCount.toString()
            holder.textviewUnread.visibility = View.VISIBLE
        } else {
            holder.textviewUnread.visibility = View.INVISIBLE
        }
    }
    // END REGION

    inner class ChatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var TAG = "ChatsViewHolder"
        var imageviewProfile: ProfileRoundImageView = itemView.findViewById(R.id.chatsItem_ImageView_Profile)
        var textviewName: TextView = itemView.findViewById(R.id.chatsItem_TextView_Name)
        var textviewStatus: TextView = itemView.findViewById(R.id.chatsItem_TextView_status)
        var textviewSendTime: TextView = itemView.findViewById(R.id.chatsItem_TextView_sendTime)
        var imageviewMute: ImageView = itemView.findViewById(R.id.chatsItem_ImageView_mute)
        var textviewUnread: TextView = itemView.findViewById(R.id.chatsItem_TextView_unreadcount)
        var constLayout: ConstraintLayout = itemView.findViewById(R.id.chatsItem_ConstLayout)
        var userFriend = UserFriend()
    }
}