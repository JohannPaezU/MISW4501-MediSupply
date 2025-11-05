package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mfpe.medisupply.data.model.OrderProductDetail
import com.mfpe.medisupply.databinding.ItemProductOrderBinding
import java.text.NumberFormat
import java.util.Locale

class OrderProductDetailAdapter : ListAdapter<OrderProductDetail, OrderProductDetailAdapter.OrderProductDetailViewHolder>(
    OrderProductDetailDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductDetailViewHolder {
        val binding = ItemProductOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductDetailViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class OrderProductDetailViewHolder(
        private val binding: ItemProductOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: OrderProductDetail) {
            binding.apply {
                textProductName.text = product.name
                textProductQuantity.text = "Cantidad: ${product.quantity}"
                
                // Formatear precio por unidad
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                val pricePerUnit = product.price_per_unit
                textProductPrice.text = "Precio unitario: ${formato.format(pricePerUnit)}"
            }
        }
    }

    class OrderProductDetailDiffCallback : DiffUtil.ItemCallback<OrderProductDetail>() {
        override fun areItemsTheSame(oldItem: OrderProductDetail, newItem: OrderProductDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProductDetail, newItem: OrderProductDetail): Boolean {
            return oldItem == newItem
        }
    }
}

