package ch.admin.foitt.pilotwallet.platform.scaffold.extension

import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.Destination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.manualcomposablecalls.composable

inline fun <reified T2 : ScreenViewModel> ManualComposableCallsBuilder.screenDestination(
    destination: Destination,
    crossinline screen: @Composable (viewModel: T2) -> Unit,
) {
    composable(destination) {
        val viewModel: T2 = hiltViewModel()
        val currentActivity = LocalActivity.current
        DisposableEffect(currentActivity, viewModel) {
            viewModel.syncScaffoldState(currentActivity::enableEdgeToEdge)
            onDispose { }
        }
        val modifier = if (viewModel.addBottomSystemBarPaddings) {
            Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
        } else {
            Modifier
        }
        Box(modifier = modifier.fillMaxSize()) {
            screen(viewModel)
        }
    }
}

fun NavigationManager.navigateUpOrToRoot() {
    if (previousDestination != null) {
        navigateUp()
    } else {
        navigateToAndClearCurrent(HomeScreenDestination)
    }
}
