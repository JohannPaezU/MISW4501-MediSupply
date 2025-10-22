package com.mfpe.medisupply.ui.comercial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mfpe.medisupply.adapters.ClientListAdapter
import com.mfpe.medisupply.data.model.Client
import com.mfpe.medisupply.databinding.FragmentComClientesBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.ClientViewModel

class ComClientesFragment : Fragment(), ClientListAdapter.OnClientActionListener {

    private var _binding: FragmentComClientesBinding? = null
    private val binding get() = _binding!!
    private lateinit var clientListAdapter: ClientListAdapter
    private lateinit var clientViewModel: ClientViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        clientViewModel = ViewModelProvider(this)[ClientViewModel::class.java]
        _binding = FragmentComClientesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        loadClients()

        return root
    }

    private fun setupRecyclerView() {
        clientListAdapter = ClientListAdapter(emptyList())
        clientListAdapter.setOnClientActionListener(this)

        binding.recyclerViewClients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = clientListAdapter
        }
    }

    private fun loadClients() {
        clientViewModel.getClients(
            PrefsManager.getInstance(requireContext()).getAuthToken!!,
            PrefsManager.getInstance(requireContext()).getuserId
        ) { success, message, response ->
            if (success && response != null) {
                clientListAdapter.updateClients(response.clients)
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRecommendationsClick(client: Client) {
        // Navegar a la pantalla de productos recomendados
        RecommendedProductsActivity.start(requireActivity(), client.id)
    }

    override fun onVisitClick(client: Client) {
        // TODO: Implementar navegación a detalles de visita
        Toast.makeText(requireContext(), "Visitar ${client.fullName}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
