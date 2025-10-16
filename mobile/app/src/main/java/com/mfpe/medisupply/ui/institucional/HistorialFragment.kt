package com.mfpe.medisupply.ui.institucional

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mfpe.medisupply.adapters.OrderListAdapter
import com.mfpe.medisupply.databinding.FragmentHistorialBinding
import com.mfpe.medisupply.ui.OrderDetailActivity
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.OrdersViewModel

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var ordersViewModel: OrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ordersViewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        loadOrders()

        return root
    }

    private fun setupRecyclerView() {
        orderListAdapter = OrderListAdapter(emptyList()) { order ->
            val intent = Intent(requireContext(), OrderDetailActivity::class.java)
            intent.putExtra("ORDER", order)
            startActivity(intent)
        }

        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderListAdapter
        }
    }

    private fun loadOrders() {
        ordersViewModel.getOrders(PrefsManager.getInstance(requireContext()).getuserId, "")
        { success, message, response ->
            if (success && response != null) {
                orderListAdapter.updateOrders(response.orders)
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
