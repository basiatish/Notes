package com.basiatish.notes

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.basiatish.notes.data.Item
import com.basiatish.notes.databinding.FragmentAddNoteBinding


class AddNoteFragment : Fragment() {

    private val navigationArgs: AddNoteFragmentArgs by navArgs()

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((activity?.application as NoteApplication).database.itemDao())
    }

    private var isSaveClicked = false
    private var isFabVisible = false
    private var prevSelectedColor: View? = null
    private var selectedColorViewId: Int = 0
    private var selectedColor: Int = 0
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabMove()
        colorPicker()
        val id = navigationArgs.id
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selected ->
                bind(selected)
            }
        } else {
            binding.saveBtn.setOnClickListener {
                isSaveClicked = true
                saveNote()
            }
            prevSelectedColor = binding.container.orange
            pickerStateChanger(binding.container.orange, binding.container.orange.isSelected)
            selectedColorViewId = binding.container.orange.id
            selectedColor = requireContext().getColor(R.color.orange_100)
        }
    }

    private fun fabMove() {
        binding.saveBtn.visibility = GONE
        binding.deleteBtn.visibility = GONE
        binding.save.shrink()
        binding.save.setOnClickListener {
            if (!isFabVisible) {
                binding.deleteBtn.show()
                binding.saveBtn.show()
                binding.save.extend()
                isFabVisible = true
            } else {
                binding.saveBtn.visibility = GONE
                binding.deleteBtn.visibility = GONE
                binding.save.shrink()
                isFabVisible = false
            }
        }
    }

    private fun bind(item: Item) {
        binding.apply {
            noteTitle.setText(item.title)
            noteDescription.setText(item.note)
            prevSelectedColor = requireActivity().findViewById(item.viewId)
            selectedColorViewId = item.viewId
            selectedColor = item.color
            pickerStateChanger(prevSelectedColor!!, prevSelectedColor!!.isSelected)
            saveBtn.setOnClickListener {
                updateNote(item)
            }
            binding.deleteBtn.setOnClickListener {
                deleteNote(item)
            }
        }
    }

    private fun colorPicker() {
        binding.container.orange.setOnClickListener {
            pickerStateChanger(binding.container.orange, binding.container.orange.isSelected)
            selectedColor = requireContext().getColor(R.color.orange_100)
            selectedColorViewId = binding.container.orange.id
        }
        binding.container.yellow.setOnClickListener {
            pickerStateChanger(binding.container.yellow, binding.container.yellow.isSelected)
            selectedColor = requireContext().getColor(R.color.yellow)
            selectedColorViewId = binding.container.yellow.id
        }
        binding.container.green.setOnClickListener {
            pickerStateChanger(binding.container.green, binding.container.green.isSelected)
            selectedColor = requireContext().getColor(R.color.green)
            selectedColorViewId = binding.container.green.id
        }
        binding.container.red.setOnClickListener {
            pickerStateChanger(binding.container.red, binding.container.red.isSelected)
            selectedColor = requireContext().getColor(R.color.red)
            selectedColorViewId = binding.container.red.id
        }
        binding.container.blue.setOnClickListener {
            pickerStateChanger(binding.container.blue, binding.container.blue.isSelected)
            selectedColor = requireContext().getColor(R.color.blue)
            selectedColorViewId = binding.container.blue.id
        }
        binding.container.pink.setOnClickListener {
            pickerStateChanger(binding.container.pink, binding.container.pink.isSelected)
            selectedColor = requireContext().getColor(R.color.pink)
            selectedColorViewId = binding.container.pink.id
        }
    }

    private fun pickerStateChanger(view: View, selection: Boolean) {
        if (!selection) {
            view.isSelected = true
            prevSelectedColor?.isSelected = false
            val expandAnim = AnimationUtils.loadAnimation(context, R.anim.expand)
            expandAnim.fillAfter = true
            val collapseAnim = AnimationUtils.loadAnimation(context, R.anim.collapse)
            collapseAnim.fillAfter = true
            prevSelectedColor?.startAnimation(collapseAnim)
            view.startAnimation(expandAnim)
            prevSelectedColor = view
        }
    }

    private fun deleteNote(item: Item) {
        if (isEntryNull()) {
            viewModel.delete(item)
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesList()
            findNavController().navigate(action)
        }
    }

    private fun saveNote() {
        if (isEntryNull()) {
            viewModel.addNewNote(
                binding.noteTitle.text.toString(),
                binding.noteDescription.text.toString(),
                selectedColorViewId,
                selectedColor
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesList()
            findNavController().navigate(action)
        } else {
            val toast = Toast.makeText(this.context, getString(R.string.nothing_save), Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun updateNote(item: Item) {
        if (isEntryNull()) {
            viewModel.updateNote(
                item.id,
                binding.noteTitle.text.toString(),
                binding.noteDescription.text.toString(),
                selectedColorViewId,
                selectedColor
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesList()
            findNavController().navigate(action)
        }
    }

    private fun isEntryNull(): Boolean {
        return viewModel.isEntryNull(
            binding.noteTitle.text.toString(),
            binding.noteDescription.text.toString())
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