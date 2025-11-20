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
        return OrderProductViewHolder(
            binding,
            { productId, newQuantity ->
                quantities[productId] = newQuantity
                // Notificar que el item ha cambiado para actualizar la vista
                val position = currentList.indexOfFirst { it.id == productId }
                if (position != -1) {
                    notifyItemChanged(position)
                }
            },
            { productId -> quantities[productId] ?: 0 }
        )
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
        private val onQuantityChanged: (String, Int) -> Unit,
        private val getCurrentQuantity: (String) -> Int
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
                    val currentQty = getCurrentQuantity(product.id)
                    val newQuantity = currentQty + 1
                    onQuantityChanged(product.id, newQuantity)
                }

                btnDecrease.setOnClickListener {
                    val currentQty = getCurrentQuantity(product.id)
                    val newQuantity = maxOf(0, currentQty - 1)
                    onQuantityChanged(product.id, newQuantity)
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

