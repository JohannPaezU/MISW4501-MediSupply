package com.mfpe.medisupply.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(org.junit.runners.JUnit4::class)
class RegisterActivityTest {

    @Test
    fun `RegisterActivity should have correct class name`() {
        // Given
        val className = RegisterActivity::class.java.simpleName
        
        // When & Then
        assertEquals("RegisterActivity", className)
    }

    @Test
    fun `RegisterActivity should extend AppCompatActivity`() {
        // Given
        val superClass = RegisterActivity::class.java.superclass
        
        // When & Then
        assertEquals(AppCompatActivity::class.java, superClass)
    }

    @Test
    fun `RegisterActivity should have onCreate method`() {
        // Given
        val activityClass = RegisterActivity::class.java
        
        // When & Then
        assertNotNull(activityClass.getDeclaredMethod("onCreate", Bundle::class.java))
    }

    @Test
    fun `RegisterActivity should be properly configured`() {
        // Given
        val activityClass = RegisterActivity::class.java
        
        // When & Then
        assertNotNull(activityClass)
        assertTrue(AppCompatActivity::class.java.isAssignableFrom(activityClass))
    }
}