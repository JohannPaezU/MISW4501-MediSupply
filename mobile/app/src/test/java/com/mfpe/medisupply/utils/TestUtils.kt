package com.mfpe.medisupply.utils

import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit

/**
 * Utilidades para los tests unitarios (adaptado de Espresso)
 */
object TestUtils {
    
    /**
     * Datos de prueba para login
     */
    object TestData {
        const val VALID_EMAIL = "test@example.com"
        const val VALID_PASSWORD = "password123"
        const val INVALID_EMAIL = "invalid@email"
        const val INVALID_PASSWORD = "123"
        const val VALID_OTP = "123456"
        const val INVALID_OTP = "000000"
        
        const val VALID_USER_ID = "1"
        const val VALID_FULL_NAME = "Juan Pérez"
        const val VALID_NIT = "123456789"
        const val VALID_ADDRESS = "Calle 123 #45-67"
        const val VALID_PHONE = "3001234567"
        
        const val VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        const val VALID_ROLE = "institutional"
    }
    
    /**
     * Configuración base para tests con Mockito
     */
    abstract class BaseTest {
        @Before
        open fun setUp() {
            MockitoAnnotations.openMocks(this)
        }
        
        @After
        fun tearDown() {
            Mockito.framework().clearInlineMocks()
        }
    }
    
    /**
     * Espera un tiempo específico (útil para tests que requieren delays)
     */
    fun waitFor(delay: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS) {
        Thread.sleep(timeUnit.toMillis(delay))
    }
    
    /**
     * Crea un mock de cualquier clase
     */
    inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
    
    /**
     * Verifica que un mock haya sido llamado
     */
    fun <T> verify(mock: T): T = Mockito.verify(mock)
    
    /**
     * Configura el comportamiento de un mock
     */
    fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)
    
    /**
     * Verifica que no haya más interacciones con los mocks
     */
    fun verifyNoMoreInteractions(vararg mocks: Any) {
        Mockito.verifyNoMoreInteractions(*mocks)
    }
    
    /**
     * Verifica que no haya interacciones con un mock
     */
    fun verifyZeroInteractions(vararg mocks: Any) {
        Mockito.verifyNoInteractions(*mocks)
    }
}
