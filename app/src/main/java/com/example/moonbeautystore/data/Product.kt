package com.example.moonbeautystore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Product(
    @PrimaryKey(autoGenerate = true) val id_producto: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    val imagen_url: String? = null,
    val stock: Int = 0
)
