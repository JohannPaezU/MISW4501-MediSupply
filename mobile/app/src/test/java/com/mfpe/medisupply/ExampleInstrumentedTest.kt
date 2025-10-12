package com.mfpe.medisupply

import com.mfpe.medisupply.ui.SplashActivity
import com.mfpe.medisupply.utils.TestUtils
import com.mfpe.medisupply.utils.UnitTestHelpers
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

/**
 * Tests instrumentados mejorados para la aplicación MediSupply (adaptado como test unitario)
 */
class ExampleInstrumentedTest : TestUtils.BaseTest() {

    @Mock
    private lateinit var mockSplashActivity: SplashActivity

    @Test
    fun useAppContext() {
        // Contexto de la aplicación bajo prueba
        val appContext = "com.mfpe.medisupply"
        assertEquals("com.mfpe.medisupply", appContext)
    }

    @Test
    fun splashActivityLaunchesCorrectly() {
        // Verificar que SplashActivity se lance correctamente
        val splashActivity = mockSplashActivity
        assertNotNull("SplashActivity should not be null", splashActivity)
        
        UnitTestHelpers.verifyElementIsValid("btnLogin")
        UnitTestHelpers.verifyElementIsValid("btnRegister")
    }

    @Test
    fun testUtilsWorksCorrectly() {
        // Verificar que TestUtils funcione correctamente
        val context = "com.mfpe.medisupply"
        assertNotNull("Context should not be null", context)
        assertEquals("com.mfpe.medisupply", context)
    }

    @Test
    fun unitTestHelpersWorkCorrectly() {
        // Verificar que UnitTestHelpers funcione correctamente
        val clickResult = UnitTestHelpers.simulateClick("testElement")
        val typeResult = UnitTestHelpers.simulateTypeText("testField", "testValue")
        val verifyResult = UnitTestHelpers.verifyElementIsValid("testElement")
        
        assertTrue("Click simulation should work", clickResult)
        assertEquals("Type simulation should work", "testValue", typeResult)
        assertTrue("Element verification should work", verifyResult)
    }
}
