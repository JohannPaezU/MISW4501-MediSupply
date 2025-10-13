package com.mfpe.medisupply.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(org.junit.runners.JUnit4::class)
class LoginActivityTest {

    @Test
    fun `LoginActivity should have correct class name`() {
        // Given
        val className = LoginActivity::class.java.simpleName
        
        // When & Then
        assertEquals("LoginActivity", className)
    }

    @Test
    fun `LoginActivity should extend AppCompatActivity`() {
        // Given
        val superClass = LoginActivity::class.java.superclass
        
        // When & Then
        assertEquals(AppCompatActivity::class.java, superClass)
    }

    @Test
    fun `LoginActivity should have onCreate method`() {
        // Given
        val activityClass = LoginActivity::class.java
        
        // When & Then
        assertNotNull(activityClass.getDeclaredMethod("onCreate", Bundle::class.java))
    }

    @Test
    fun `LoginActivity should be properly configured`() {
        // Given
        val activityClass = LoginActivity::class.java
        
        // When & Then
        assertNotNull(activityClass)
        assertTrue(AppCompatActivity::class.java.isAssignableFrom(activityClass))
    }
}