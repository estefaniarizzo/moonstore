package com.example.moonbeautystore.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moonbeautystore.R
import com.example.moonbeautystore.data.CartItem
import com.example.moonbeautystore.data.Product

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DIFF_CALLBACK) {

    private var productsById: Map<Int, Product> = emptyMap()

    fun updateProducts(products: List<Product>) {
        productsById = products.associateBy { it.id_producto }
        notifyDataSetChanged()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
                oldItem.id_item == newItem.id_item

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.textCartProductName)
        private val textQuantity: TextView = itemView.findViewById(R.id.textCartQuantity)
        private val buttonIncrease: ImageButton = itemView.findViewById(R.id.buttonIncrease)
        private val buttonDecrease: ImageButton = itemView.findViewById(R.id.buttonDecrease)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDeleteFromCart)

        fun bind(item: CartItem) {
            val product = productsById[item.id_producto]
            textName.text = product?.nombre ?: "Producto"
            textQuantity.text = "Cantidad: ${item.cantidad}"

            buttonIncrease.setOnClickListener { onIncrease(item) }
            buttonDecrease.setOnClickListener { onDecrease(item) }
            buttonDelete.setOnClickListener { onDelete(item) }
        }
    }
}
