package com.mfpe.medisupply.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.mfpe.medisupply.R
import com.mfpe.medisupply.data.model.RegisterUserRequest
import com.mfpe.medisupply.databinding.ActivityRegisterBinding
import com.mfpe.medisupply.utils.ValidationUtils
import com.mfpe.medisupply.viewmodel.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.btnCreateAccount.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val fullName = binding.inputNames.text.toString().trim()
            val nit = binding.inputNitRuc.text.toString().trim()
            val address = binding.inputAddress.text.toString().trim()
            val phone = binding.inputContactNumber.text.toString().trim()
            val password = binding.inputPassword.text.toString()
            val confirmPassword = binding.inputConfirmPassword.text.toString()

            if (email.isEmpty() || fullName.isEmpty() || nit.isEmpty() ||
                address.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidationUtils.isValidEmail(email)) {
                Toast.makeText(this, "Por favor ingresa un correo electrónico válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registerRequest = RegisterUserRequest(
                fullName = fullName,
                email = email,
                role = "institutional",
                password = password,
                phone = phone,
                doi = nit,
                address = address
            )

            userViewModel.registerUser(registerRequest) { success, message ->
                if (success) {
                    Toast.makeText(this, "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}