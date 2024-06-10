package ch.admin.foitt.pilotwallet.platform.navigation.utils

import ch.admin.foitt.pilotwalletcomposedestinations.destinations.BiometricLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LockScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoDevicePinSetScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingIntroScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingQRScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PinLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.RegisterBiometricsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.StartScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UserPrivacyPolicyScreenDestination

// Destinations that should not trigger a lock screen
// it implicitly means that the database should either not exist, or be closed and encrypted, on these destinations
val blackListedDestinationsLockScreen = listOf(
    StartScreenDestination,
    NoDevicePinSetScreenDestination,
    LockScreenDestination,
    OnboardingIntroScreenDestination,
    OnboardingQRScreenDestination,
    PinLoginScreenDestination,
    BiometricLoginScreenDestination,
    UserPrivacyPolicyScreenDestination,
    EnterPinScreenDestination,
    ConfirmPinScreenDestination,
    RegisterBiometricsScreenDestination,
)

// destinations that should not trigger the session timeout navigation
val blackListedDestinationsSessionTimeout = blackListedDestinationsLockScreen
