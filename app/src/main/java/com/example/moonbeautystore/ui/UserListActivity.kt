package com.example.moonbeautystore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moonbeautystore.R.*
import com.example.moonbeautystore.data.AppDatabase
import com.example.moonbeautystore.data.Cliente
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListActivity : AppCompatActivity() {

    private lateinit var recyclerUsers: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_user_list)

        recyclerUsers = findViewById(id.recyclerUsers)
        val db = AppDatabase.getInstance(this)
        val clienteDao = db.clienteDao()

        adapter = UserAdapter(
            onEdit = { cliente -> showEditDialog(clienteDao, cliente) },
            onDelete = { cliente ->
                lifecycleScope.launch {
                    // Evitar borrar al admin principal por seguridad básica
                    if (cliente.email == "admin@moonbeautystore.com") {
                        Toast.makeText(
                            this@UserListActivity,
                            "No se puede eliminar el usuario administrador principal.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        clienteDao.deleteCliente(cliente)
                    }
                }
            }
        )

        recyclerUsers.layoutManager = LinearLayoutManager(this)
        recyclerUsers.adapter = adapter

        lifecycleScope.launch {
            clienteDao.getAllClientes().collectLatest { clientes ->
                adapter.submitList(clientes)
            }
        }
    }

    private fun showEditDialog(clienteDao: com.example.moonbeautystore.data.ClienteDao, cliente: Cliente) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar usuario")

        val view = LayoutInflater.from(this).inflate(layout.dialog_user, null)
        val editName: EditText = view.findViewById(id.editUserName)
        val editRole: EditText = view.findViewById(id.editUserRole)

        editName.setText(cliente.nombre)
        editRole.setText(cliente.rol)

        builder.setView(view)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val newName = editName.text.toString().trim()
            val newRole = editRole.text.toString().trim().ifEmpty { "cliente" }

            lifecycleScope.launch {
                val updated = cliente.copy(nombre = newName, rol = newRole)
                clienteDao.updateCliente(updated)
                Toast.makeText(
                    this@UserListActivity,
                    "Usuario actualizado.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.show()
    }
}


