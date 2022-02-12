package com.asterisk.grocerylist.ui.grocery.grocery_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asterisk.grocerylist.data.Item
import com.asterisk.grocerylist.databinding.ListItemBinding

class GroceryListAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Item, GroceryListAdapter.ListViewHolder>(ITEM_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ListViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onItemClicked(item)
                    }
                }

                cbIsComplete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onCheckBoxClick(item, cbIsComplete.isChecked)
                    }
                }
            }
        }

        fun bind(item: Item) {
            binding.apply {
                cbIsComplete.isChecked = item.completed
                tvItemName.paint.isStrikeThruText = item.completed
                itemCount.text = item.quantity.toString()
                tvItemName.text = item.name
            }
        }

    }


    interface OnItemClickListener {
        fun onItemClicked(item: Item)
        fun onCheckBoxClick(item: Item, isChecked: Boolean)
    }

    companion object {

        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
                oldItem == newItem

        }

    }
}