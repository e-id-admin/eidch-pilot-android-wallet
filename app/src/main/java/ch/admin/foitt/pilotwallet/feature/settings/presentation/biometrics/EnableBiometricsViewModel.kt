package ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.settings.domain.usecase.EnableBiometricsUseCases
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricManagerResult
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptType
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.presentation.AndroidBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.BiometricsError
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.map
import ch.admin.foitt.pilotwallet.platform.utils.openSecuritySettings
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsLockoutScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsScreenDestination
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EnableBiometricsViewModel @Inject constructor(
    private val enableBiometricsUseCases: EnableBiometricsUseCases,
    private val navManager: NavigationManager,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val appContext: Context,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.Details(::close, R.string.change_biometrics_title)

    private val navArgs = EnableBiometricsScreenDestination.argsFrom(savedStateHandle)
    private val pin = navArgs.pin

    private val _initializationInProgress = MutableStateFlow(false)
    val initializationInProgress = _initializationInProgress.asStateFlow()

    private val _biometricsStatus = MutableStateFlow(enableBiometricsUseCases.biometricsStatus())
    val areBiometricsEnabled = _biometricsStatus.asStateFlow().map(viewModelScope) { biometricManagerResult ->
        when (biometricManagerResult) {
            BiometricManagerResult.Available -> true
            BiometricManagerResult.CanEnroll,
            BiometricManagerResult.Disabled -> false
            BiometricManagerResult.Unsupported -> {
                Timber.w(message = "Biometrics unsupported on the enabling screen")
                false
            }
        }
    }

    fun refreshBiometricStatus(activity: FragmentActivity) {
        _biometricsStatus.value = enableBiometricsUseCases.biometricsStatus()
        if (areBiometricsEnabled.value) {
            enableBiometricsLogin(activity)
        }
    }

    fun openSettings() = appContext.openSecuritySettings()

    fun enableBiometricsLogin(activity: FragmentActivity) {
        viewModelScope.launch {
            enableBiometricsLogin(activity, pin)
        }.trackCompletion(_initializationInProgress)
    }

    private suspend fun enableBiometricsLogin(activity: FragmentActivity, pin: String) {
        Timber.d("Passphrase: Showing biometric dialog")
        val biometricPromptWrapper = AndroidBiometricPrompt(
            activity = activity,
            allowedAuthenticators = enableBiometricsUseCases.getAuthenticators(),
            promptType = BiometricPromptType.Setup
        )

        enableBiometricsUseCases.enableBiometrics(biometricPromptWrapper, pin, false)
            .onSuccess {
                close()
            }
            .onFailure { enableBiometricsError ->
                when (enableBiometricsError) {
                    BiometricsError.Locked -> {
                        Timber.w("Enable biometric error: Lockout")
                        navManager.navigateToAndClearCurrent(EnableBiometricsLockoutScreenDestination)
                    }
                    is BiometricsError.Unexpected -> {
                        Timber.e("Enable biometric error: ${enableBiometricsError.cause?.localizedMessage}")
                        navManager.navigateToAndClearCurrent(EnableBiometricsErrorScreenDestination)
                    }
                    BiometricsError.Cancelled -> { }
                }
            }
    }

    private fun close() = navManager.popBackStack()
}
