package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.databinding.ClientListItemBinding

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

    class ClientViewHolder(val binding: ClientListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ClientListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]

        // Configurar avatar con la primera letra del nombre
        val firstLetter = client.full_name.takeIf { it.isNotEmpty() }?.first()?.uppercaseChar() ?: "A"
        holder.binding.textAvatar.text = firstLetter.toString()

        // Configurar información básica
        holder.binding.textClientName.text = client.full_name
        holder.binding.textClientNIT.text = "NIT: ${client.doi}"

        // Configurar información de contacto
        holder.binding.textClientPhone.text = "Teléfono: ${client.phone}"
        holder.binding.textClientAddress.text = "Dirección: ${client.address}"
        holder.binding.textClientEmail.text = "Correo: ${client.email}"

        // Configurar listeners para los botones
        holder.binding.buttonRecommendations.setOnClickListener {
            actionListener?.onRecommendationsClick(client)
        }

        holder.binding.buttonVisit.setOnClickListener {
            actionListener?.onVisitClick(client)
        }

        // Toggle de detalles expandidos y botones al hacer click en la tarjeta
        holder.itemView.setOnClickListener {
            val isExpanded = holder.binding.expandedDetails.visibility == View.VISIBLE

            if (isExpanded) {
                holder.binding.expandedDetails.visibility = View.GONE
                holder.binding.buttonsContainer.visibility = View.GONE
                holder.binding.iconExpand.animate().rotation(0f).setDuration(200).start()
            } else {
                holder.binding.expandedDetails.visibility = View.VISIBLE
                holder.binding.buttonsContainer.visibility = View.VISIBLE
                holder.binding.iconExpand.animate().rotation(180f).setDuration(200).start()
            }
        }
    }

    override fun getItemCount(): Int = clients.size

    fun updateClients(newClients: List<Client>) {
        clients = newClients
        notifyDataSetChanged()
    }
}
