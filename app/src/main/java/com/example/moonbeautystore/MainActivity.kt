package com.example.moonbeautystore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
// Importa tus Activities. Si alguna sale en rojo, verifica que el archivo .kt exista
import com.example.moonbeautystore.ui.ProductListActivity
import com.example.moonbeautystore.ui.CartActivity
import com.example.moonbeautystore.ui.UserListActivity
// import com.example.moonbeautystore.ProfileActivity // Descomenta si tienes esta activity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Configurar Botones ---

        // 1. Ir a Productos
        findViewById<Button>(R.id.btnGoToProducts).setOnClickListener {
            // Asegúrate de tener ProductListActivity creada
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        // 2. Ir al Carrito
        findViewById<Button>(R.id.btnGoToCart).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // 3. Ir al Perfil
        findViewById<Button>(R.id.btnGoToProfile).setOnClickListener {
            // val intent = Intent(this, ProfileActivity::class.java)
            // startActivity(intent)
        }

        // 4. Ir a Gestión de Usuarios
        findViewById<Button>(R.id.btnGoToUsers).setOnClickListener {
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }

        // 5. Cerrar Sesión (Volver al Login)
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            // Limpiar el historial para que no pueda volver atrás
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
