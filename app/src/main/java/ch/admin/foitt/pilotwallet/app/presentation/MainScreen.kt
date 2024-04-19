package ch.admin.foitt.pilotwallet.app.presentation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenContainer
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine

@Composable
fun MainScreen(
    activity: FragmentActivity,
    viewModel: MainViewModel = hiltViewModel(),
) {
    PilotWalletTheme {
        val engine = rememberNavHostEngine(
            navHostContentAlignment = Alignment.Center,
            rootDefaultAnimations = RootNavGraphDefaultAnimations(
                enterTransition = {
                    slideInHorizontally(tween(durationMillis = 300)) + fadeIn(
                        tween(
                            durationMillis = 300
                        )
                    )
                },
                exitTransition = {
                    slideOutHorizontally(tween(durationMillis = 300)) + fadeOut(
                        tween(
                            durationMillis = 300
                        )
                    )
                }
            ),
            defaultAnimationsForNestedNavGraph = mapOf()
        )

        val navController = engine.rememberNavController()
        viewModel.initNavHost(navController)

        CompositionLocalProvider(LocalActivity provides activity) {
            ScreenContainer {
                NavigationHost(
                    engine = engine,
                    navController = navController,
                )
            }
        }

        LaunchedEffect(activity.intent) {
            viewModel.parseIntent(activity.intent)
        }
    }
}
