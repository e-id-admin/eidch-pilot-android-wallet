package ch.admin.foitt.pilotwallet.feature.home.presentation

import android.content.Context
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.GetHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.feature.home.domain.repository.SaveHomeIntroductionIsDone
import ch.admin.foitt.pilotwallet.platform.credential.domain.usecase.GetCredentialPreviewsFlow
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialStateStack
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openLink
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.QrScanPermissionScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getCredentialPreviewsFlow: GetCredentialPreviewsFlow,
    private val getHomeIntroductionIsDone: GetHomeIntroductionIsDone,
    private val saveHomeIntroductionIsDone: SaveHomeIntroductionIsDone,
    private val getCredentialStateStack: GetCredentialStateStack,
    @ApplicationContext private val appContext: Context,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Root

    val screenState: StateFlow<HomeScreenState> = getCredentialPreviewsFlow().map { credentialList ->
        when {
            credentialList.isNotEmpty() -> {
                saveHomeIntroductionIsDone()
                HomeScreenState.Credentials(
                    credentials = getCredentialStateStack(credentialList),
                    onCredentialClick = ::onCredentialPreviewClick,
                )
            }
            !getHomeIntroductionIsDone() -> HomeScreenState.Introduction
            else -> HomeScreenState.NoCredential
        }
    }.toStateFlow(HomeScreenState.Initial)

    fun onQrScan() = navManager.navigateTo(QrScanPermissionScreenDestination)

    private fun onCredentialPreviewClick(credentialId: Long) {
        navManager.navigateTo(CredentialDetailScreenDestination(credentialId = credentialId))
    }

    fun onMoreInfo() = appContext.openLink(R.string.home_empty_view_no_credentials_more_info_link)

    fun onNoQr() = appContext.openLink(R.string.home_empty_view_no_credentials_scan_link)
}
