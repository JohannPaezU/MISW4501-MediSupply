package com.mfpe.medisupply.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.mfpe.medisupply.data.model.RegisterVisitRequest
import com.mfpe.medisupply.data.model.RegisterVisitResponse
import com.mfpe.medisupply.data.model.VisitClient
import com.mfpe.medisupply.data.model.VisitGeolocation
import com.mfpe.medisupply.data.repository.VisitRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class VisitsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockVisitRepository: VisitRepository

    @Mock
    private lateinit var mockCall: Call<RegisterVisitResponse>

    private lateinit var visitsViewModel: VisitsViewModel

    @Before
    fun setUp() {
        visitsViewModel = VisitsViewModel(mockVisitRepository)
    }

    @Test
    fun `registerCompletedVisit should call repository method`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Test observations",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
            val response = Response.success(RegisterVisitResponse(
                id = id,
                clientId = VisitClient("client-123", "John Doe"),
                expectedDate = Date(),
                visitDate = Date(),
                observations = "Test observations",
                visualEvidenceUrl = "https://example.com/image.jpg",
                expectedGeoLocation = geolocation,
                reportGeoLocation = geolocation,
                status = "completed"
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        var resultSuccess = false
        var resultMessage = ""
        var resultData: RegisterVisitResponse? = null

        visitsViewModel.registerCompletedVisit(authToken, id, request) { success, message, data ->
            resultSuccess = success
            resultMessage = message
            resultData = data
        }

        // Then
        verify(mockVisitRepository).registerCompletedVisit(authToken, id, request)
        assertTrue("Result should be successful", resultSuccess)
        assertNotNull("Result data should not be null", resultData)
        assertEquals("Visit registered.", resultMessage)
    }

    @Test
    fun `registerCompletedVisit should handle success response`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Success test",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
            val response = Response.success(RegisterVisitResponse(
                id = id,
                clientId = VisitClient("client-123", "John Doe"),
                expectedDate = Date(),
                visitDate = Date(),
                observations = "Success test",
                visualEvidenceUrl = "https://example.com/image.jpg",
                expectedGeoLocation = geolocation,
                reportGeoLocation = geolocation,
                status = "completed"
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultMessage = ""
        var resultData: RegisterVisitResponse? = null

        visitsViewModel.registerCompletedVisit(authToken, id, request) { success, message, data ->
            resultSuccess = success
            resultMessage = message
            resultData = data
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertTrue("Should be successful", resultSuccess)
        assertEquals("Visit registered.", resultMessage)
        assertNotNull("Data should not be null", resultData)
        assertEquals(id, resultData?.id)
    }

    @Test
    fun `registerCompletedVisit should handle error response`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Error test",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            val response = Response.error<RegisterVisitResponse>(
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

        visitsViewModel.registerCompletedVisit(authToken, id, request) { success, message, _ ->
            resultSuccess = success
            resultMessage = message
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful", resultSuccess)
        assertTrue("Message should contain error", resultMessage.contains("Error registering visit"))
    }

    @Test
    fun `registerCompletedVisit should handle network failure`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Failure test",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            callback.onFailure(mockCall, Throwable("Network error"))
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false
        var resultMessage = ""

        visitsViewModel.registerCompletedVisit(authToken, id, request) { success, message, _ ->
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
    fun `registerCompletedVisit should handle null response body`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Null response test",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            val response = Response.success<RegisterVisitResponse>(null)
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false

        visitsViewModel.registerCompletedVisit(authToken, id, request) { success, _, _ ->
            resultSuccess = success
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful with null body", resultSuccess)
    }

    @Test
    fun `registerCompletedVisit should handle empty token`() {
        // Given
        val authToken = ""
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Empty token test",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            val response = Response.error<RegisterVisitResponse>(
                401,
                okhttp3.ResponseBody.create(null, "Unauthorized")
            )
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(1)
        var resultSuccess = false

        visitsViewModel.registerCompletedVisit(authToken, id, request) { success, _, _ ->
            resultSuccess = success
            latch.countDown()
        }

        latch.await(2, TimeUnit.SECONDS)

        // Then
        assertFalse("Should not be successful with empty token", resultSuccess)
    }

    @Test
    fun `registerCompletedVisit should be callable multiple times`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request1 = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Multiple calls test 1",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )
        val request2 = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Multiple calls test 2",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )
        val request3 = RegisterVisitRequest(
            visitDate = Date(),
            observations = "Multiple calls test 3",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request1))
            .thenReturn(mockCall)
        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request2))
            .thenReturn(mockCall)
        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request3))
            .thenReturn(mockCall)

        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterVisitResponse>>(0)
            val geolocation = VisitGeolocation("geo-1", "123 Main St", 4.6097, -74.0817)
            val response = Response.success(RegisterVisitResponse(
                id = id,
                clientId = VisitClient("client-123", "John Doe"),
                expectedDate = Date(),
                visitDate = Date(),
                observations = "Multiple calls test",
                visualEvidenceUrl = "https://example.com/image.jpg",
                expectedGeoLocation = geolocation,
                reportGeoLocation = geolocation,
                status = "completed"
            ))
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // When
        val latch = CountDownLatch(3)
        var successCount = 0

        visitsViewModel.registerCompletedVisit(authToken, id, request1) { success, _, _ ->
            if (success) successCount++
            latch.countDown()
        }
        visitsViewModel.registerCompletedVisit(authToken, id, request2) { success, _, _ ->
            if (success) successCount++
            latch.countDown()
        }
        visitsViewModel.registerCompletedVisit(authToken, id, request3) { success, _, _ ->
            if (success) successCount++
            latch.countDown()
        }

        latch.await(5, TimeUnit.SECONDS)

        // Then
        assertEquals("Should have 3 successful calls", 3, successCount)
        verify(mockVisitRepository).registerCompletedVisit(authToken, id, request1)
        verify(mockVisitRepository).registerCompletedVisit(authToken, id, request2)
        verify(mockVisitRepository).registerCompletedVisit(authToken, id, request3)
    }

    @Test
    fun `VisitsViewModel should maintain state across calls`() {
        // Given
        val authToken = "Bearer test-token"
        val id = "visit-123"
        val request = RegisterVisitRequest(
            visitDate = Date(),
            observations = "State test",
            visualEvidence = "https://example.com/image.jpg",
            latitude = 4.6097,
            longitude = -74.0817
        )

        // When & Then
        assertNotNull("ViewModel should not be null", visitsViewModel)

        `when`(mockVisitRepository.registerCompletedVisit(authToken, id, request))
            .thenReturn(mockCall)

        visitsViewModel.registerCompletedVisit(authToken, id, request) { _, _, _ -> }
        visitsViewModel.registerCompletedVisit(authToken, id, request) { _, _, _ -> }

        verify(mockVisitRepository, times(2)).registerCompletedVisit(authToken, id, request)
    }
}

