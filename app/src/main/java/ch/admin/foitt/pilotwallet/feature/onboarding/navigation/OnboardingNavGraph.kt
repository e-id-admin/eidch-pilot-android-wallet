package ch.admin.foitt.pilotwallet.feature.onboarding.navigation

import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph
@NavGraph(route = OnboardingNavGraph.NAME)
annotation class OnboardingNavGraph(
    val start: Boolean = false
) {
    companion object {
        const val NAME = "onboardingNavGraph"
    }
}
