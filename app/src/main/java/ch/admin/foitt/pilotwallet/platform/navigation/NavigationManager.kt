package ch.admin.foitt.pilotwallet.platform.navigation

import androidx.annotation.MainThread
import androidx.navigation.NavHostController
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.Destination
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.coroutines.flow.StateFlow

interface NavigationManager {

    val currentDestination: Destination?
    val previousDestination: Destination?
    val currentDestinationFlow: StateFlow<Destination?>

    fun setNavHost(navHost: NavHostController)

    @MainThread
    fun navigateTo(
        direction: Direction,
    )

    @MainThread
    fun navigateToAndClearCurrent(
        direction: Direction,
    )

    @MainThread
    fun navigateToAndPopUpTo(
        direction: Direction,
        route: String,
    )

    @MainThread
    fun popBackStack()

    @MainThread
    fun popBackStackTo(
        destination: Destination,
        inclusive: Boolean,
    ): Boolean

    @MainThread
    fun navigateUp()
}
