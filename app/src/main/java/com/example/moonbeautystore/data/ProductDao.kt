package com.example.moonbeautystore.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM productos ORDER BY nombre")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun getProductCount(): Int

    @Query("SELECT * FROM productos WHERE id_producto = :id LIMIT 1")
    suspend fun getProductById(id: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)
}
