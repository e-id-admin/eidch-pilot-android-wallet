package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.onboarding.domain.usecase.RegisterBiometricsUseCases
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricManagerResult
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptType
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.presentation.AndroidBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.BiometricsError
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.InitializePassphraseError
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.openSecuritySettings
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
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
class RegisterBiometricsViewModel @Inject constructor(
    private val registerBiometricsUseCases: RegisterBiometricsUseCases,
    private val navManager: NavigationManager,
    @ApplicationContext private val appContext: Context,
    setTopBarState: SetTopBarState,
    savedStateHandle: SavedStateHandle,
) : ScreenViewModel(setTopBarState) {
    override val topBarState = TopBarState.None

    private val navArgs = EnableBiometricsScreenDestination.argsFrom(savedStateHandle)
    private val pin = navArgs.pin

    private val _initializationInProgress = MutableStateFlow(false)
    val initializationInProgress = _initializationInProgress.asStateFlow()

    private val _screenState = MutableStateFlow<RegisterBiometricsScreenState>(RegisterBiometricsScreenState.Initial)
    val screenState = _screenState.asStateFlow()

    fun refreshScreenState() {
        _screenState.value = getScreenState()
    }

    fun enableBiometrics(activity: FragmentActivity) {
        viewModelScope.launch {
            initializeBiometrics(activity, pin)
        }.trackCompletion(_initializationInProgress)
    }

    fun openSettings() = appContext.openSecuritySettings()

    fun declineBiometrics() {
        viewModelScope.launch {
            initializeWithoutBiometrics(pin)
        }.trackCompletion(_initializationInProgress)
    }

    private suspend fun initializeBiometrics(activity: FragmentActivity, pin: String) {
        Timber.d("Passphrase: Showing biometric dialog")
        val biometricPromptWrapper = AndroidBiometricPrompt(
            activity = activity,
            allowedAuthenticators = registerBiometricsUseCases.getAuthenticators(),
            promptType = BiometricPromptType.Setup,
        )

        registerBiometricsUseCases.enableBiometrics(
            promptWrapper = biometricPromptWrapper,
            pin = pin,
            fromSetup = true,
        ).onSuccess {
            saveOnboardingCompletedState()
            navigateToHomeScreen()
        }.onFailure { initializationError ->
            when (initializationError) {
                BiometricsError.Locked -> {
                    Timber.w("Biometrics registration: biometrics Locked")
                    _screenState.value = RegisterBiometricsScreenState.Lockout
                }
                is BiometricsError.Unexpected -> {
                    Timber.e("Biometrics registration: Unexpected")
                    _screenState.value = RegisterBiometricsScreenState.Error
                }
                BiometricsError.Cancelled -> { }
            }
        }
    }

    private suspend fun initializeWithoutBiometrics(pin: String) {
        Timber.d("Passphrase: initialize without biometrics")
        registerBiometricsUseCases.initializePassphrase(pin, null)
            .onSuccess {
                saveOnboardingCompletedState()
                navigateToHomeScreen()
            }
            .onFailure { error: InitializePassphraseError ->
                registerBiometricsUseCases.setErrorDialogState(
                    ErrorDialogState.Wallet(
                        errorDetails = error.throwable?.localizedMessage,
                    )
                )
            }
    }

    private fun getScreenState(): RegisterBiometricsScreenState = when (registerBiometricsUseCases.biometricsStatus()) {
        BiometricManagerResult.Available -> {
            if (
                screenState.value is RegisterBiometricsScreenState.Lockout ||
                screenState.value is RegisterBiometricsScreenState.Error
            ) {
                screenState.value
            } else {
                RegisterBiometricsScreenState.Available
            }
        }
        BiometricManagerResult.CanEnroll,
        BiometricManagerResult.Disabled,
        BiometricManagerResult.Unsupported -> RegisterBiometricsScreenState.Disabled
    }

    private fun saveOnboardingCompletedState() {
        viewModelScope.launch {
            registerBiometricsUseCases.saveOnboardingState(isCompleted = true)
        }
    }

    private fun navigateToHomeScreen() {
        navManager.navigateToAndPopUpTo(
            direction = HomeScreenDestination,
            route = OnboardingNavGraph.NAME,
        )
    }
}
