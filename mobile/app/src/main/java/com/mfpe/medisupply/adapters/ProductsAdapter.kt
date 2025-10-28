package com.mfpe.medisupply.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.Product
import com.mfpe.medisupply.databinding.ItemProductBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ProductsAdapter : ListAdapter<Product, ProductsAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductoViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvProductName.text = product.name

                // Formatear precio
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                tvProductPrice.text = formato.format(product.price_per_unit)

                // Formatear fecha de vencimiento
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

                // Cargar imagen con Glide
                Glide.with(ivProductImage.context)
                    .load(product.image_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage)
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
