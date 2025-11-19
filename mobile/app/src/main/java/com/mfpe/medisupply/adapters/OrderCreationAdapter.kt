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

class OrderCreationAdapter : ListAdapter<Product, OrderCreationAdapter.OrderProductViewHolder>(
    OrderProductDiffCallback()
) {

    private val quantities = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val binding = ItemOrderproductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderProductViewHolder(binding) { productId, newQuantity ->
            quantities[productId] = newQuantity
        }
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        val product = getItem(position)
        val currentQuantity = quantities[product.id] ?: 0
        holder.bind(product, currentQuantity)
    }

    fun getProductsWithQuantities(): Map<String, Int> {
        return quantities.toMap()
    }

    class OrderProductViewHolder(
        private val binding: ItemOrderproductBinding,
        private val onQuantityChanged: (String, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, currentQuantity: Int) {
            binding.apply {
                tvProductName.text = product.name

                // Formatear precio
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                tvProductPrice.text = formato.format(product.price_per_unit)

                // Mostrar cantidad
                tvQuantity.text = currentQuantity.toString()

                // Cargar imagen con Glide
                Glide.with(ivProductImage.context)
                    .load(product.image_url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivProductImage)

                // Configurar botones de incremento/decremento
                btnIncrease.setOnClickListener {
                    val newQuantity = currentQuantity + 1
                    onQuantityChanged(product.id, newQuantity)
                    tvQuantity.text = newQuantity.toString()
                }

                btnDecrease.setOnClickListener {
                    val newQuantity = maxOf(0, currentQuantity - 1)
                    onQuantityChanged(product.id, newQuantity)
                    tvQuantity.text = newQuantity.toString()
                }
            }
        }
    }

    class OrderProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}

