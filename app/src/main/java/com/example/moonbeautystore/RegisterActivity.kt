package com.example.moonbeautystore

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moonbeautystore.data.AppDatabase
import com.example.moonbeautystore.data.Cliente
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var spinnerSkinType: Spinner
    private lateinit var spinnerHairType: Spinner
    private lateinit var textRegisterError: TextView
    private lateinit var buttonRegister: Button
    private lateinit var textAlreadyHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupSpinners()
        setupListeners()
    }

    private fun initViews() {
        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextPhone = findViewById(R.id.editTextPhone)
        spinnerSkinType = findViewById(R.id.spinnerSkinType)
        spinnerHairType = findViewById(R.id.spinnerHairType)
        textRegisterError = findViewById(R.id.textRegisterError)
        buttonRegister = findViewById(R.id.buttonRegister)
        textAlreadyHaveAccount = findViewById(R.id.textAlreadyHaveAccount)
    }

    private fun setupSpinners() {
        val skinTypes = listOf("Seleccione...", "Seca", "Mixta", "Grasa", "Sensible")
        val hairTypes = listOf("Seleccione...", "Liso", "Ondulado", "Rizado", "Seco", "Graso")

        spinnerSkinType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            skinTypes
        )

        spinnerHairType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            hairTypes
        )
    }

    private fun setupListeners() {
        buttonRegister.setOnClickListener {
            registerUser()
        }

        textAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        textRegisterError.visibility = View.GONE

        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()
        val confirmPassword = editTextConfirmPassword.text.toString()
        val phone = editTextPhone.text.toString().trim().ifEmpty { null }
        val skinType = spinnerSkinType.selectedItem.toString()
        val hairType = spinnerHairType.selectedItem.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Por favor completa todos los campos obligatorios.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Ingresa un correo electrónico válido.")
            return
        }

        if (password.length < 6) {
            showError("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        if (password != confirmPassword) {
            showError("Las contraseñas no coinciden.")
            return
        }

        if (skinType == "Seleccione...") {
            showError("Selecciona tu tipo de piel.")
            return
        }

        if (hairType == "Seleccione...") {
            showError("Selecciona tu tipo de cabello.")
            return
        }

        // Guardar en base de datos usando Room
        val db = AppDatabase.getInstance(this)
        val clienteDao = db.clienteDao()

        lifecycleScope.launch {
            try {
                val cliente = Cliente(
                    nombre = name,
                    email = email,
                    password = password,
                    telefono = phone,
                    tipo_piel = skinType,
                    tipo_cabello = hairType
                )

                clienteDao.insertCliente(cliente)

                // También actualizamos la sesión en memoria para poder usar el perfil
                val sessionUser = User(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone,
                    skinType = skinType,
                    hairType = hairType
                )
                FakeUserSession.registeredUser = sessionUser

                Toast.makeText(
                    this@RegisterActivity,
                    "Registro exitoso. Ahora puedes iniciar sesión.",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                showError("Error al registrar el cliente en la base de datos.")
            }
        }
    }

    private fun showError(message: String) {
        textRegisterError.text = message
        textRegisterError.visibility = View.VISIBLE
    }
}
