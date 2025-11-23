package com.example.moonbeautystore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moonbeautystore.data.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextLoginEmail: EditText
    private lateinit var editTextLoginPassword: EditText
    private lateinit var checkBoxRememberEmail: CheckBox
    private lateinit var textLoginError: TextView
    private lateinit var buttonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        prefillEmailIfAvailable()
        setupListeners()
    }

    private fun initViews() {
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail)
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword)
        checkBoxRememberEmail = findViewById(R.id.checkBoxRememberEmail)
        textLoginError = findViewById(R.id.textLoginError)
        buttonLogin = findViewById(R.id.buttonLogin)
    }

    private fun prefillEmailIfAvailable() {
        val emailFromRegister = intent.getStringExtra("email")
        if (!emailFromRegister.isNullOrEmpty()) {
            editTextLoginEmail.setText(emailFromRegister)
        }
    }

    private fun setupListeners() {
        buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        textLoginError.visibility = View.GONE

        val email = editTextLoginEmail.text.toString().trim()
        val password = editTextLoginPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Ingresa tu correo y contraseña.")
            return
        }

        val db = AppDatabase.getInstance(this)
        val clienteDao = db.clienteDao()

        lifecycleScope.launch {
            val cliente = clienteDao.getClienteByEmail(email)
            if (cliente == null) {
                showError("El usuario no existe. Regístrate primero.")
                return@launch
            }

            if (cliente.password == password) {
                // Cargar la sesión en memoria para reutilizar ProfileActivity
                val user = User(
                    name = cliente.nombre,
                    email = cliente.email,
                    password = cliente.password,
                    phone = cliente.telefono,
                    skinType = cliente.tipo_piel,
                    hairType = cliente.tipo_cabello
                )
                FakeUserSession.registeredUser = user
                FakeUserSession.loggedInUser = user

                Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                finish()
            } else {
                showError("Credenciales inválidas. Verifica tu correo y contraseña.")
            }
        }
    }

    private fun showError(message: String) {
        textLoginError.text = message
        textLoginError.visibility = View.VISIBLE
    }
}
