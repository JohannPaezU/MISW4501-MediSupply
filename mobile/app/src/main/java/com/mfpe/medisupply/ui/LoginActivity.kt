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
import com.mfpe.medisupply.data.model.LoginUserRequest
import com.mfpe.medisupply.databinding.ActivityLoginBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.utils.ValidationUtils
import com.mfpe.medisupply.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        prefsManager = PrefsManager.getInstance(this)

        loadRememberMeData()

        binding.checkRememberMe.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                prefsManager.clearRememberMe()
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidationUtils.isValidEmail(email)) {
                Toast.makeText(this, "Por favor ingresa un correo electrónico válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rememberMe = binding.checkRememberMe.isChecked
            prefsManager.saveRememberMeChecked(rememberMe)
            if (rememberMe) {
                prefsManager.saveRememberMeEmail(email)
            } else {
                prefsManager.clearRememberMe()
            }

            val loginRequest = LoginUserRequest(
                email = email,
                password = password
            )

            userViewModel.loginUser(loginRequest) { success, message ->
                if (success) {
                    val intent = Intent(this, ValidateOTPActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadRememberMeData() {
        val rememberMeChecked = prefsManager.getRememberMeChecked()
        binding.checkRememberMe.isChecked = rememberMeChecked

        if (rememberMeChecked) {
            val savedEmail = prefsManager.getRememberMeEmail()
            if (!savedEmail.isNullOrEmpty()) {
                binding.inputEmail.setText(savedEmail)
            }
        }
    }
}