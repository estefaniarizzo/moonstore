package com.example.moonbeautystore.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Obtener un ítem específico por cliente + producto
    @Query("SELECT * FROM carrito WHERE id_cliente = :clientId AND id_producto = :productId LIMIT 1")
    suspend fun getItem(clientId: Int, productId: Int): CartItem?

    // Obtener todo el carrito del cliente
    @Query("SELECT * FROM carrito WHERE id_cliente = :clientId")
    fun getCartForClient(clientId: Int): Flow<List<CartItem>>

    // Insertar ítem nuevo (sin REPLACE)
    @Insert
    suspend fun insertItem(item: CartItem): Long

    // Actualizar ítem existente
    @Update
    suspend fun updateItem(item: CartItem)

    // Eliminar ítem del carrito
    @Delete
    suspend fun deleteItem(item: CartItem)

    // Vaciar carrito
    @Query("DELETE FROM carrito WHERE id_cliente = :clientId")
    suspend fun clearCart(clientId: Int)

    // --------------------------
    // 🚀 FUNCIÓN CLAVE: ADD TO CART
    // --------------------------
    suspend fun addToCart(clientId: Int, productId: Int) {
        val existingItem = getItem(clientId, productId)

        if (existingItem != null) {
            // Si ya existe el producto → aumentar cantidad
            val updated = existingItem.copy(cantidad = existingItem.cantidad + 1)
            updateItem(updated)
        } else {
            // Si es la primera vez → cantidad = 1
            val newItem = CartItem(
                id_cliente = clientId,
                id_producto = productId,
                cantidad = 1
            )
            insertItem(newItem)
        }
    }
}
