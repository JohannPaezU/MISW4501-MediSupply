package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.OrderProductDetail
import com.mfpe.medisupply.databinding.ItemOrderproductBinding
import java.text.NumberFormat
import java.util.Locale

class OrderProductsAdapter : ListAdapter<OrderProductDetail, OrderProductsAdapter.OrderProductViewHolder>(
    OrderProductDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val binding = ItemOrderproductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderProductViewHolder(
        private val binding: ItemOrderproductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: OrderProductDetail) {
            binding.apply {
                tvProductName.text = product.name

                // Formatear precio
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                tvProductPrice.text = formato.format(product.price_per_unit)

                // Mostrar cantidad (ocultar botones y mostrar solo el texto)
                btnIncrease.visibility = android.view.View.GONE
                btnDecrease.visibility = android.view.View.GONE
                tvQuantity.text = product.quantity.toString()
                tvQuantity.textSize = 14f
                tvQuantity.setTextColor(androidx.core.content.ContextCompat.getColor(
                    tvQuantity.context,
                    com.mfpe.medisupply.R.color.dark_gray
                ))

                // Cargar imagen con Glide
                Glide.with(ivProductImage.context)
                    .load(product.image_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage)
            }
        }
    }

    class OrderProductDiffCallback : DiffUtil.ItemCallback<OrderProductDetail>() {
        override fun areItemsTheSame(oldItem: OrderProductDetail, newItem: OrderProductDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProductDetail, newItem: OrderProductDetail): Boolean {
            return oldItem == newItem
        }
    }
}
