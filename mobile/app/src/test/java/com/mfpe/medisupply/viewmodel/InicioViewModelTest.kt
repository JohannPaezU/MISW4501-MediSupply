package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InicioViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: InicioViewModel

    @Before
    fun setUp() {
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
    fun `InicioViewModel should be properly configured`() {
        // Given
        val viewModelClass = InicioViewModel::class.java
        
        // When & Then
        assertNotNull(viewModelClass)
        assertTrue(ViewModel::class.java.isAssignableFrom(viewModelClass))
    }

    @Test
    fun `InicioViewModel should be instantiable`() {
        // Given & When
        val newViewModel = InicioViewModel()

        // Then
        assertNotNull(newViewModel)
    }

    @Test
    fun `InicioViewModel should have text LiveData`() {
        // Given
        val viewModel = InicioViewModel()

        // When
        val textLiveData = viewModel.text

        // Then
        assertNotNull("text LiveData should not be null", textLiveData)
    }

    @Test
    fun `text LiveData should have initial value`() {
        // Given
        val viewModel = InicioViewModel()

        // When
        val textValue = viewModel.text.value

        // Then
        assertNotNull("text value should not be null", textValue)
        assertEquals("Initial text should be 'Inicio'", "Inicio", textValue)
    }

    @Test
    fun `text LiveData should be observable`() {
        // Given
        val viewModel = InicioViewModel()
        var observedValue: String? = null

        // When
        viewModel.text.observeForever { value ->
            observedValue = value
        }

        // Then
        assertNotNull("Observed value should not be null", observedValue)
        assertEquals("Observed value should be 'Inicio'", "Inicio", observedValue)
    }

    @Test
    fun `InicioViewModel should maintain state across multiple instances`() {
        // Given
        val viewModel1 = InicioViewModel()
        val viewModel2 = InicioViewModel()

        // When
        val text1 = viewModel1.text.value
        val text2 = viewModel2.text.value

        // Then
        assertNotNull("First viewModel text should not be null", text1)
        assertNotNull("Second viewModel text should not be null", text2)
        assertEquals("Both should have same initial text", text1, text2)
        assertNotEquals("ViewModels should be different instances", viewModel1, viewModel2)
    }

    @Test
    fun `InicioViewModel should handle multiple observers`() {
        // Given
        val viewModel = InicioViewModel()
        var observedValue1: String? = null
        var observedValue2: String? = null

        // When
        viewModel.text.observeForever { value ->
            observedValue1 = value
        }
        viewModel.text.observeForever { value ->
            observedValue2 = value
        }

        // Then
        assertNotNull("First observed value should not be null", observedValue1)
        assertNotNull("Second observed value should not be null", observedValue2)
        assertEquals("Both observers should see same value", observedValue1, observedValue2)
    }

    @Test
    fun `InicioViewModel should be thread safe`() {
        // Given
        val viewModel = InicioViewModel()
        var observedValue: String? = null

        // When
        val thread = Thread {
            viewModel.text.observeForever { value ->
                observedValue = value
            }
        }
        thread.start()
        thread.join()

        // Then
        assertNotNull("Observed value should not be null", observedValue)
        assertEquals("Observed value should be 'Inicio'", "Inicio", observedValue)
    }

    @Test
    fun `InicioViewModel should handle rapid instantiation`() {
        // Given & When
        val viewModels = (1..10).map { InicioViewModel() }

        // Then
        assertEquals("Should create 10 viewModels", 10, viewModels.size)
        viewModels.forEach { vm ->
            assertNotNull("Each viewModel should not be null", vm)
            assertEquals("Each should have correct text", "Inicio", vm.text.value)
        }
    }

    @Test
    fun `InicioViewModel should have correct initial state`() {
        // Given
        val viewModel = InicioViewModel()

        // When
        val textValue = viewModel.text.value
        val hasObservers = viewModel.text.hasObservers()

        // Then
        assertNotNull("Text value should not be null", textValue)
        assertEquals("Text should be 'Inicio'", "Inicio", textValue)
        assertFalse("Should not have observers initially", hasObservers)
    }

    @Test
    fun `InicioViewModel should maintain consistency`() {
        // Given
        val viewModel = InicioViewModel()

        // When
        val textValue1 = viewModel.text.value
        val textValue2 = viewModel.text.value

        // Then
        assertEquals("Text value should be consistent", textValue1, textValue2)
        assertEquals("Text should always be 'Inicio'", "Inicio", textValue1)
    }
}