package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.Client
import java.text.SimpleDateFormat
import java.util.Locale

class ClientListAdapter(
    private var clients: List<Client>
) : RecyclerView.Adapter<ClientListAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textClientName: TextView = itemView.findViewById(R.id.textClientName)
        val textClientNIT: TextView = itemView.findViewById(R.id.textClientNIT)
        val textClientPhone: TextView = itemView.findViewById(R.id.textClientPhone)
        val textClientAddress: TextView = itemView.findViewById(R.id.textClientAddress)
        val textClientEmail: TextView = itemView.findViewById(R.id.textClientEmail)
        val expandedDetails: LinearLayout = itemView.findViewById(R.id.expandedDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_list_item, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]

        holder.textClientName.text = client.fullName

        // Formatear la fecha DOI como NIT
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val nitFormatted = dateFormat.format(client.doi)
        holder.textClientNIT.text = "NIT: $nitFormatted"

        holder.textClientPhone.text = "Teléfono: ${client.phone}"
        holder.textClientAddress.text = "Dirección: ${client.address}"
        holder.textClientEmail.text = "Correo: ${client.email}"

        // Toggle de detalles expandidos al hacer click
        holder.itemView.setOnClickListener {
            if (holder.expandedDetails.visibility == View.GONE) {
                holder.expandedDetails.visibility = View.VISIBLE
            } else {
                holder.expandedDetails.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = clients.size

    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }
}

