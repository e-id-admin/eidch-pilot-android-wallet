package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation

import androidx.lifecycle.SavedStateHandle
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.CredentialActivityDetail
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetCredentialActivityDetailFlow
import ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation.model.ActivityDetailUiState
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.DeleteActivity
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromData
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcherScope
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.epochSecondsToZonedDateTime
import ch.admin.foitt.pilotwallet.platform.utils.toPainter
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivityDetailScreenDestination
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialActivityDetailViewModel @Inject constructor(
    private val navManager: NavigationManager,
    getCredentialActivityDetailFlow: GetCredentialActivityDetailFlow,
    private val deleteActivity: DeleteActivity,
    private val getLocalizedDateTime: GetLocalizedDateTime,
    private val getDrawableFromData: GetDrawableFromData,
    private val getCredentialCardState: GetCredentialCardState,
    private val setErrorDialogState: SetErrorDialogState,
    @IoDispatcherScope private val ioDispatcherScope: CoroutineScope,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState =
        TopBarState.DetailsWithCustomSettings(
            onUp = navManager::navigateUp,
            titleId = null,
            onSettings = { _showBottomSheet.value = true }
        )

    private val navArgs = CredentialActivityDetailScreenDestination.argsFrom(savedStateHandle)

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()
    private var shouldDelete = false

    val uiState: StateFlow<ActivityDetailUiState> = getCredentialActivityDetailFlow(navArgs.activityId).map { result ->
        result.mapBoth(
            success = { mapToActivityDetailUiState(it) },
            failure = { error ->
                setErrorDialogState(ErrorDialogState.Unexpected(error.toString()))
                null
            }
        )
    }.filterNotNull()
        .toStateFlow(ActivityDetailUiState.Loading)

    private suspend fun mapToActivityDetailUiState(detail: CredentialActivityDetail) =
        ActivityDetailUiState.Success(
            actor = detail.actor,
            actorLogo = getDrawableFromData(detail.actorLogo)?.toPainter(),
            createdAt = getLocalizedDateTime(detail.createdAt.epochSecondsToZonedDateTime()),
            type = detail.type,
            cardState = getCredentialCardState(detail.credentialPreview),
            claims = detail.claims,
        )

    fun onDelete() {
        _showBottomSheet.value = false
        navManager.popBackStackTo(CredentialActivityDetailScreenDestination, true)
        shouldDelete = true
    }

    fun onBottomSheetDismiss() {
        _showBottomSheet.value = false
    }

    override fun onCleared() {
        super.onCleared()
        ioDispatcherScope.launch {
            if (shouldDelete) {
                deleteActivity(navArgs.activityId)
            }
        }
    }
}
