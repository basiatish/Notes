package com.basiatish.notes

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.basiatish.notes.data.Item
import com.basiatish.notes.databinding.NoteListCardBinding

class NoteListAdapter(private val clickListener: OnItemClickListener): ListAdapter<Item,
        NoteListAdapter.NoteListViewHolder>(DiffCallBack) {

    private var selection: Boolean = false
    private var numSelected: Int = 0

    inner class NoteListViewHolder(private val binding: NoteListCardBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Item) {
            binding.apply {
                cardView.isSelected = false
                when (note.title) {
                    "" -> title.setText(R.string.untitled)
                    else -> title.text = note.title
                }
                when (note.note) {
                    "" -> noteBody.setText(R.string.type_something)
                    else -> noteBody.text = note.note
                }
                date.text = note.date
                colorDot.backgroundTintList = ColorStateList.valueOf(note.color)
                val cardBackground = cardView.background.mutate() as (GradientDrawable)
                cardBackground.setStroke(2, note.color)
            }
        }

        fun selection(note: Item, context: Context, position: Int) {
            val isSelected = binding.cardView.isSelected
            if (isSelected) {
                Log.i("Remove", "Selected before")
                binding.cardView.animation.cancel()
                binding.cardView.isSelected = false
                numSelected--
                clickListener.onItemSelection(note, false, position)
            } else {
                Log.i("Remove", "New selection")
                binding.cardView.isSelected = true
                numSelected++
                val anim = AnimationUtils.loadAnimation(context, R.anim.tremble)
                binding.cardView.startAnimation(anim)
                Log.i("Remove", "Num selected: $numSelected")
                clickListener.onItemSelection(note, true, position)
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
        holder.bind(current)
        holder.itemView.setOnClickListener {
            if (!selection) {
                clickListener.onItemClick(current)
            } else {
                holder.selection(current, holder.itemView.context, position)
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<Item>,
        currentList: MutableList<Item>
    ) {
        selection = false
        numSelected = 0
    }

    fun isSelectionStatedChanger(state: Boolean) {
        selection = state
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(item: Item)
    fun onItemSelection(item: Item, isSelected: Boolean, position: Int)
}