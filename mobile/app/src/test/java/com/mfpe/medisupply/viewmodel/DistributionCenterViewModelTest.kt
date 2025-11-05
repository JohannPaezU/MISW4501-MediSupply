package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mfpe.medisupply.data.model.CenterListResponse
import com.mfpe.medisupply.data.model.DistributionCenter
import com.mfpe.medisupply.data.repository.DistributionCenterRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class DistributionCenterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockDistributionCenterRepository: DistributionCenterRepository

    @Mock
    private lateinit var mockCall: Call<CenterListResponse>

    private lateinit var distributionCenterViewModel: DistributionCenterViewModel

    @Before
    fun setUp() {
        distributionCenterViewModel = DistributionCenterViewModel(mockDistributionCenterRepository)
    }

    @Test
    fun `getDistributionCenters should call repository method`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.success(CenterListResponse(
                total_count = 1,
                distribution_centers = listOf(
                    DistributionCenter(
                        id = "center-123",
                        name = "Test Center",
                        address = "Test Address",
                        city = "Bogotá",
                        country = "Colombia",
                        created_at = "2025-10-28"
                    )
                )
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        var resultSuccess = false
        var resultMessage = ""
        var resultData: CenterListResponse? = null

        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, data ->
            resultSuccess = success
            resultMessage = message
            resultData = data
        }

        // Then
        verify(mockDistributionCenterRepository).getDistributionCenters(authToken)
        assertTrue("Result should be successful", resultSuccess)
        assertNotNull("Result data should not be null", resultData)
        assertEquals("Centers obtained.", resultMessage)
    }

    @Test
    fun `getDistributionCenters should handle success response`() {
        // Given
        val authToken = "Bearer test-token"
        val centers = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                created_at = "2025-10-28"
            ),
            DistributionCenter(
                id = "center-2",
                name = "Center 2",
                address = "Address 2",
                city = "Medellín",
                country = "Colombia",
                created_at = "2025-10-28"
            )
        )

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.success(CenterListResponse(
                total_count = centers.size,
                distribution_centers = centers
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultMessage = ""
        var resultData: CenterListResponse? = null

        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, data ->
            resultSuccess = success
            resultMessage = message
            resultData = data
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertTrue("Should be successful", resultSuccess)
        assertEquals("Centers obtained.", resultMessage)
        assertNotNull("Data should not be null", resultData)
        assertEquals(2, resultData?.total_count)
        assertEquals(2, resultData?.distribution_centers?.size)
    }

    @Test
    fun `getDistributionCenters should handle error response`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.error<CenterListResponse>(
                400,
                okhttp3.ResponseBody.create(null, "Bad Request")
            )
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultMessage = ""

        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, _ ->
            resultSuccess = success
            resultMessage = message
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful", resultSuccess)
        assertTrue("Message should contain error", resultMessage.contains("Error obtaining centers"))
    }

    @Test
    fun `getDistributionCenters should handle network failure`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            callback.onFailure(mockCall, Throwable("Network error"))
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultMessage = ""

        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, _ ->
            resultSuccess = success
            resultMessage = message
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful", resultSuccess)
        assertTrue("Message should contain connection error", resultMessage.contains("Connection error"))
    }

    @Test
    fun `getDistributionCenters should handle null response body`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.success<CenterListResponse>(null)
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false

        distributionCenterViewModel.getDistributionCenters(authToken) { success, _, _ ->
            resultSuccess = success
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful with null body", resultSuccess)
    }

    @Test
    fun `getDistributionCenters should handle empty token`() {
        // Given
        val authToken = ""

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.error<CenterListResponse>(
                401,
                okhttp3.ResponseBody.create(null, "Unauthorized")
            )
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false

        distributionCenterViewModel.getDistributionCenters(authToken) { success, _, _ ->
            resultSuccess = success
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful with empty token", resultSuccess)
    }

    @Test
    fun `getDistributionCenters should handle empty center list`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.success(CenterListResponse(
                total_count = 0,
                distribution_centers = emptyList()
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultData: CenterListResponse? = null

        distributionCenterViewModel.getDistributionCenters(authToken) { success, _, data ->
            resultSuccess = success
            resultData = data
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertTrue("Should be successful even with empty list", resultSuccess)
        assertNotNull("Data should not be null", resultData)
        assertEquals(0, resultData?.total_count)
        assertTrue("Centers list should be empty", resultData?.distribution_centers?.isEmpty() == true)
    }

    @Test
    fun `getDistributionCenters should be callable multiple times`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(anyString()))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.success(CenterListResponse(
                total_count = 1,
                distribution_centers = listOf(
                    DistributionCenter(
                        id = "center-123",
                        name = "Test Center",
                        address = "Test Address",
                        city = "Bogotá",
                        country = "Colombia",
                        created_at = "2025-10-28"
                    )
                )
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(3)
        var successCount = 0

        repeat(3) {
            distributionCenterViewModel.getDistributionCenters(authToken) { success, _, _ ->
                if (success) successCount++
                latch.countDown()
            }
        }

        latch.await(5, TimeUnit.SECONDS)

        // Then
        assertEquals("Should have 3 successful calls", 3, successCount)
    }

    @Test
    fun `DistributionCenterViewModel should maintain state across calls`() {
        // Given
        val authToken = "Bearer test-token"

        // When & Then
        assertNotNull("ViewModel should not be null", distributionCenterViewModel)

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        distributionCenterViewModel.getDistributionCenters(authToken) { _, _, _ -> }
        distributionCenterViewModel.getDistributionCenters(authToken) { _, _, _ -> }

        verify(mockDistributionCenterRepository, times(2)).getDistributionCenters(authToken)
    }

    @Test
    fun `getDistributionCenters should handle 500 error`() {
        // Given
        val authToken = "Bearer test-token"

        `when`(mockDistributionCenterRepository.getDistributionCenters(authToken))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<CenterListResponse>>(0)
            val response = Response.error<CenterListResponse>(
                500,
                okhttp3.ResponseBody.create(null, "Internal Server Error")
            )
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultMessage = ""

        distributionCenterViewModel.getDistributionCenters(authToken) { success, message, _ ->
            resultSuccess = success
            resultMessage = message
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful", resultSuccess)
        assertTrue("Message should contain error code", resultMessage.contains("500"))
    }
}
