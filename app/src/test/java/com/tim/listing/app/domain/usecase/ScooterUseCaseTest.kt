package com.tim.listing.app.domain.usecase

import Fixtures
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tim.listing.app.data.model.ScooterResponseEntity
import com.tim.listing.app.data.repo.ScooterRepository
import com.tim.listing.app.domain.model.toScooterResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScooterUseCaseTest {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Test
    fun testGetScooters() {
        runTest {
            val repo = mockk<ScooterRepository>()
            val entity: ScooterResponseEntity = json.decodeFromString(Fixtures.scooters)

            every { repo.getScooters() }.returns(
                flowOf(Result.success(entity.toScooterResponse()))
            )

            val useCase = ScooterUseCase(repo = repo)
            val result = useCase.getScooters().first()

            assertTrue(result.isSuccess)
            assertEquals("Stockholm", result.getOrThrow().name)
            assertEquals(12, result.getOrThrow().scooters.size)

            verify(exactly = 1) {
                repo.getScooters()
            }

        }
    }
}