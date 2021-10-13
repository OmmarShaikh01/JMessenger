package org.jam.jmessenger.ui.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
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
): PagingDataAdapter<RoomMessage, ChatRecycleViewAdapter.ChatViewHolder> (RoomMessageEntityDiff()) {

    private val TAG = "ChatRVAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_recycler_view_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val data: RoomMessage? = getItem(position)
        data?.let { roomMessage ->
            holder.setMessage(roomMessage)
        }
    }

    class RoomMessageEntityDiff : DiffUtil.ItemCallback<RoomMessage>() {
        override fun areItemsTheSame(oldItem: RoomMessage, newItem: RoomMessage): Boolean = oldItem.mid == newItem.mid
        override fun areContentsTheSame(oldItem: RoomMessage, newItem: RoomMessage): Boolean = oldItem == newItem
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
            constLayout.setOnClickListener {
                if (userMessage.isVisible && message.text.toString().length < 3500) {
                    userMessage.maxLines += 10
                }
                if (friendMessage.isVisible && message.text.toString().length < 3500) {
                    friendMessage.maxLines += 10
                }
            }
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