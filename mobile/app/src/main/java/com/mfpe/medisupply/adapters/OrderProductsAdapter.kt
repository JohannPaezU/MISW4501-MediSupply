package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.databinding.ItemOrderproductBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderProductsAdapter : ListAdapter<Product, OrderProductsAdapter.OrderProductViewHolder>(ProductoDiffCallback()) {

    private val quantityMap = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val binding = ItemOrderproductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        val product = getItem(position)
        val currentQuantity = quantityMap[product.id] ?: 0
        holder.bind(product, currentQuantity,
            onDecrease = {
                updateQuantity(product.id, -1)
                notifyItemChanged(position)
            },
            onIncrease = {
                updateQuantity(product.id, 1)
                notifyItemChanged(position)
            }
        )
    }

    private fun updateQuantity(productId: String, delta: Int) {
        val currentQuantity = quantityMap[productId] ?: 0
        val newQuantity = (currentQuantity + delta).coerceAtLeast(0)
        quantityMap[productId] = newQuantity
    }

    fun getProductsWithQuantities(): Map<String, Int> = quantityMap.toMap()

    class OrderProductViewHolder(
        private val binding: ItemOrderproductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            product: Product,
            quantity: Int,
            onDecrease: () -> Unit,
            onIncrease: () -> Unit
        ) {
            binding.apply {
                tvProductName.text = product.name
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                tvProductPrice.text = formato.format(product.price_per_unit)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
                try {
                    val date = dateFormat.parse(product.due_date)
                    if (date != null) {
                        tvProductExpiry.text = "Vence: ${displayFormat.format(date)}"
                    } else {
                        tvProductExpiry.text = "Vence: ${product.due_date}"
                    }
                } catch (e: Exception) {
                    tvProductExpiry.text = "Vence: ${product.due_date}"
                }
                Glide.with(ivProductImage.context)
                    .load(product.image_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage)
                tvQuantity.text = quantity.toString()

                // Configurar listeners
                btnDecrease.setOnClickListener { onDecrease() }
                btnIncrease.setOnClickListener { onIncrease() }
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
