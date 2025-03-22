package cbi.androidapp.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.*
import cbi.androidapp.myapplication.repository.ItemRepository
import cbi.androidapp.myapplication.room.AppDatabase
import cbi.androidapp.myapplication.room.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import cbi.androidapp.myapplication.network.*

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private val itemDao = AppDatabase.getDatabase(application).itemDao()
    private val repository = ItemRepository(itemDao)

    val allItems = repository.allItems.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun insert(item: ItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(item)
    }

    fun update(item: ItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(item)
    }

    fun delete(item: ItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(item)
    }

    fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearAll()
    }

    // ✅ Perbaiki fungsi update agar tidak menambahkan data baru
    fun updateItem(updatedItem: ItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        val currentItems = allItems.value
        val existingItem = currentItems.find { it.itemName == updatedItem.itemName }
        if (existingItem != null) {
            repository.update(updatedItem.copy(id = existingItem.id)) // ✅ Pastikan pakai ID yang ada
        }
    }

    // ✅ Tambahkan fungsi refresh data dari API
    fun refreshData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ApiClient.instance.getListItems("Bearer $token").enqueue(object : Callback<ListItemsResponse> {
                override fun onResponse(call: Call<ListItemsResponse>, response: Response<ListItemsResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it.statusCode == 1) {
                                viewModelScope.launch(Dispatchers.IO) {
                                    clearAll() // Hapus semua data lama
                                    it.data.forEach { item ->
                                        insert(ItemEntity(itemName = item.item_name, stock = item.stock, unit = item.unit))
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ListItemsResponse>, t: Throwable) {}
            })
        }
    }
}
