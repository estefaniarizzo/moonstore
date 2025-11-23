package com.example.moonbeautystore.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moonbeautystore.R
import com.example.moonbeautystore.data.Product

class ProductAdapter(
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id_producto == newItem.id_producto

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.textProductName)
        private val textDescription: TextView = itemView.findViewById(R.id.textProductDescription)
        private val textPrice: TextView = itemView.findViewById(R.id.textProductPrice)
        private val imageThumb: ImageView = itemView.findViewById(R.id.imageProductThumb)
        private val buttonAddToCart: ImageButton = itemView.findViewById(R.id.buttonAddToCart)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEditProduct)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDeleteProduct)

        fun bind(product: Product) {
            textName.text = product.nombre
            textDescription.text = product.descripcion
            textPrice.text = "${product.precio}"

            if (product.imagen_url != null) {
                val uri = android.net.Uri.parse(product.imagen_url)
                imageThumb.setImageURI(uri)
            } else {
                imageThumb.setImageDrawable(null)
            }

            buttonEdit.setOnClickListener { onEdit(product) }
            buttonDelete.setOnClickListener { onDelete(product) }
            buttonAddToCart.setOnClickListener { onAddToCart(product) }
        }
    }
}
