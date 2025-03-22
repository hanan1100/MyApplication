package cbi.androidapp.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemName: String,
    val stock: String,
    val unit: String
)
