package com.example.newnotes

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newnotes.data.Item
import com.example.newnotes.databinding.NoteListCardBinding
import java.util.*

class NoteListAdapter(private val clickListener: OnItemClickListener): ListAdapter<Item,
        NoteListAdapter.NoteListViewHolder>(DiffCallBack) {

    class NoteListViewHolder(private val binding: NoteListCardBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.apply {
                when (item.title) {
                    "" -> title.setText(R.string.untitled)
                    else -> title.text = item.title
                }
                when (item.note) {
                    "" -> noteBody.setText(R.string.type_something)
                    else -> noteBody.text = item.note
                }
                date.text = item.date
                color.setBackgroundColor(Color.argb(255, 255,
                    Random().nextInt(256), Random().nextInt(256)))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        return NoteListViewHolder(
            NoteListCardBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(current)
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}

interface OnItemClickListener {
    fun onItemClick(item: Item)
}