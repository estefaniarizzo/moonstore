package com.example.moonbeautystore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.view.View

class ProfileActivity : AppCompatActivity() {

    private lateinit var textProfileName: TextView
    private lateinit var textProfileEmail: TextView
    private lateinit var textProfileSkinType: TextView
    private lateinit var textProfileHairType: TextView
    private lateinit var textLocation: TextView
    private lateinit var buttonEditProfile: Button
    private lateinit var buttonLogout: Button
    private lateinit var buttonViewProducts: Button
    private lateinit var buttonManageUsers: Button
    private lateinit var buttonGetLocation: Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        loadUserData()
        setupListeners()
    }

    private fun initViews() {
        textProfileName = findViewById(R.id.textProfileName)
        textProfileEmail = findViewById(R.id.textProfileEmail)
        textProfileSkinType = findViewById(R.id.textProfileSkinType)
        textProfileHairType = findViewById(R.id.textProfileHairType)
        textLocation = findViewById(R.id.textLocation)
        buttonEditProfile = findViewById(R.id.buttonEditProfile)
        buttonLogout = findViewById(R.id.buttonLogout)
        buttonViewProducts = findViewById(R.id.buttonViewProducts)
        buttonManageUsers = findViewById(R.id.buttonManageUsers)
        buttonGetLocation = findViewById(R.id.buttonGetLocation)
    }

    private fun loadUserData() {
        val user = FakeUserSession.loggedInUser

        if (user == null) {
            Toast.makeText(this, "No hay usuario en sesión. Inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        textProfileName.text = user.name
        textProfileEmail.text = user.email
        textProfileSkinType.text = "Tipo de piel: ${user.skinType}"
        textProfileHairType.text = "Tipo de cabello: ${user.hairType}"
        // Mostrar el botón de gestión de usuarios solo si es admin (por email)
        buttonManageUsers.visibility =
            if (user.email == "admin@moonbeautystore.com") View.VISIBLE else View.GONE
    }

    private fun setupListeners() {
        buttonEditProfile.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de edición pendiente (versión futura).", Toast.LENGTH_SHORT).show()
        }

        buttonLogout.setOnClickListener {
            FakeUserSession.logout()
            Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        buttonViewProducts.setOnClickListener {
            val intent = Intent(this, com.example.moonbeautystore.ui.ProductListActivity::class.java)
            startActivity(intent)
        }

        buttonManageUsers.setOnClickListener {
            val intent = Intent(this, com.example.moonbeautystore.ui.UserListActivity::class.java)
            startActivity(intent)
        }

        buttonGetLocation.setOnClickListener {
            requestLocation()
        }
    }

    private fun requestLocation() {
        val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            fetchLastLocation()
        }
    }

    private fun fetchLastLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val lat = location.latitude
                        val lng = location.longitude
                        textLocation.text = "Ubicación actual: $lat, $lng"
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error obteniendo la ubicación.", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permiso de ubicación no concedido.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                fetchLastLocation()
            } else {
                Toast.makeText(this, "Se requiere permiso de ubicación para esta función.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
