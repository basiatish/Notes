package com.example.newnotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.newnotes.data.Item
import com.example.newnotes.databinding.FragmentNotesListBinding


class NoteListFragment : Fragment(), OnItemClickListener {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((activity?.application as NoteApplication).database.itemDao())
    }

    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { NoteListAdapter(this) }

    private var deleteViewActive = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        onBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllNotes()
        binding.recyclerView.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager

        binding.addNewNote.setOnClickListener {
            val action = NoteListFragmentDirections.actionNotesListToAddNoteFragment(getString(R.string.add_note), -1)
            this.findNavController().navigate(action)
        }
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (deleteViewActive) {
            inflater.inflate(R.menu.options_menu, menu)
        } else {
            inflater.inflate(R.menu.notes_list_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                deleteViewActive = true
                (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.delete_note)
                requireActivity().invalidateOptionsMenu()
                adapter.isSelectionStatedChanger(true)
                binding.addNewNote.hide()
                true
            }
            R.id.yes -> {
                deleteViewActive = false
                binding.addNewNote.show()
                (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
                requireActivity().invalidateOptionsMenu()
                adapter.isSelectionStatedChanger(false)
                viewModel.deleteSelectedNotes()
                viewModel.clearSelection()
                true
            }
            R.id.no -> {
                deleteViewActive = false
                binding.addNewNote.show()
                (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
                requireActivity().invalidateOptionsMenu()
                adapter.isSelectionStatedChanger(false)
                resetSelection()
                true
            }
            else -> {false}
        }
    }

    private fun setupObservers() {
        viewModel.notes.observe(this.viewLifecycleOwner) {
            haveAnyNote(it)
            adapter.submitList(it)
        }
        viewModel.deleteStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == Status.DONE) {
                viewModel.getAllNotes()
            }
        }
    }

    private fun resetSelection() {
        viewModel.getSelectedNotesPositions().forEach {
            adapter.notifyItemChanged(it)
        }
        viewModel.clearSelection()
    }

    private fun haveAnyNote(item: List<Item>) {
        if (item.isEmpty()) {
            binding.startCard.visibility = VISIBLE
        } else {
            binding.startCard.visibility = GONE
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (deleteViewActive) {
                    deleteViewActive = false
                    binding.addNewNote.show()
                    adapter.isSelectionStatedChanger(false)
                    resetSelection()
                    (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
                    requireActivity().invalidateOptionsMenu()
                } else {
                    activity?.finish()
                }
            }
        })
    }

    override fun onItemClick(item: Item) {
        val action = NoteListFragmentDirections.actionNotesListToAddNoteFragment(getString(R.string.edit_note), item.id)
        this.findNavController().navigate(action)
    }

    override fun onItemSelection(item: Item, isSelected: Boolean, position: Int) {
        if (isSelected) viewModel.addSelectedNode(item, position)
        else viewModel.retrieveSelectedNote(item, position)
    }
}