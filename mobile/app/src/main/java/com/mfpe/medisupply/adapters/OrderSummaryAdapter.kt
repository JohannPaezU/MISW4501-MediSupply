package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.OrderSummaryItem
import com.mfpe.medisupply.databinding.ItemOrderSummaryBinding
import java.text.NumberFormat
import java.util.Locale

class OrderSummaryAdapter : ListAdapter<OrderSummaryItem, OrderSummaryAdapter.OrderSummaryViewHolder>(OrderSummaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderSummaryViewHolder {
        val binding = ItemOrderSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderSummaryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class OrderSummaryViewHolder(
        private val binding: ItemOrderSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderSummaryItem) {
            binding.apply {
                tvProductName.text = item.name
                tvProductQuantity.text = "Cantidad: ${item.quantity}"
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                tvProductPrice.text = formato.format(item.price)

                Glide.with(ivProductImage.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage)
            }
        }
    }

    class OrderSummaryDiffCallback : DiffUtil.ItemCallback<OrderSummaryItem>() {
        override fun areItemsTheSame(oldItem: OrderSummaryItem, newItem: OrderSummaryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderSummaryItem, newItem: OrderSummaryItem): Boolean {
            return oldItem == newItem
        }
    }
}
