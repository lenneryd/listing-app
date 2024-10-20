package com.tim.listing.app.ui.navigation

import android.net.Uri
import androidx.navigation.NavDestination

enum class Argument(val value: String) {
    Id("id")
}

sealed class Route(val value: String, val arguments: List<Argument> = listOf()) {
    data object ScooterList : Route("list")
    data object ScooterDetails : Route("details", arguments = listOf(Argument.Id))

    companion object {
        val entries = listOf(ScooterList, ScooterDetails)
    }
}

// Sets up a definition for a route that is extendable and allows definitions as well as specific resolved implementations.
private fun Route.routeDefinitionBuilder() = Uri.parse(value).buildUpon().apply {
    arguments.forEach { key ->
        appendPath("{${key.value}}")
    }

    // Can be extended with optional arguments as well, that will be provided with ? and & using Uri encoding/decoding.
}

fun NavDestination.asRoute(): Route = Route.entries.first { it.value == this.route }
fun Route.asRouteDefinition(): String = Uri.decode(routeDefinitionBuilder().toString())

// Standard route items get resolved without the need to add any specific additions to the uri/path.
fun Route.resolved(): String = asRouteDefinition()

// This one requires an ID, and will therefore need to be resolved separately by adding the ID to the path.
fun Route.ScooterDetails.resolved(id: Long): String =
    Uri.decode(routeDefinitionBuilder().toString()).replace("{${Argument.Id.value}}", id.toString())