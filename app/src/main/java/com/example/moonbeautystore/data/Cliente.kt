package com.example.moonbeautystore.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id_cliente: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val tipo_piel: String,
    val tipo_cabello: String,
    val rol: String = "cliente"
)
