package ch.admin.foitt.pilotwallet.feature.home.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.home.domain.model.GetLatestActivityFlowError
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.GetHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.SaveHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.feature.home.domain.usecase.GetLatestActivityFlow
import ch.admin.foitt.pilotwallet.platform.activity.presentation.model.HomeScreenActivity
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetLocalizedDateTime
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewsFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialStateStack
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivitiesNavArg
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.CredentialActivityDetailNavArg
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetLocalizedCredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateAllCredentialStatuses
import ch.admin.foitt.pilotwallet.platform.utils.epochSecondsToZonedDateTime
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivitiesScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialActivityDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.QrScanPermissionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.RecentCredentialActivitiesScreenDestination
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getCredentialPreviewsFlow: GetCredentialPreviewsFlow,
    private val getHomeIntroductionIsDone: GetHomeIntroductionIsDone,
    private val saveHomeIntroductionIsDone: SaveHomeIntroductionIsDone,
    private val getCredentialStateStack: GetCredentialStateStack,
    private val updateAllCredentialStatuses: UpdateAllCredentialStatuses,
    private val getLocalizedCredentialIssuerDisplay: GetLocalizedCredentialIssuerDisplay,
    private val getLocalizedDateTime: GetLocalizedDateTime,
    getLatestActivityFlow: GetLatestActivityFlow,
    @ApplicationContext private val appContext: Context,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Root

    private val _showErrorToast = MutableStateFlow(false)
    val showErrorToast = _showErrorToast.asStateFlow()

    val screenState: StateFlow<HomeScreenState> = combine(
        getCredentialPreviewsFlow(),
        getLatestActivityFlow()
    ) { credentialList, latestActivityResult ->
        when {
            credentialList.isNotEmpty() -> {
                saveHomeIntroductionIsDone()
                HomeScreenState.Credentials(
                    credentials = getCredentialStateStack(credentialList),
                    latestActivity = mapLatestActivityResult(latestActivityResult),
                    onCredentialClick = ::onCredentialPreviewClick,
                )
            }
            !getHomeIntroductionIsDone() -> HomeScreenState.Introduction
            else -> HomeScreenState.NoCredential
        }
    }.toStateFlow(HomeScreenState.Initial)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private suspend fun mapLatestActivityResult(
        latestActivityResult: Result<ActivityWithVerifier?, GetLatestActivityFlowError>
    ) = latestActivityResult.mapBoth(
        success = { it?.toHomeScreenActivity() },
        failure = {
            _showErrorToast.value = true
            null
        }
    )

    private suspend fun ActivityWithVerifier.toHomeScreenActivity(): HomeScreenActivity = HomeScreenActivity(
        id = activity.id,
        credentialId = activity.credentialId,
        type = activity.type,
        name = when (activity.type) {
            ActivityType.CREDENTIAL_RECEIVED -> getLocalizedCredentialIssuerDisplay(activity.credentialId)?.name ?: ""
            ActivityType.PRESENTATION_ACCEPTED, ActivityType.PRESENTATION_DECLINED -> verifier?.name ?: ""
        },
        dateTimeString = getLocalizedDateTime(activity.createdAt.epochSecondsToZonedDateTime()),
    )

    fun resetShowErrorToast() {
        _showErrorToast.value = false
    }

    fun onQrScan() = navManager.navigateTo(QrScanPermissionScreenDestination)

    private fun onCredentialPreviewClick(credentialId: Long) {
        navManager.navigateTo(RecentCredentialActivitiesScreenDestination(credentialId = credentialId))
    }

    fun onMoreInfo() = appContext.openLink(R.string.home_empty_view_no_credentials_more_info_link)

    fun onNoQr() = appContext.openLink(R.string.home_empty_view_no_credentials_scan_link)

    fun onRefresh() {
        viewModelScope.launch {
            updateAllCredentialStatuses()
        }.trackCompletion(_isRefreshing)
    }

    fun onActivity(activityId: Long) {
        navManager.navigateTo(
            direction = CredentialActivityDetailScreenDestination(
                navArgs = CredentialActivityDetailNavArg(activityId)
            )
        )
    }

    fun onShowAllActivities(credentialId: Long) {
        navManager.navigateTo(
            direction = CredentialActivitiesScreenDestination(
                navArgs = CredentialActivitiesNavArg(credentialId)
            )
        )
    }
}
