package com.besirkaraoglu.cloudfunctionssample.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.besirkaraoglu.cloudfunctionssample.R
import com.besirkaraoglu.cloudfunctionssample.model.Message
import com.besirkaraoglu.cloudfunctionssample.model.User
import com.bumptech.glide.Glide

class ChatAdapter(private val currentUser: User, private val receiver: User, private val onClick: (Message) -> Unit) :
    ListAdapter<Message, ChatAdapter.ViewHolder>(CustomDiffCallback) {


    class ViewHolder(private val view: View,private val currentUser: User, private val receiver: User, val onClick: (Message) -> Unit) : RecyclerView.ViewHolder(view) {
        private val messageTextView: TextView = itemView.findViewById(R.id.tvContent)
        private val messageImageView: ImageView = itemView.findViewById(R.id.iv)
        private var currentMessage: Message? = null

        init {
            itemView.setOnClickListener {
                currentMessage?.let {
                    onClick(it)
                }
            }
        }

        fun bind(message: Message) {
            currentMessage = message

            messageTextView.text = message.content
            val currentPhoto = if (message.senderId == currentUser.uid) currentUser.photoUrl else receiver.photoUrl
            Glide.with(view)
                .load(currentPhoto ?: "")
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(messageImageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentUser.uid == getItem(position).senderId) 0 else 1
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = when(viewType){
            0 -> LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.message_sent_item, viewGroup, false)
            else -> LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.message_received_item, viewGroup, false)
        }
        return ViewHolder(view,currentUser, receiver, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

}

object CustomDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.uid == newItem.uid
    }
}