package cbi.androidapp.myapplication.repository

import cbi.androidapp.myapplication.room.ItemDao
import cbi.androidapp.myapplication.room.ItemEntity
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    val allItems: Flow<List<ItemEntity>> = itemDao.getAllItems()

    suspend fun insert(item: ItemEntity) = itemDao.insertItem(item)
    suspend fun update(item: ItemEntity) = itemDao.updateItem(item)
    suspend fun delete(item: ItemEntity) = itemDao.deleteItem(item)
    suspend fun clearAll() = itemDao.clearAll()
}
