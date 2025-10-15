package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(org.junit.runners.JUnit4::class)
class InicioViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: InicioViewModel

    @Before
    fun setup() {
        viewModel = InicioViewModel()
    }

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

    @Test
    fun `text LiveData should have initial value Inicio`() {
        // Given & When
        val textValue = viewModel.text.value

        // Then
        assertEquals("Inicio", textValue)
    }

    @Test
    fun `text LiveData should not be null`() {
        // Given & When
        val text = viewModel.text

        // Then
        assertNotNull(text)
    }

    @Test
    fun `viewModel should be instantiable`() {
        // Given & When
        val newViewModel = InicioViewModel()

        // Then
        assertNotNull(newViewModel)
        assertNotNull(newViewModel.text)
    }
}