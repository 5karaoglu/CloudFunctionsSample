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

class ChatAdapter(private val uid: String, private val onClick: (Message) -> Unit) :
    ListAdapter<Message, ChatAdapter.ViewHolder>(CustomDiffCallback) {


    class ViewHolder(view: View, val onClick: (Message) -> Unit) : RecyclerView.ViewHolder(view) {
        private val MessageTextView: TextView = itemView.findViewById(R.id.tvContent)
        private val MessageImageView: ImageView = itemView.findViewById(R.id.iv)
        private var currentMessage: Message? = null

        init {
            itemView.setOnClickListener {
                currentMessage?.let {
                    onClick(it)
                }
            }
        }

        fun bind(Message: Message) {
            currentMessage = Message

            MessageTextView.text = Message.content
           /* if (Message.photoUrl != null) {
                *//*MessageImageView.setImageResource(Message.photoUrl)*//*
            } else {
                MessageImageView.setImageResource(R.drawable.ic_launcher_foreground)
            }*/
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (uid == getItem(position).receiverId) 1 else 0
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
        return ViewHolder(view,onClick)
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