package com.example.moonbeautystore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id_item: Int = 0,
    val id_cliente: Int,
    val id_producto: Int,
    val cantidad: Int
)
