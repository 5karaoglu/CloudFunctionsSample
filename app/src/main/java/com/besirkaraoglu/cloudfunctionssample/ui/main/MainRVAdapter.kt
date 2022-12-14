package com.besirkaraoglu.cloudfunctionssample.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.besirkaraoglu.cloudfunctionssample.R
import com.besirkaraoglu.cloudfunctionssample.model.User

class MainRVAdapter(private val onClick: (User) -> Unit) :
    ListAdapter<User, MainRVAdapter.ViewHolder>(CustomDiffCallback) {


    class ViewHolder(view: View, val onClick: (User) -> Unit) : RecyclerView.ViewHolder(view) {
        private val userTextView: TextView = itemView.findViewById(R.id.tvName)
        private val userImageView: ImageView = itemView.findViewById(R.id.iv)
        private var currentUser: User? = null

        init {
            itemView.setOnClickListener {
                currentUser?.let {
                    onClick(it)
                }
            }
        }
        
        fun bind(user: User) {
            currentUser = user

            userTextView.text = user.name
            if (user.photoUrl != null) {
                /*userImageView.setImageResource(user.photoUrl)*/
            } else {
                userImageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.main_user_item, viewGroup, false)

        return ViewHolder(view,onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

}

object CustomDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.uid == newItem.uid
    }
}