package org.jam.jmessenger.ui.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.jam.jmessenger.R
import org.jam.jmessenger.data.db.entity.RoomMessage
import org.jam.jmessenger.data.db.entity.RoomUser
import org.jam.jmessenger.data.db.entity.UserFriend
import org.jam.jmessenger.widgets.ProfileRoundImageView
import java.lang.ref.WeakReference


class ChatRecycleViewAdapter(
    private val userUID: String,
    private val parentViewModel: ChatViewModel,
    private var userConversationList: HashMap<String, RoomMessage>
): RecyclerView.Adapter<ChatRecycleViewAdapter.ChatViewHolder>() {

    private val TAG = "ChatRVAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_recycler_view_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val data = userConversationList[userConversationList.keys.elementAt(position)]
        data?.let { roomMessage ->
            holder.setMessage(roomMessage)
        }
    }

    override fun getItemCount(): Int {
        return userConversationList.size
    }

    fun updateList(data: HashMap<String, RoomMessage>) {
        val tempList: HashMap<String, RoomMessage> = data.clone() as HashMap<String, RoomMessage>
        userConversationList = tempList
    }

    inner class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var TAG = "ChatViewHolder"
        var constLayout: ConstraintLayout = itemView.findViewById(R.id.message_ConstLayout)
        private var userMessage: TextView = itemView.findViewById(R.id.message_UserChat)
        private var friendMessage: TextView = itemView.findViewById(R.id.message_FriendChat)
        private lateinit var message: RoomMessage

        init {
            userMessage.visibility = View.INVISIBLE
            friendMessage.visibility = View.INVISIBLE
        }

        fun setMessage(msg: RoomMessage) {
            message = msg
            if (msg.sender == userUID) {
                userMessage.text = msg.text
                friendMessage.visibility = View.INVISIBLE
                userMessage.visibility = View.VISIBLE
            } else if (msg.receiver == userUID) {
                friendMessage.text = msg.text
                friendMessage.visibility = View.VISIBLE
                userMessage.visibility = View.INVISIBLE
            } else {
                Log.e(TAG, "IllegalMessage")
            }
        }
    }
}