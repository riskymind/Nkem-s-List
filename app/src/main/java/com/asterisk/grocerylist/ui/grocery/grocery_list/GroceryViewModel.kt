package com.asterisk.grocerylist.ui.grocery.grocery_list

import androidx.lifecycle.*
import com.asterisk.grocerylist.data.Item
import com.asterisk.grocerylist.data.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceryViewModel @Inject constructor(
    private val repository: ItemRepository
) : ViewModel() {

    private val deleteChannel = Channel<DeleteEvent>()
    val deleteEventChannel = deleteChannel.receiveAsFlow()

    fun updateItem(item: Item, checked: Boolean) = viewModelScope.launch {
        repository.updateItem(item.copy(completed = checked))
    }

    // On swipe delete action method
    fun onSwipeDeleteItem(item: Item) = viewModelScope.launch {
        repository.deleteItem(item)
        deleteChannel.send(DeleteEvent.ShowUndoDeleteItemMessage(item))
    }

    fun onUndoDeleteClicked(item: Item) = viewModelScope.launch {
        repository.insertItem(item)
    }


    val searchQuery = MutableStateFlow("")
    private val itemFlow = searchQuery.flatMapLatest {
        repository.getItems(it)
    }
    val items = itemFlow.asLiveData()

    sealed class DeleteEvent {
        data class ShowUndoDeleteItemMessage(val item: Item) : DeleteEvent()
    }
}