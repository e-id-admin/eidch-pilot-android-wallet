package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetActivitiesForCredentialFlow
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.DeleteActivity
import ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter.GetActivityListItem
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivityDetailNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.navigateUpOrToRoot
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivitiesScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivityDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialActivitiesViewModel @Inject constructor(
    getActivitiesForCredentialFlow: GetActivitiesForCredentialFlow,
    private val getActivityListItem: GetActivityListItem,
    private val setErrorDialogState: SetErrorDialogState,
    private val navManager: NavigationManager,
    private val deleteActivity: DeleteActivity,
    savedStateHandle: SavedStateHandle,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState: TopBarState = TopBarState.Details(
        navManager::navigateUpOrToRoot,
        R.string.activities_title
    )

    private val navArg = CredentialActivitiesScreenDestination.argsFrom(savedStateHandle)
    private val credentialId = navArg.credentialId

    val uiState: StateFlow<CredentialActivitiesUiState> = getActivitiesForCredentialFlow(credentialId).map { result ->
        result.mapBoth(
            success = { activitiesMap ->
                when {
                    activitiesMap.isNotEmpty() -> CredentialActivitiesUiState.Activities(activitiesMap.toMapWithActivityItemList())
                    else -> CredentialActivitiesUiState.Empty
                }
            },
            failure = {
                setErrorDialogState(ErrorDialogState.Unexpected("Could not get activities"))
                null
            },
        )
    }
        .filterNotNull()
        .toStateFlow(CredentialActivitiesUiState.Loading)

    private suspend fun Map<String, List<ActivityWithVerifier>>.toMapWithActivityItemList(): Map<String, List<ActivityListItem>> =
        this.mapValues { mapEntry ->
            mapEntry.value.map { getActivityListItem(it) }
        }

    fun onActivityClick(activityId: Long) {
        navManager.navigateTo(
            direction = CredentialActivityDetailScreenDestination(
                navArgs = CredentialActivityDetailNavArg(activityId)
            )
        )
    }

    fun onDelete(activityId: Long) {
        viewModelScope.launch {
            deleteActivity(activityId)
        }
    }

    fun onBackToHome() {
        navManager.popBackStackTo(HomeScreenDestination, false)
    }
}
