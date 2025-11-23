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
import com.example.moonbeautystore.R.id.textUserRole
import com.example.moonbeautystore.data.Cliente

class UserAdapter(
    private val onEdit: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : ListAdapter<Cliente, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cliente>() {
            override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean =
                oldItem.id_cliente == newItem.id_cliente

            override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textName: TextView = itemView.findViewById(R.id.textUserName)
        private val textEmail: TextView = itemView.findViewById(R.id.textUserEmail)
        private val textRole: TextView = itemView.findViewById(textUserRole)
        private val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEditUser)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDeleteUser)

        fun bind(cliente: Cliente) {
            textName.text = cliente.nombre
            textEmail.text = cliente.email
            textRole.text = "Rol: ${cliente.rol}"

            buttonEdit.setOnClickListener { onEdit(cliente) }
            buttonDelete.setOnClickListener { onDelete(cliente) }
        }
    }
}
