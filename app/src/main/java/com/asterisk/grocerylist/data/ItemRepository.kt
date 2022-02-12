package com.asterisk.grocerylist.data

import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val itemDao: ItemDao
) {

    suspend fun insertItem(item: Item) = itemDao.insert(item)

    suspend fun deleteItem(item: Item) = itemDao.delete(item)

    suspend fun updateItem(item: Item) = itemDao.update(item)

    fun getItems(string: String) = itemDao.getItems(string)

}