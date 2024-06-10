package ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.GetLast3ActivitiesForCredentialFlow
import ch.admin.foitt.pilotwallet.feature.recentCredentialActivities.domain.model.GetLast3ActivitiesForCredentialFlowError
import ch.admin.foitt.pilotwallet.platform.activity.presentation.adapter.GetActivityListItem
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.ActivityListItem
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivitiesNavArg
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivityDetailNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.policeQrCode.usecase.GetPoliceQrCode
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateCredentialStatus
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivitiesScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivityDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDeleteScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PoliceQrCodeScreenDestination
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias ActivityWithVerifierListResult = Result<List<ActivityWithVerifier>, GetLast3ActivitiesForCredentialFlowError>

@HiltViewModel
class RecentCredentialActivitiesViewModel @Inject constructor(
    getCredentialPreviewFlow: GetCredentialPreviewFlow,
    private val getPoliceQrCode: GetPoliceQrCode,
    private val getCredentialCardState: GetCredentialCardState,
    private val updateCredentialStatus: UpdateCredentialStatus,
    getLast3ActivitiesForCredentialFlow: GetLast3ActivitiesForCredentialFlow,
    private val getActivityListItem: GetActivityListItem,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.DetailsWithCustomSettings(
        onUp = navManager::navigateUp,
        titleId = null,
        onSettings = { _showBottomSheet.value = true }
    )

    private val navArgs = CredentialActivitiesScreenDestination.argsFrom(savedStateHandle)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    private val _showErrorToast = MutableStateFlow(false)
    val showErrorToast = _showErrorToast.asStateFlow()

    private var qrCodeImageData = ""
    val showPoliceControlItem = flow {
        getPoliceQrCode(credentialId = navArgs.credentialId)
            .onSuccess { imageData ->
                emit(true)
                qrCodeImageData = imageData
            }
            .onFailure { error ->
                Timber.e(error.toString())
                emit(false)
            }
    }

    val uiState: Flow<RecentCredentialActivitiesUiState> = combine(
        getCredentialPreviewFlow(credentialId = navArgs.credentialId),
        getLast3ActivitiesForCredentialFlow(navArgs.credentialId),
    ) { credentialPreview: CredentialPreview, activityItemList: ActivityWithVerifierListResult ->
        RecentCredentialActivitiesUiState.Success(
            credentialCardState = getCredentialCardState(credentialPreview),
            activities = mapActivityListResult(activityItemList)
        )
    }.toStateFlow(RecentCredentialActivitiesUiState.Loading)

    init {
        viewModelScope.launch {
            updateCredentialStatus(navArgs.credentialId)
        }
    }

    private suspend fun mapActivityListResult(
        activityWithVerifierListResult: ActivityWithVerifierListResult
    ): List<ActivityListItem> =
        activityWithVerifierListResult.mapBoth(
            success = { activityWithVerifierList ->
                activityWithVerifierList.map { activityWithVerifier ->
                    getActivityListItem(activityWithVerifier)
                }
            },
            failure = {
                _showErrorToast.value = true
                emptyList()
            }
        )

    fun resetErrorToast() {
        _showErrorToast.value = false
    }

    fun onActivityClick(activityId: Long) {
        _showBottomSheet.value = false
        navManager.navigateTo(
            direction = CredentialActivityDetailScreenDestination(
                navArgs = CredentialActivityDetailNavArg(activityId = activityId)
            )
        )
    }

    fun onShowAllActivities() {
        _showBottomSheet.value = false
        navManager.navigateTo(
            direction = CredentialActivitiesScreenDestination(
                navArgs = CredentialActivitiesNavArg(credentialId = navArgs.credentialId)
            )
        )
    }

    fun onBottomSheetDismiss() {
        _showBottomSheet.value = false
    }

    fun onShowCredentialContent() {
        _showBottomSheet.value = false
        navManager.navigateTo(CredentialDetailScreenDestination(credentialId = navArgs.credentialId))
    }

    fun onShowQrCode() {
        _showBottomSheet.value = false
        navManager.navigateTo(PoliceQrCodeScreenDestination(imageData = qrCodeImageData))
    }

    fun onDelete() {
        _showBottomSheet.value = false
        navManager.navigateTo(CredentialDeleteScreenDestination(credentialId = navArgs.credentialId))
    }

    fun onRefresh() {
        viewModelScope.launch {
            updateCredentialStatus(navArgs.credentialId)
        }.trackCompletion(_isRefreshing)
    }
}
