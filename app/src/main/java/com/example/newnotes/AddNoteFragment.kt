package com.example.newnotes

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.newnotes.data.Item
import com.example.newnotes.databinding.FragmentAddNoteBinding


class AddNoteFragment : Fragment() {

    private val navigationArgs: AddNoteFragmentArgs by navArgs()

    private lateinit var item: Item

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((activity?.application as NoteApplication).database.itemDao())
    }

    private var saveClicked = false
    private var fabVisible: Boolean = false
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabMove()
        val id = navigationArgs.id
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selected ->
                item = selected
                bind(item)
            }
        }
        else {
            binding.saveBtn.setOnClickListener {
                saveClicked = true
                saveNote()
            }
        }
    }

    private fun fabMove() {
        binding.saveBtn.visibility = GONE
        binding.deleteBtn.visibility = GONE
        binding.save.shrink()
        binding.save.setOnClickListener {
            if (!fabVisible) {
                binding.deleteBtn.show()
                binding.saveBtn.show()
                binding.save.extend()
                fabVisible = true
            } else {
                binding.saveBtn.visibility = GONE
                binding.deleteBtn.visibility = GONE
                binding.save.shrink()
                fabVisible = false
            }
        }
    }

    private fun bind(item: Item) {
        binding.apply {
            noteTitle.setText(item.title)
            noteDescription.setText(item.note)
            saveBtn.setOnClickListener {
                updateNote()
            }
            binding.deleteBtn.setOnClickListener {
                deleteNote()
            }
        }
    }

    private fun deleteNote() {
        if (isEntryNull()) {
            viewModel.delete(item)
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesList()
            findNavController().navigate(action)
        }
    }

    private fun saveNote() {
        if (isEntryNull()) {
            viewModel.addNewNote(binding.noteTitle.text.toString(), binding.noteDescription.text.toString())
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesList()
            findNavController().navigate(action)
        } else {
            val toast = Toast.makeText(this.context, getString(R.string.nothing_save), Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun updateNote() {
        if (isEntryNull()) {
            viewModel.updateNote(item.id, binding.noteTitle.text.toString(), binding.noteDescription.text.toString())
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesList()
            findNavController().navigate(action)
        }
    }

    private fun isEntryNull(): Boolean {
        return viewModel.isEntryNull(
            binding.noteTitle.text.toString(),
            binding.noteDescription.text.toString(),)
    }

    override fun onStop() {
        super.onStop()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}