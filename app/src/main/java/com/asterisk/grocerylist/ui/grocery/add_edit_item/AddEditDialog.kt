package com.asterisk.grocerylist.ui.grocery.add_edit_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.asterisk.grocerylist.databinding.AddEditDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditDialog : BottomSheetDialogFragment() {

    private val viewModel by viewModels<AddEditViewModel>()
    private val navArgs: AddEditDialogArgs by navArgs()

    private var _binding: AddEditDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddEditDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = navArgs.item
        binding.apply {
            etItemName.setText(viewModel.itemName)
            btnAdd.text = if (item != null) "Edit Item" else "Add Item"
            tvTitle.text = if (item != null) "Edit Item" else "Add Item"
            itemCount.text = if (item != null) viewModel.itemCount.toString() else (1).toString()

            etItemName.addTextChangedListener {
                viewModel.itemName = it.toString()
            }

            btnAdd.setOnClickListener {
                viewModel.saveItem()
                findNavController().popBackStack()
            }

            increase.setOnClickListener {
                itemCount.text = viewModel.increaseQuantity().toString()
            }

            decrease.setOnClickListener {
                itemCount.text = viewModel.decreaseQuantity().toString()
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}