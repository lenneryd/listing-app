package com.tim.listing.app.data.repo

import Fixtures
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tim.listing.app.data.api.ScooterApi
import com.tim.listing.app.data.model.ScooterResponseEntity
import com.tim.listing.app.domain.model.toScooterResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class ScooterRepositoryTest {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    lateinit var api: ScooterApi

    @Before
    fun setUp() {
        api = mockk<ScooterApi>()
    }

    @Test
    fun testGetScootersSuccess() {
        val entity: ScooterResponseEntity = json.decodeFromString(Fixtures.scooters)
        coEvery { api.getScooters() }.returns(Response.success(entity))

        runTest {
            val repo = ScooterRepository(api)
            val result = repo.getScooters().first()

            assertTrue(result.isSuccess)
            assertEquals(result.getOrThrow(), entity.toScooterResponse())
            coVerify(exactly = 1) { api.getScooters() }
        }
    }

    @Test
    fun testGetScootersFailure() {
        coEvery { api.getScooters() }.returns(Response.error(404, mockk(relaxed = true)))

        runTest {
            val repo = ScooterRepository(api)
            val result = repo.getScooters().first()

            assertTrue(result.isFailure)
            assertNotNull(result.exceptionOrNull())
        }
    }
}