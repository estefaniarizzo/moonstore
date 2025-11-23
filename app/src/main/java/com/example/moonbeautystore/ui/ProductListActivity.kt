package com.example.moonbeautystore.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonbeautystore.R
import com.example.moonbeautystore.data.AppDatabase
import com.example.moonbeautystore.data.CartItem
import com.example.moonbeautystore.data.Product
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val clientIdDemo = 1

    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerView = findViewById(R.id.recyclerProducts)
        val fabAdd: View = findViewById(R.id.fabAddProduct)
        val buttonViewCart: Button = findViewById(R.id.buttonViewCart)

        val db = AppDatabase.getInstance(this)
        val productDao = db.productDao()
        val cartDao = db.cartDao()

        adapter = ProductAdapter(
            onEdit = { product -> showProductDialog(productDao, product) },
            onDelete = { product ->
                lifecycleScope.launch {
                    productDao.deleteProduct(product)
                }
            },
            onAddToCart = { product ->
                lifecycleScope.launch {

                    // Obtener los items del carrito UNA SOLA VEZ
                    val items = cartDao.getCartForClient(clientIdDemo).first()

                    // Buscar si ya existe
                    val existing = items.firstOrNull { it.id_producto == product.id_producto }

                    if (existing == null) {
                        cartDao.insertItem(
                            CartItem(
                                id_cliente = clientIdDemo,
                                id_producto = product.id_producto,
                                cantidad = 1
                            )
                        )
                    } else {
                        cartDao.updateItem(
                            existing.copy(
                                cantidad = existing.cantidad + 1
                            )
                        )
                    }
                }

                Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            // Insertar productos iniciales si la tabla está vacía
            val count = productDao.getProductCount()
            if (count == 0) {
                productDao.insertProduct(
                    Product(
                        nombre = "Labial Mate Rosa",
                        descripcion = "Labial de larga duración acabado mate.",
                        precio = 29900.0,
                        categoria = "Maquillaje"
                    )
                )
                productDao.insertProduct(
                    Product(
                        nombre = "Sérum Hidratante",
                        descripcion = "Sérum facial con ácido hialurónico.",
                        precio = 59900.0,
                        categoria = "Skincare"
                    )
                )
                productDao.insertProduct(
                    Product(
                        nombre = "Shampoo Nutritivo",
                        descripcion = "Shampoo para cabello seco con aceites naturales.",
                        precio = 39900.0,
                        categoria = "Cabello"
                    )
                )
            }

            productDao.getAllProducts().collectLatest { products ->
                adapter.submitList(products)
            }
        }

        fabAdd.setOnClickListener {
            showProductDialog(productDao, null)
        }

        buttonViewCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun showProductDialog(productDao: com.example.moonbeautystore.data.ProductDao, product: Product?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (product == null) "Nuevo producto" else "Editar producto")

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_product, null)
        val editName: EditText = view.findViewById(R.id.editProductName)
        val editDescription: EditText = view.findViewById(R.id.editProductDescription)
        val editPrice: EditText = view.findViewById(R.id.editProductPrice)
        val editCategory: EditText = view.findViewById(R.id.editProductCategory)
        val buttonTakePhoto: Button = view.findViewById(R.id.buttonTakePhoto)
        val imagePreview: android.widget.ImageView = view.findViewById(R.id.imagePreview)

        var imagePath: String? = product?.imagen_url

        if (imagePath != null) {
            val uri = Uri.parse(imagePath)
            imagePreview.setImageURI(uri)
        }

        if (product != null) {
            editName.setText(product.nombre)
            editDescription.setText(product.descripcion)
            editPrice.setText(product.precio.toString())
            editCategory.setText(product.categoria)
        }

        buttonTakePhoto.setOnClickListener {
            // pedir permiso
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1001)
            } else {
                val photoFile = java.io.File(
                    getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
                    "product_${System.currentTimeMillis()}.jpg"
                )
                val uri = FileProvider.getUriForFile(
                    this,
                    "com.example.moonbeautystore.fileprovider",
                    photoFile
                )
                currentPhotoUri = uri
                imagePath = uri.toString()

                val takePictureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
                cameraLauncher.launch(takePictureIntent)

                imagePreview.setImageURI(uri)
            }
        }

        builder.setView(view)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val name = editName.text.toString().trim()
            val desc = editDescription.text.toString().trim()
            val priceText = editPrice.text.toString().trim()
            val category = editCategory.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || priceText.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val price = priceText.toDoubleOrNull()
            if (price == null) {
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            lifecycleScope.launch {
                if (product == null) {
                    productDao.insertProduct(
                        Product(
                            nombre = name,
                            descripcion = desc,
                            precio = price,
                            categoria = category,
                            imagen_url = imagePath
                        )
                    )
                } else {
                    productDao.updateProduct(
                        product.copy(
                            nombre = name,
                            descripcion = desc,
                            precio = price,
                            categoria = category,
                            imagen_url = imagePath
                        )
                    )
                }
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }
}
