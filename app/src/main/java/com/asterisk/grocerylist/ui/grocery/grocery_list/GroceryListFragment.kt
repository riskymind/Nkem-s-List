package com.asterisk.grocerylist.ui.grocery.grocery_list

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asterisk.grocerylist.R
import com.asterisk.grocerylist.data.Item
import com.asterisk.grocerylist.databinding.FragmentGroceryListBinding
import com.asterisk.grocerylist.utils.onQueryTextChange
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GroceryListFragment : Fragment(R.layout.fragment_grocery_list),
    GroceryListAdapter.OnItemClickListener {

    private var _binding: FragmentGroceryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GroceryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroceryListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)

        val listAdapter = GroceryListAdapter(this)

        binding.rvItems.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabAddItem.setOnClickListener {
            val action =
                GroceryListFragmentDirections.actionGroceryListFragmentToAddEditDialog(null)
            findNavController().navigate(action)
        }

        /** Swipe to delete operation */
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = listAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onSwipeDeleteItem(item)
            }

        }).attachToRecyclerView(binding.rvItems)

        viewModel.items.observe(viewLifecycleOwner) { list ->
            listAdapter.submitList(list.sortedBy { it.name })

            binding.apply {
                rvItems.isVisible = !list.isNullOrEmpty()
                tvEmpty.isVisible = list.isNullOrEmpty()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deleteEventChannel.collect { event ->
                when (event) {
                    is GroceryViewModel.DeleteEvent.ShowUndoDeleteItemMessage -> {
                        Snackbar.make(
                            requireView(),
                            "${event.item.name} Deleted",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClicked(event.item)
                            }.show()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemClicked(item: Item) {
        val action = GroceryListFragmentDirections.actionGroceryListFragmentToAddEditDialog(item)
        findNavController().navigate(action)
    }

    override fun onCheckBoxClick(item: Item, isChecked: Boolean) {
        viewModel.updateItem(item, isChecked)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChange {
            viewModel.searchQuery.value = it
        }
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Closing app")
                .setMessage("Do you want to close the app?")
                .setIcon(R.drawable.ic_list_alt)
                .setNegativeButton("NO") { _, _ ->

                }
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    activity?.finish()
                }
                .show()
        }
    }
}