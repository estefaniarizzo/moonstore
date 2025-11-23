package com.example.moonbeautystore

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moonbeautystore.data.AppDatabase
import com.example.moonbeautystore.data.Cliente
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val splashDuration: Long = 2000 // 2 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Sembrar un usuario administrador por defecto si no existe
        val db = AppDatabase.getInstance(this)
        val clienteDao = db.clienteDao()

        lifecycleScope.launch {
            val admin = clienteDao.getAdmin()
            if (admin == null) {
                val adminCliente = Cliente(
                    nombre = "Administrador",
                    email = "admin@moonbeautystore.com",
                    password = "admin123",
                    telefono = null,
                    tipo_piel = "Mixta",
                    tipo_cabello = "Liso",
                    rol = "admin"
                )
                clienteDao.insertCliente(adminCliente)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuration)
    }
}
