package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    // Interface para manejar clicks en los botones
    interface OnClientActionListener {
        fun onRecommendationsClick(client: Client)
        fun onVisitClick(client: Client)
    }

    private var actionListener: OnClientActionListener? = null

    fun setOnClientActionListener(listener: OnClientActionListener) {
        this.actionListener = listener
    }

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textAvatar: TextView = itemView.findViewById(R.id.textAvatar)
        val textClientName: TextView = itemView.findViewById(R.id.textClientName)
        val textClientNIT: TextView = itemView.findViewById(R.id.textClientNIT)
        val textClientNameRepeat: TextView = itemView.findViewById(R.id.textClientNameRepeat)
        val textClientPhone: TextView = itemView.findViewById(R.id.textClientPhone)
        val textClientAddress: TextView = itemView.findViewById(R.id.textClientAddress)
        val textClientEmail: TextView = itemView.findViewById(R.id.textClientEmail)
        val expandedDetails: LinearLayout = itemView.findViewById(R.id.expandedDetails)
        val buttonRecommendations: Button = itemView.findViewById(R.id.buttonRecommendations)
        val buttonVisit: Button = itemView.findViewById(R.id.buttonVisit)
        val buttonsContainer: LinearLayout = itemView.findViewById(R.id.buttonsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.client_list_item, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]

        // Configurar avatar con la primera letra del nombre
        val firstLetter = client.fullName.takeIf { it.isNotEmpty() }?.first()?.uppercaseChar() ?: "A"
        holder.textAvatar.text = firstLetter.toString()

        // Configurar información básica
        holder.textClientName.text = client.fullName
        holder.textClientNameRepeat.text = client.fullName

        // Formatear la fecha DOI como NIT
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val nitFormatted = dateFormat.format(client.doi)
        holder.textClientNIT.text = "NIT: $nitFormatted"

        // Configurar información de contacto
        holder.textClientPhone.text = "Teléfono: ${client.phone}"
        holder.textClientAddress.text = "Dirección: ${client.address}"
        holder.textClientEmail.text = "Correo: ${client.email}"

        // Configurar listeners para los botones
        holder.buttonRecommendations.setOnClickListener {
            actionListener?.onRecommendationsClick(client)
        }

        holder.buttonVisit.setOnClickListener {
            actionListener?.onVisitClick(client)
        }

        // Toggle de detalles expandidos y botones al hacer click en la tarjeta
        holder.itemView.setOnClickListener {
            if (holder.expandedDetails.visibility == View.GONE) {
                holder.expandedDetails.visibility = View.VISIBLE
                holder.buttonsContainer.visibility = View.VISIBLE
            } else {
                holder.expandedDetails.visibility = View.GONE
                holder.buttonsContainer.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = clients.size

    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }
}

