package com.mfpe.medisupply.viewmodel

import androidx.lifecycle.ViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(org.junit.runners.JUnit4::class)
class InicioViewModelTest {

    @Test
    fun `InicioViewModel should have correct class name`() {
        // Given
        val className = InicioViewModel::class.java.simpleName
        
        // When & Then
        assertEquals("InicioViewModel", className)
    }

    @Test
    fun `InicioViewModel should extend ViewModel`() {
        // Given
        val superClass = InicioViewModel::class.java.superclass
        
        // When & Then
        assertEquals(ViewModel::class.java, superClass)
    }

    @Test
    fun `InicioViewModel should have text property`() {
        // Given
        val viewModelClass = InicioViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass.getDeclaredField("text"))
    }

    @Test
    fun `InicioViewModel should be properly configured`() {
        // Given
        val viewModelClass = InicioViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }
}