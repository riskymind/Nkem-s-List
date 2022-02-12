package com.asterisk.grocerylist.ui.grocery.add_edit_item

import androidx.lifecycle.*
import com.asterisk.grocerylist.data.Item
import com.asterisk.grocerylist.data.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val addEditChannel = Channel<AddEditEvent>()
    val addEditEventChannel = addEditChannel.receiveAsFlow()


    val item = state.get<Item>("item")

    var itemName = state.get<String>("itemName") ?: item?.name ?: ""
        set(value) {
            field = value
            state.set("itemName", value)
        }

    var itemCount = state.get<Int>("itemCount") ?: item?.quantity ?: 1
        set(value) {
            field = value
            state.set("itemCount", value)
        }

    var itemCompleted = state.get<Boolean>("itemCompleted") ?: item?.completed ?: false
        set(value) {
            field = value
            state.set("itemCompleted", value)
        }

    private fun insertItem(item: Item) = viewModelScope.launch {
        repository.insertItem(item)
    }

    private fun updateItem(item: Item) = viewModelScope.launch {
        repository.updateItem(item)
    }

    fun saveItem() {
        if (itemName.isBlank()) {
            return
        }

        if (item != null) {
            val updateItem = item.copy(name = itemName, quantity = itemCount)
            updateItem(updateItem)
        } else {
            val newItem = Item(name = itemName, quantity = itemCount)
            insertItem(newItem)
        }
    }

    fun increaseQuantity(): Int {
        if (itemCount >= 1) {
            itemCount++
        }
        return itemCount
    }

    fun decreaseQuantity(): Int {
        if (itemCount == 1) {
            return 1
        }
        itemCount--
        return itemCount
    }


    sealed class AddEditEvent {
        data class ShowErrorMessages(val msg: String) : AddEditEvent()
    }
}

