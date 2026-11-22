package com.example.madventuretv.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madventuretv.R
import com.example.madventuretv.data.Channel
import com.example.madventuretv.data.Channels

/** Меню каналов (жанров). Один канал всегда выбран. */
class ChannelAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelHolder>() {

    private val items: List<Channel> = Channels.list
    private var selected = 0

    fun select(index: Int) {
        val previous = selected
        selected = index
        if (previous in items.indices) notifyItemChanged(previous)
        if (index in items.indices) notifyItemChanged(index)
    }

    fun selectedIndex(): Int = selected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        holder.bind(items[position], position == selected)
    }

    override fun getItemCount(): Int = items.size

    inner class ChannelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val label: TextView = itemView as TextView

        init {
            itemView.isFocusable = true
            itemView.isClickable = true
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) onClick(position)
            }
        }

        fun bind(channel: Channel, isSelected: Boolean) {
            label.text = channel.title
            itemView.isSelected = isSelected
        }
    }
}
