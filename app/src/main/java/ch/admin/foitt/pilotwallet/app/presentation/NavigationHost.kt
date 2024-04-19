package ch.admin.foitt.pilotwallet.app.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import ch.admin.foitt.pilotwallet.feature.changeLogin.presentation.ConfirmNewPinScreen
import ch.admin.foitt.pilotwallet.feature.changeLogin.presentation.ConfirmNewPinViewModel
import ch.admin.foitt.pilotwallet.feature.changeLogin.presentation.EnterCurrentPinScreen
import ch.admin.foitt.pilotwallet.feature.changeLogin.presentation.EnterCurrentPinViewModel
import ch.admin.foitt.pilotwallet.feature.changeLogin.presentation.EnterNewPinScreen
import ch.admin.foitt.pilotwallet.feature.changeLogin.presentation.EnterNewPinViewModel
import ch.admin.foitt.pilotwallet.feature.credentialDelete.presentation.CredentialDeleteScreen
import ch.admin.foitt.pilotwallet.feature.credentialDelete.presentation.CredentialDeleteViewModel
import ch.admin.foitt.pilotwallet.feature.credentialDetail.presentation.CredentialDetailScreen
import ch.admin.foitt.pilotwallet.feature.credentialDetail.presentation.CredentialDetailViewModel
import ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation.CredentialOfferErrorScreen
import ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation.CredentialOfferErrorViewModel
import ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation.CredentialOfferScreen
import ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation.CredentialOfferViewModel
import ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation.DeclineCredentialOfferScreen
import ch.admin.foitt.pilotwallet.feature.credentialOffer.presentation.DeclineCredentialOfferViewModel
import ch.admin.foitt.pilotwallet.feature.home.presentation.HomeScreen
import ch.admin.foitt.pilotwallet.feature.home.presentation.HomeViewModel
import ch.admin.foitt.pilotwallet.feature.login.presentation.BiometricLoginScreen
import ch.admin.foitt.pilotwallet.feature.login.presentation.BiometricLoginViewModel
import ch.admin.foitt.pilotwallet.feature.login.presentation.LockScreen
import ch.admin.foitt.pilotwallet.feature.login.presentation.LockViewModel
import ch.admin.foitt.pilotwallet.feature.login.presentation.NoDevicePinSetScreen
import ch.admin.foitt.pilotwallet.feature.login.presentation.NoDevicePinSetViewModel
import ch.admin.foitt.pilotwallet.feature.login.presentation.PinLoginScreen
import ch.admin.foitt.pilotwallet.feature.login.presentation.PinLoginViewModel
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.ConfirmPinScreen
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.ConfirmPinViewModel
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.EnterPinScreen
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.EnterPinViewModel
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.OnboardingIntroScreen
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.OnboardingIntroViewModel
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.OnboardingQRScreen
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.OnboardingQRViewModel
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.RegisterBiometricsScreen
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.RegisterBiometricsViewModel
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.UserPrivacyPolicyScreen
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.UserPrivacyPolicyViewModel
import ch.admin.foitt.pilotwallet.feature.policeQrCode.presentation.PoliceQrCodeScreen
import ch.admin.foitt.pilotwallet.feature.policeQrCode.presentation.PoliceQrCodeViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationCredentialListScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationCredentialListViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationDeclinedScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationDeclinedViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationEmptyWalletScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationEmptyWalletViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationFailureScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationFailureViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationNoMatchScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationNoMatchViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationRequestScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationRequestViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationSuccessScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationSuccessViewModel
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationValidationErrorScreen
import ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation.PresentationValidationErrorViewModel
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.QrScanPermissionScreen
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.QrScanPermissionViewModel
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.QrScannerScreen
import ch.admin.foitt.pilotwallet.feature.qrscan.presentation.QrScannerViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.SettingsScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.SettingsViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.AuthWithPinScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.AuthWithPinViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.EnableBiometricsErrorScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.EnableBiometricsErrorViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.EnableBiometricsLockoutScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.EnableBiometricsLockoutViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.EnableBiometricsScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.biometrics.EnableBiometricsViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.impressum.ImpressumScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.impressum.ImpressumViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.language.LanguageScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.language.LanguageViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.licences.LicencesScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.licences.LicencesViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.security.DataAnalysisScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.security.DataAnalysisViewModel
import ch.admin.foitt.pilotwallet.feature.settings.presentation.security.SecuritySettingsScreen
import ch.admin.foitt.pilotwallet.feature.settings.presentation.security.SecuritySettingsViewModel
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.InvalidCredentialErrorScreen
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.InvalidCredentialErrorViewModel
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.InvitationFailureScreen
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.InvitationFailureViewModel
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.UnknownIssuerErrorScreen
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.UnknownIssuerErrorViewModel
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.UnknownVerifierErrorScreen
import ch.admin.foitt.pilotwallet.platform.invitation.presentation.UnknownVerifierErrorViewModel
import ch.admin.foitt.pilotwallet.platform.nointernet.presentation.NoInternetConnectionScreen
import ch.admin.foitt.pilotwallet.platform.nointernet.presentation.NoInternetConnectionViewModel
import ch.admin.foitt.pilotwallet.platform.scaffold.extension.screenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.NavGraphs
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.AuthWithPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.BiometricLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmNewPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ConfirmPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDeleteScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialDetailScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.CredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.DataAnalysisScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.DeclineCredentialOfferScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsLockoutScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnableBiometricsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterCurrentPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterNewPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.EnterPinScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.HomeScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.ImpressumScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvalidCredentialErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.InvitationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LanguageScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LicencesScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.LockScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoDevicePinSetScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.NoInternetConnectionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingIntroScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.OnboardingQRScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PinLoginScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PoliceQrCodeScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationCredentialListScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationDeclinedScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationEmptyWalletScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationFailureScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationNoMatchScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationRequestScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationSuccessScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.PresentationValidationErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.QrScanPermissionScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.QrScannerScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.RegisterBiometricsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.SecuritySettingsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.SettingsScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.StartScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownIssuerErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UnknownVerifierErrorScreenDestination
import ch.admin.foitt.pilotwalletcomposedestinations.destinations.UserPrivacyPolicyScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.spec.NavHostEngine

@Composable
fun NavigationHost(
    engine: NavHostEngine,
    navController: NavHostController,
) {
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        engine = engine,
        navController = navController,
    ) {
        screenDestination(StartScreenDestination) { viewModel: StartViewModel ->
            StartScreen(viewModel)
        }

        screenDestination(LockScreenDestination) { viewModel: LockViewModel ->
            LockScreen(viewModel)
        }

        screenDestination(NoDevicePinSetScreenDestination) { viewModel: NoDevicePinSetViewModel ->
            NoDevicePinSetScreen(viewModel)
        }

        screenDestination(OnboardingIntroScreenDestination) { viewModel: OnboardingIntroViewModel ->
            OnboardingIntroScreen(viewModel)
        }

        screenDestination(OnboardingQRScreenDestination) { viewModel: OnboardingQRViewModel ->
            OnboardingQRScreen(viewModel)
        }

        screenDestination(UserPrivacyPolicyScreenDestination) { viewModel: UserPrivacyPolicyViewModel ->
            UserPrivacyPolicyScreen(viewModel)
        }

        screenDestination(EnterPinScreenDestination) { viewModel: EnterPinViewModel ->
            EnterPinScreen(viewModel)
        }

        screenDestination(ConfirmPinScreenDestination) { viewModel: ConfirmPinViewModel ->
            ConfirmPinScreen(viewModel)
        }

        screenDestination(RegisterBiometricsScreenDestination) { viewModel: RegisterBiometricsViewModel ->
            RegisterBiometricsScreen(viewModel)
        }

        screenDestination(EnableBiometricsLockoutScreenDestination) { viewModel: EnableBiometricsLockoutViewModel ->
            EnableBiometricsLockoutScreen(viewModel)
        }

        screenDestination(EnableBiometricsScreenDestination) { viewModel: EnableBiometricsViewModel ->
            EnableBiometricsScreen(viewModel)
        }

        screenDestination(EnableBiometricsErrorScreenDestination) { viewModel: EnableBiometricsErrorViewModel ->
            EnableBiometricsErrorScreen(viewModel)
        }

        screenDestination(PinLoginScreenDestination) { viewModel: PinLoginViewModel ->
            PinLoginScreen(viewModel)
        }

        screenDestination(BiometricLoginScreenDestination) { viewModel: BiometricLoginViewModel ->
            BiometricLoginScreen(viewModel)
        }

        screenDestination(SettingsScreenDestination) { viewModel: SettingsViewModel ->
            SettingsScreen(viewModel)
        }

        screenDestination(SecuritySettingsScreenDestination) { viewModel: SecuritySettingsViewModel ->
            SecuritySettingsScreen(viewModel)
        }

        screenDestination(DataAnalysisScreenDestination) { _: DataAnalysisViewModel ->
            DataAnalysisScreen()
        }

        screenDestination(LanguageScreenDestination) { viewModel: LanguageViewModel ->
            LanguageScreen(viewModel)
        }

        screenDestination(ImpressumScreenDestination) { viewModel: ImpressumViewModel ->
            ImpressumScreen(viewModel)
        }

        screenDestination(LicencesScreenDestination) { viewModel: LicencesViewModel ->
            LicencesScreen(viewModel)
        }

        screenDestination(QrScanPermissionScreenDestination) { viewModel: QrScanPermissionViewModel ->
            QrScanPermissionScreen(viewModel)
        }

        screenDestination(QrScannerScreenDestination) { viewModel: QrScannerViewModel ->
            QrScannerScreen(viewModel)
        }

        screenDestination(InvitationFailureScreenDestination) { viewModel: InvitationFailureViewModel ->
            InvitationFailureScreen(viewModel)
        }

        screenDestination(CredentialOfferScreenDestination) { viewModel: CredentialOfferViewModel ->
            CredentialOfferScreen(viewModel)
        }

        screenDestination(DeclineCredentialOfferScreenDestination) { viewModel: DeclineCredentialOfferViewModel ->
            DeclineCredentialOfferScreen(viewModel)
        }

        screenDestination(CredentialOfferErrorScreenDestination) { viewModel: CredentialOfferErrorViewModel ->
            CredentialOfferErrorScreen(viewModel)
        }

        screenDestination(UnknownIssuerErrorScreenDestination) { viewModel: UnknownIssuerErrorViewModel ->
            UnknownIssuerErrorScreen(viewModel)
        }

        screenDestination(UnknownVerifierErrorScreenDestination) { viewModel: UnknownVerifierErrorViewModel ->
            UnknownVerifierErrorScreen(viewModel)
        }

        screenDestination(NoInternetConnectionScreenDestination) { viewModel: NoInternetConnectionViewModel ->
            NoInternetConnectionScreen(viewModel)
        }

        screenDestination(PresentationRequestScreenDestination) { viewModel: PresentationRequestViewModel ->
            PresentationRequestScreen(viewModel)
        }

        screenDestination(PresentationSuccessScreenDestination) { viewModel: PresentationSuccessViewModel ->
            PresentationSuccessScreen(viewModel)
        }

        screenDestination(PresentationFailureScreenDestination) { viewModel: PresentationFailureViewModel ->
            PresentationFailureScreen(viewModel)
        }

        screenDestination(PresentationValidationErrorScreenDestination) { viewModel: PresentationValidationErrorViewModel ->
            PresentationValidationErrorScreen(viewModel)
        }

        screenDestination(PresentationCredentialListScreenDestination) { viewModel: PresentationCredentialListViewModel ->
            PresentationCredentialListScreen(viewModel)
        }

        screenDestination(PresentationEmptyWalletScreenDestination) { viewModel: PresentationEmptyWalletViewModel ->
            PresentationEmptyWalletScreen(viewModel)
        }

        screenDestination(PresentationNoMatchScreenDestination) { viewModel: PresentationNoMatchViewModel ->
            PresentationNoMatchScreen(viewModel)
        }

        screenDestination(PresentationDeclinedScreenDestination) { viewModel: PresentationDeclinedViewModel ->
            PresentationDeclinedScreen(viewModel)
        }

        screenDestination(HomeScreenDestination) { viewModel: HomeViewModel ->
            HomeScreen(viewModel)
        }

        screenDestination(PoliceQrCodeScreenDestination) { viewModel: PoliceQrCodeViewModel ->
            PoliceQrCodeScreen(viewModel)
        }

        screenDestination(CredentialDetailScreenDestination) { viewModel: CredentialDetailViewModel ->
            CredentialDetailScreen(viewModel)
        }

        screenDestination(CredentialDeleteScreenDestination) { viewModel: CredentialDeleteViewModel ->
            CredentialDeleteScreen(viewModel)
        }

        screenDestination(AuthWithPinScreenDestination) { viewModel: AuthWithPinViewModel ->
            AuthWithPinScreen(viewModel)
        }

        screenDestination(EnterCurrentPinScreenDestination) { viewModel: EnterCurrentPinViewModel ->
            EnterCurrentPinScreen(viewModel)
        }

        screenDestination(ConfirmNewPinScreenDestination) { viewModel: ConfirmNewPinViewModel ->
            ConfirmNewPinScreen(viewModel)
        }

        screenDestination(EnterNewPinScreenDestination) { viewModel: EnterNewPinViewModel ->
            EnterNewPinScreen(viewModel)
        }

        screenDestination(InvalidCredentialErrorScreenDestination) { viewModel: InvalidCredentialErrorViewModel ->
            InvalidCredentialErrorScreen(viewModel)
        }
    }
}
