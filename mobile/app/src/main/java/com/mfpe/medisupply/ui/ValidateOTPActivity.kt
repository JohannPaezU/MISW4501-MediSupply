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
import com.mfpe.medisupply.data.model.ValidateOTPRequest
import com.mfpe.medisupply.databinding.ActivityValidateOtpBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.UserViewModel

class ValidateOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValidateOtpBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityValidateOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val otp = binding.inputEmail.text.toString().trim()

            if (otp.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa el código OTP.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otpRequest = ValidateOTPRequest(otp = otp)

            userViewModel.validateOTP(otpRequest) { success, message, response ->
                if (success && response != null) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    PrefsManager.getInstance(this).saveAuthToken(response.token)
                    PrefsManager.getInstance(this).saveUserFullName(response.fullName)
                    PrefsManager.getInstance(this).saveUserEmail(response.email)
                    PrefsManager.getInstance(this).saveUserRole(response.role)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
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