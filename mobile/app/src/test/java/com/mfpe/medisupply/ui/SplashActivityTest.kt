package com.mfpe.medisupply.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(org.junit.runners.JUnit4::class)
class SplashActivityTest {

    @Test
    fun `SplashActivity should have correct class name`() {
        // Given
        val className = SplashActivity::class.java.simpleName
        
        // When & Then
        assertEquals("SplashActivity", className)
    }

    @Test
    fun `SplashActivity should extend AppCompatActivity`() {
        // Given
        val superClass = SplashActivity::class.java.superclass
        
        // When & Then
        assertEquals(AppCompatActivity::class.java, superClass)
    }

    @Test
    fun `SplashActivity should have onCreate method`() {
        // Given
        val activityClass = SplashActivity::class.java
        
        // When & Then
        assertNotNull(activityClass.getDeclaredMethod("onCreate", Bundle::class.java))
    }

    @Test
    fun `SplashActivity should be properly configured`() {
        // Given
        val activityClass = SplashActivity::class.java
        
        // When & Then
        assertNotNull(activityClass)
        assertTrue(AppCompatActivity::class.java.isAssignableFrom(activityClass))
    }
}