package com.example.moonbeautystore.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCliente(cliente: Cliente): Long

    @Query("SELECT * FROM clientes WHERE email = :email LIMIT 1")
    suspend fun getClienteByEmail(email: String): Cliente?

    @Query("SELECT * FROM clientes WHERE rol = 'admin' LIMIT 1")
    suspend fun getAdmin(): Cliente?

    @Query("SELECT * FROM clientes")
    fun getAllClientes(): Flow<List<Cliente>>

    @Update
    suspend fun updateCliente(cliente: Cliente)

    @Delete
    suspend fun deleteCliente(cliente: Cliente)
}
