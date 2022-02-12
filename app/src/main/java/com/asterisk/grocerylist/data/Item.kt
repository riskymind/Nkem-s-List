package com.asterisk.grocerylist.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Parcelize
@Entity(tableName = "item_table")
data class Item(
    @ColumnInfo(name = "item_name")
    val name: String,
    @ColumnInfo(name = "item_completed")
    val completed: Boolean = false,
    @ColumnInfo(name = "item_quantity")
    val quantity: Int,
    @ColumnInfo(name = "item_createAt")
    val createdAt: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    val id: Int = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(createdAt)
}