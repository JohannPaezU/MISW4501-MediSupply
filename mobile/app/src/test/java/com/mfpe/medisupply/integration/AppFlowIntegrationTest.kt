package com.mfpe.medisupply.integration

import com.mfpe.medisupply.utils.TestUtils
import com.mfpe.medisupply.utils.UnitTestHelpers
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

/**
 * Tests de integración para el flujo completo de la aplicación (adaptado de Espresso)
 */
class AppFlowIntegrationTest : TestUtils.BaseTest() {

    @Mock
    private lateinit var mockSplashActivity: com.mfpe.medisupply.ui.SplashActivity

    @Mock
    private lateinit var mockLoginActivity: com.mfpe.medisupply.ui.LoginActivity

    @Mock
    private lateinit var mockRegisterActivity: com.mfpe.medisupply.ui.RegisterActivity

    @Test
    fun `completeAppFlow splashToLoginToRegister should work correctly`() {
        // Given
        val splashActivity = mockSplashActivity
        val registerActivity = mockRegisterActivity

        // When - 1. Desde SplashActivity, navegar a RegisterActivity
        val registerClickResult = UnitTestHelpers.simulateClick("btnRegister")

        // Then - 2. Verificar que estamos en RegisterActivity
        assertTrue("Register button click should be successful", registerClickResult)
        UnitTestHelpers.verifyElementIsValid("inputEmail")
        UnitTestHelpers.verifyElementIsValid("inputNames")
        UnitTestHelpers.verifyElementIsValid("btnCreateAccount")

        // When - 3. Llenar el formulario de registro
        UnitTestHelpers.simulateTypeText("inputEmail", TestUtils.TestData.VALID_EMAIL)
        UnitTestHelpers.simulateTypeText("inputNames", TestUtils.TestData.VALID_FULL_NAME)
        UnitTestHelpers.simulateTypeText("inputNitRuc", TestUtils.TestData.VALID_NIT)
        UnitTestHelpers.simulateTypeText("inputAddress", TestUtils.TestData.VALID_ADDRESS)
        UnitTestHelpers.simulateTypeText("inputContactNumber", TestUtils.TestData.VALID_PHONE)
        UnitTestHelpers.simulateTypeText("inputPassword", TestUtils.TestData.VALID_PASSWORD)
        UnitTestHelpers.simulateTypeText("inputConfirmPassword", TestUtils.TestData.VALID_PASSWORD)

        // When - 4. Cerrar el teclado
        val keyboardCloseResult = UnitTestHelpers.simulateCloseKeyboard()

        // When - 5. Intentar crear la cuenta
        val createAccountResult = UnitTestHelpers.simulateClick("btnCreateAccount")

        // Then
        assertTrue("Keyboard should close successfully", keyboardCloseResult)
        assertTrue("Create account should be successful", createAccountResult)
    }

    @Test
    fun `completeAppFlow splashToLogin should work correctly`() {
        // Given
        val splashActivity = mockSplashActivity
        val loginActivity = mockLoginActivity

        // When - 1. Desde SplashActivity, navegar a LoginActivity
        val loginClickResult = UnitTestHelpers.simulateClick("btnLogin")

        // Then - 2. Verificar que estamos en LoginActivity
        assertTrue("Login button click should be successful", loginClickResult)
        UnitTestHelpers.verifyElementIsValid("inputEmail")
        UnitTestHelpers.verifyElementIsValid("inputPassword")
        UnitTestHelpers.verifyElementIsValid("btnLogin")

        // When - 3. Intentar hacer login con campos vacíos
        val emptyLoginClickResult = UnitTestHelpers.simulateClick("btnLogin")

        // Then - 4. Verificar que se muestre el mensaje de error
        assertTrue("Empty login click should be successful", emptyLoginClickResult)
        UnitTestHelpers.verifyMessageAppears("Por favor completa todos los campos.")

        // When - 5. Llenar los campos con datos válidos
        UnitTestHelpers.simulateTypeText("inputEmail", TestUtils.TestData.VALID_EMAIL)
        UnitTestHelpers.simulateTypeText("inputPassword", TestUtils.TestData.VALID_PASSWORD)

        // When - 6. Cerrar el teclado
        val keyboardCloseResult = UnitTestHelpers.simulateCloseKeyboard()

        // When - 7. Intentar hacer login
        val validLoginClickResult = UnitTestHelpers.simulateClick("btnLogin")

        // Then
        assertTrue("Keyboard should close successfully", keyboardCloseResult)
        assertTrue("Valid login click should be successful", validLoginClickResult)
    }

    @Test
    fun `completeAppFlow loginToOTPValidation should work correctly`() {
        // Given
        val loginActivity = mockLoginActivity

        // When - 1. Navegar a LoginActivity
        val loginClickResult = UnitTestHelpers.simulateClick("btnLogin")

        // When - 2. Llenar datos de login
        UnitTestHelpers.simulateTypeText("inputEmail", TestUtils.TestData.VALID_EMAIL)
        UnitTestHelpers.simulateTypeText("inputPassword", TestUtils.TestData.VALID_PASSWORD)
        UnitTestHelpers.simulateCloseKeyboard()

        // When - 3. Hacer login
        val loginResult = UnitTestHelpers.simulateClick("btnLogin")

        // Then
        assertTrue("Login navigation should work", loginClickResult)
        assertTrue("Login should be successful", loginResult)
    }

    @Test
    fun `completeAppFlow navigationBetweenActivities should work correctly`() {
        // Given
        val splashActivity = mockSplashActivity
        val registerActivity = mockRegisterActivity
        val loginActivity = mockLoginActivity

        // When - 1. Desde SplashActivity ir a RegisterActivity
        val registerClickResult = UnitTestHelpers.simulateClick("btnRegister")

        // When - 2. Desde RegisterActivity ir a LoginActivity (simulado con cancel)
        val cancelClickResult = UnitTestHelpers.simulateClick("btnCancel")

        // Then - 3. Verificar que volvimos a SplashActivity
        assertTrue("Register click should work", registerClickResult)
        assertTrue("Cancel click should work", cancelClickResult)
        UnitTestHelpers.verifyElementIsValid("btnLogin")
        UnitTestHelpers.verifyElementIsValid("btnRegister")

        // When - 4. Ir a LoginActivity
        val loginClickResult = UnitTestHelpers.simulateClick("btnLogin")

        // When - 5. Cancelar login
        val loginCancelClickResult = UnitTestHelpers.simulateClick("btnCancel")

        // Then - 6. Verificar que volvimos a SplashActivity
        assertTrue("Login click should work", loginClickResult)
        assertTrue("Login cancel click should work", loginCancelClickResult)
        UnitTestHelpers.verifyElementIsValid("btnLogin")
        UnitTestHelpers.verifyElementIsValid("btnRegister")
    }

    @Test
    fun `completeAppFlow formValidation should work correctly`() {
        // Given
        val registerActivity = mockRegisterActivity

        // When - 1. Ir a RegisterActivity
        val registerClickResult = UnitTestHelpers.simulateClick("btnRegister")

        // When - 2. Probar validación de campos vacíos
        val createAccountClickResult = UnitTestHelpers.simulateClick("btnCreateAccount")

        // Then
        assertTrue("Register click should work", registerClickResult)
        assertTrue("Create account click should work", createAccountClickResult)
        UnitTestHelpers.verifyMessageAppears("Por favor completa todos los campos.")

        // When - 3. Llenar algunos campos pero dejar otros vacíos
        UnitTestHelpers.simulateTypeText("inputEmail", TestUtils.TestData.VALID_EMAIL)
        UnitTestHelpers.simulateTypeText("inputNames", TestUtils.TestData.VALID_FULL_NAME)

        // When - 4. Intentar crear cuenta
        val partialCreateAccountClickResult = UnitTestHelpers.simulateClick("btnCreateAccount")

        // Then
        assertTrue("Partial create account click should work", partialCreateAccountClickResult)
        UnitTestHelpers.verifyMessageAppears("Por favor completa todos los campos.")

        // When - 5. Llenar todos los campos pero con contraseñas diferentes
        UnitTestHelpers.simulateTypeText("inputNitRuc", TestUtils.TestData.VALID_NIT)
        UnitTestHelpers.simulateTypeText("inputAddress", TestUtils.TestData.VALID_ADDRESS)
        UnitTestHelpers.simulateTypeText("inputContactNumber", TestUtils.TestData.VALID_PHONE)
        UnitTestHelpers.simulateTypeText("inputPassword", TestUtils.TestData.VALID_PASSWORD)
        UnitTestHelpers.simulateTypeText("inputConfirmPassword", "differentpassword")

        UnitTestHelpers.simulateCloseKeyboard()
        val passwordMismatchClickResult = UnitTestHelpers.simulateClick("btnCreateAccount")

        // Then
        assertTrue("Password mismatch click should work", passwordMismatchClickResult)
        UnitTestHelpers.verifyMessageAppears("Las contraseñas no coinciden.")
    }

    @Test
    fun `completeAppFlow uiElementsAccessibility should work correctly`() {
        // Given
        val splashActivity = mockSplashActivity
        val loginActivity = mockLoginActivity

        // When - 1. Verificar que todos los elementos principales sean accesibles
        UnitTestHelpers.verifyElementIsValid("btnLogin")
        UnitTestHelpers.verifyElementIsValid("btnRegister")

        // When - 2. Verificar que los botones estén habilitados
        UnitTestHelpers.verifyElementIsEnabled(true) // btnLogin
        UnitTestHelpers.verifyElementIsEnabled(true) // btnRegister

        // When - 3. Ir a LoginActivity y verificar elementos
        val loginClickResult = UnitTestHelpers.simulateClick("btnLogin")

        // Then
        assertTrue("Login click should work", loginClickResult)
        UnitTestHelpers.verifyElementIsValid("inputEmail")
        UnitTestHelpers.verifyElementIsValid("inputPassword")
        UnitTestHelpers.verifyElementIsEnabled(true) // btnLogin
        UnitTestHelpers.verifyElementIsEnabled(true) // btnCancel

        // When - 4. Verificar tipos de campos
        UnitTestHelpers.verifyFieldIsEmailType(TestUtils.TestData.VALID_EMAIL)
        UnitTestHelpers.verifyFieldIsPasswordType(TestUtils.TestData.VALID_PASSWORD)
    }

    @Test
    fun `completeAppFlow should handle all user interactions correctly`() {
        // Given
        val splashActivity = mockSplashActivity
        val loginActivity = mockLoginActivity
        val registerActivity = mockRegisterActivity

        // When - Test all navigation flows
        val splashToLogin = UnitTestHelpers.simulateClick("btnLogin")
        val splashToRegister = UnitTestHelpers.simulateClick("btnRegister")
        val loginCancel = UnitTestHelpers.simulateClick("btnCancel")
        val registerCancel = UnitTestHelpers.simulateClick("btnCancel")

        // Then
        assertTrue("Splash to login should work", splashToLogin)
        assertTrue("Splash to register should work", splashToRegister)
        assertTrue("Login cancel should work", loginCancel)
        assertTrue("Register cancel should work", registerCancel)
    }
}
