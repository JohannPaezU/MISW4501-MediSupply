package com.mfpe.medisupply.data.model

import org.junit.Assert.*
import org.junit.Test

class CenterListResponseTest {

    @Test
    fun `CenterListResponse should have correct properties`() {
        // Given
        val totalCount = 3
        val centers = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                createdAt = "2025-10-28"
            ),
            DistributionCenter(
                id = "center-2",
                name = "Center 2",
                address = "Address 2",
                city = "Medellín",
                country = "Colombia",
                createdAt = "2025-10-28"
            ),
            DistributionCenter(
                id = "center-3",
                name = "Center 3",
                address = "Address 3",
                city = "Cali",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        // When
        val response = CenterListResponse(
            total_count = totalCount,
            distribution_centers = centers
        )

        // Then
        assertEquals("Total count should match", totalCount, response.total_count)
        assertEquals("Distribution centers should match", centers, response.distribution_centers)
        assertEquals("Centers count should match", 3, response.distribution_centers.size)
    }

    @Test
    fun `CenterListResponse should be Serializable`() {
        // Given
        val response = CenterListResponse(
            total_count = 0,
            distribution_centers = emptyList()
        )

        // When & Then
        assertTrue("Should be Serializable", response is java.io.Serializable)
    }

    @Test
    fun `CenterListResponse should handle empty centers list`() {
        // Given & When
        val response = CenterListResponse(
            total_count = 0,
            distribution_centers = emptyList()
        )

        // Then
        assertTrue("Centers list should be empty", response.distribution_centers.isEmpty())
        assertEquals("Total count should be 0", 0, response.total_count)
    }

    @Test
    fun `CenterListResponse should handle large centers list`() {
        // Given
        val centers = (1..100).map { index ->
            DistributionCenter(
                id = "center-$index",
                name = "Center $index",
                address = "Address $index",
                city = "City $index",
                country = "Country $index",
                createdAt = "2025-10-28"
            )
        }

        // When
        val response = CenterListResponse(
            total_count = 100,
            distribution_centers = centers
        )

        // Then
        assertEquals("Should handle 100 centers", 100, response.distribution_centers.size)
        assertEquals("Total count should be 100", 100, response.total_count)
    }

    @Test
    fun `CenterListResponse should handle mismatch between total_count and list size`() {
        // Given
        val centers = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        // When
        val response = CenterListResponse(
            total_count = 10,
            distribution_centers = centers
        )

        // Then
        assertEquals("Total count should be 10", 10, response.total_count)
        assertEquals("Actual centers size should be 1", 1, response.distribution_centers.size)
        assertNotEquals("Should handle mismatch", response.total_count, response.distribution_centers.size)
    }

    @Test
    fun `CenterListResponse should support copy with different values`() {
        // Given
        val originalCenters = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        val original = CenterListResponse(
            total_count = 1,
            distribution_centers = originalCenters
        )

        val newCenters = listOf(
            DistributionCenter(
                id = "center-2",
                name = "Center 2",
                address = "Address 2",
                city = "Medellín",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        // When
        val copied = original.copy(
            total_count = 2,
            distribution_centers = newCenters
        )

        // Then
        assertEquals("Modified total count", 2, copied.total_count)
        assertEquals("Modified distribution centers", newCenters, copied.distribution_centers)
        assertNotEquals("Total count should differ", original.total_count, copied.total_count)
    }

    @Test
    fun `CenterListResponse equals should work correctly`() {
        // Given
        val centers = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        val response1 = CenterListResponse(
            total_count = 1,
            distribution_centers = centers
        )

        val response2 = CenterListResponse(
            total_count = 1,
            distribution_centers = centers
        )

        // When & Then
        assertEquals("Should be equal", response1, response2)
    }

    @Test
    fun `CenterListResponse hashCode should be consistent`() {
        // Given
        val centers = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        val response = CenterListResponse(
            total_count = 1,
            distribution_centers = centers
        )

        // When
        val hashCode1 = response.hashCode()
        val hashCode2 = response.hashCode()

        // Then
        assertEquals("Hash codes should be consistent", hashCode1, hashCode2)
    }

    @Test
    fun `CenterListResponse should handle negative total_count`() {
        // Given & When
        val response = CenterListResponse(
            total_count = -1,
            distribution_centers = emptyList()
        )

        // Then
        assertEquals("Should handle negative count", -1, response.total_count)
    }

    @Test
    fun `CenterListResponse should handle zero total_count with non-empty list`() {
        // Given
        val centers = listOf(
            DistributionCenter(
                id = "center-1",
                name = "Center 1",
                address = "Address 1",
                city = "Bogotá",
                country = "Colombia",
                createdAt = "2025-10-28"
            )
        )

        // When
        val response = CenterListResponse(
            total_count = 0,
            distribution_centers = centers
        )

        // Then
        assertEquals("Total count should be 0", 0, response.total_count)
        assertFalse("Centers list should not be empty", response.distribution_centers.isEmpty())
    }

    @Test
    fun `CenterListResponse should allow filtering centers`() {
        // Given
        val centers = listOf(
            DistributionCenter("c1", "North Center", "North Address", "Bogotá", "Colombia", "2025-10-28"),
            DistributionCenter("c2", "South Center", "South Address", "Cali", "Colombia", "2025-10-28"),
            DistributionCenter("c3", "North Warehouse", "North Address 2", "Medellín", "Colombia", "2025-10-28")
        )

        val response = CenterListResponse(
            total_count = 3,
            distribution_centers = centers
        )

        // When
        val northCenters = response.distribution_centers.filter { it.name.contains("North") }

        // Then
        assertEquals("Should have 2 north centers", 2, northCenters.size)
    }

    @Test
    fun `CenterListResponse should allow mapping centers`() {
        // Given
        val centers = listOf(
            DistributionCenter("c1", "Center 1", "Address 1", "Bogotá", "Colombia", "2025-10-28"),
            DistributionCenter("c2", "Center 2", "Address 2", "Medellín", "Colombia", "2025-10-28")
        )

        val response = CenterListResponse(
            total_count = 2,
            distribution_centers = centers
        )

        // When
        val centerNames = response.distribution_centers.map { it.name }

        // Then
        assertEquals("Should have 2 names", 2, centerNames.size)
        assertTrue("Should contain Center 1", centerNames.contains("Center 1"))
        assertTrue("Should contain Center 2", centerNames.contains("Center 2"))
    }

    @Test
    fun `CenterListResponse should handle centers with duplicate IDs`() {
        // Given
        val centers = listOf(
            DistributionCenter("c1", "Center 1", "Address 1", "Bogotá", "Colombia", "2025-10-28"),
            DistributionCenter("c1", "Center 1 Duplicate", "Address 2", "Medellín", "Colombia", "2025-10-28")
        )

        // When
        val response = CenterListResponse(
            total_count = 2,
            distribution_centers = centers
        )

        // Then
        assertEquals("Should have 2 centers", 2, response.distribution_centers.size)
        val uniqueIds = response.distribution_centers.map { it.id }.distinct()
        assertEquals("Should have 1 unique ID", 1, uniqueIds.size)
    }

    @Test
    fun `CenterListResponse should handle centers from different cities`() {
        // Given
        val centers = listOf(
            DistributionCenter("c1", "Center 1", "Address 1", "Bogotá", "Colombia", "2025-10-28"),
            DistributionCenter("c2", "Center 2", "Address 2", "Medellín", "Colombia", "2025-10-28"),
            DistributionCenter("c3", "Center 3", "Address 3", "Cali", "Colombia", "2025-10-28")
        )

        val response = CenterListResponse(
            total_count = 3,
            distribution_centers = centers
        )

        // When
        val cities = response.distribution_centers.map { it.city }.distinct()

        // Then
        assertEquals("Should have 3 different cities", 3, cities.size)
        assertTrue("Should contain Bogotá", cities.contains("Bogotá"))
        assertTrue("Should contain Medellín", cities.contains("Medellín"))
        assertTrue("Should contain Cali", cities.contains("Cali"))
    }
}
