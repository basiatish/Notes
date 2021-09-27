package com.example.newnotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.newnotes.data.Item
import com.example.newnotes.databinding.FragmentNotesListBinding


class NoteListFragment() : Fragment(), OnItemClickListener {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((activity?.application as NoteApplication).database.itemDao())
    }

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NoteListAdapter(this)
        binding.recyclerView.adapter = adapter
        viewModel.allNotes.observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                anyNote(notes)
                adapter.submitList(it)
            }
        }
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager

        binding.addNewNote.setOnClickListener {
            val action = NoteListFragmentDirections.actionNotesListToAddNoteFragment(getString(R.string.add_note), -1)
            this.findNavController().navigate(action)
        }
    }

    private fun anyNote(item: List<Item>) {
        if (item.isEmpty()) {
            binding.startCard.visibility = VISIBLE
        } else {
            binding.startCard.visibility = GONE
        }
    }

    override fun onItemClick(item: Item) {
        val action = NoteListFragmentDirections.actionNotesListToAddNoteFragment(getString(R.string.edit_note), item.id)
        this.findNavController().navigate(action)
    }
}