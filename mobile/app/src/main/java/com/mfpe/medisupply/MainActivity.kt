package com.mfpe.medisupply

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mfpe.medisupply.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // Manejar clic en Cerrar sesión
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_logout -> {
                    // Redirigir a LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.placeholder -> {
                    // No hacer nada para el placeholder
                    false
                }
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

        // Manejar clic en el FAB
        binding.fabAdd.setOnClickListener {
            // Aquí puedes agregar la funcionalidad que desees
            // Por ejemplo, navegar a una pantalla de agregar producto
            // o mostrar un diálogo
        }
    }
}