package com.mfpe.medisupply.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(org.junit.runners.JUnit4::class)
class MainActivityTest {

    @Test
    fun `MainActivity should have correct class name`() {
        // Given
        val className = MainActivity::class.java.simpleName
        
        // When & Then
        assertEquals("MainActivity", className)
    }

    @Test
    fun `MainActivity should extend AppCompatActivity`() {
        // Given
        val superClass = MainActivity::class.java.superclass
        
        // When & Then
        assertEquals(AppCompatActivity::class.java, superClass)
    }

    @Test
    fun `MainActivity should have onCreate method`() {
        // Given
        val activityClass = MainActivity::class.java
        
        // When & Then
        assertNotNull(activityClass.getDeclaredMethod("onCreate", Bundle::class.java))
    }

    @Test
    fun `MainActivity should be properly configured`() {
        // Given
        val activityClass = MainActivity::class.java
        
        // When & Then
        assertNotNull(activityClass)
        assertTrue(AppCompatActivity::class.java.isAssignableFrom(activityClass))
    }
}