package com.tim.listing.app.ui.navigation

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteTest {

    @Test
    fun testScooterList() {
        assertEquals(Route.ScooterList.value, Route.ScooterList.asRouteDefinition())
    }

    @Test
    fun testScooterDetails() {
        assertEquals("${Route.ScooterDetails.value}/42", Route.ScooterDetails.resolved(42))
        assertEquals("${Route.ScooterDetails.value}/17", Route.ScooterDetails.resolved(17))
        // Expects details/{id}
        assertEquals("${Route.ScooterDetails.value}/{${Argument.Id.value}}", Route.ScooterDetails.asRouteDefinition())
    }

}