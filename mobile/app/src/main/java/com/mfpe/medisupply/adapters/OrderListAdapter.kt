package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.Order

class OrderListAdapter(
    private var orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconDelivery: TextView? = itemView.findViewById(R.id.iconDelivery)
        val textOrderName: TextView = itemView.findViewById(R.id.textOrderName)
        val textOrderStatus: TextView = itemView.findViewById(R.id.textOrderStatus)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_list_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.textOrderName.text = order.id
        holder.textOrderStatus.text = "Estado: ${order.status}"
        
        // Mostrar número consecutivo basado en la posición (1, 2, 3, 4, 5, 6...)
        val deliveryNumber = position + 1
        holder.iconDelivery?.text = deliveryNumber.toString()

        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}

