package com.example.moonbeautystore.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonbeautystore.R
import com.example.moonbeautystore.data.AppDatabase
import com.example.moonbeautystore.data.CartItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerCart: RecyclerView
    private lateinit var textTotal: TextView
    private lateinit var adapter: CartAdapter

    private val clientIdDemo = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerCart = findViewById(R.id.recyclerCart)
        textTotal = findViewById(R.id.textCartTotal)

        val db = AppDatabase.getInstance(this)
        val cartDao = db.cartDao()
        val productDao = db.productDao()

        adapter = CartAdapter(
            onIncrease = { item ->
                lifecycleScope.launch {
                    cartDao.updateItem(item.copy(cantidad = item.cantidad + 1))
                }
            },
            onDecrease = { item ->
                lifecycleScope.launch {
                    if (item.cantidad > 1) {
                        cartDao.updateItem(item.copy(cantidad = item.cantidad - 1))
                    } else {
                        cartDao.deleteItem(item)
                    }
                }
            },
            onDelete = { item ->
                lifecycleScope.launch {
                    cartDao.deleteItem(item)
                }
            }
        )

        recyclerCart.layoutManager = LinearLayoutManager(this)
        recyclerCart.adapter = adapter

        lifecycleScope.launch {
            cartDao.getCartForClient(clientIdDemo).collectLatest { items ->
                adapter.submitList(items)

                // Actualizar productos en el adaptador
                val products = productDao.getAllProducts().first()
                adapter.updateProducts(products)

                // Calcular total
                val total = items.sumOf { item ->
                    val p = products.firstOrNull { it.id_producto == item.id_producto }
                    (p?.precio ?: 0.0) * item.cantidad
                }

                textTotal.text = "Total: $${"%.2f".format(total)}"
            }
        }
    }
}
