package com.mfpe.medisupply.utils

import android.util.Patterns

object ValidationUtils {

    /**
     * Valida el formato de un correo electrónico
     * @param email El correo electrónico a validar
     * @return true si el formato es válido, false en caso contrario
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

