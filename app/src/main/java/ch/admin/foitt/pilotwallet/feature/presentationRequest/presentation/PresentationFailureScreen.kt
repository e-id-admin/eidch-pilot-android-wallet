package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.ButtonSecondary
import ch.admin.foitt.pilotwallet.platform.composables.ResultScreenContent
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationFailureNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import ch.admin.foitt.pilotwallet.theme.errorBackgroundDark
import ch.admin.foitt.pilotwallet.theme.errorBackgroundLight
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(
    navArgsDelegate = PresentationFailureNavArg::class,
)
fun PresentationFailureScreen(viewModel: PresentationFailureViewModel) {
    PresentationFailureContent(
        dateTime = viewModel.dateTime,
        onRetry = viewModel::onRetry,
        onClose = viewModel::onClose,
    )
}

@Composable
private fun PresentationFailureContent(
    dateTime: String,
    onRetry: () -> Unit,
    onClose: () -> Unit,
) {
    ResultScreenContent(
        iconRes = R.drawable.pilot_ic_warning_big,
        dateTime = dateTime,
        message = stringResource(id = R.string.presentation_error_title),
        topColor = MaterialTheme.colorScheme.errorBackgroundLight,
        bottomColor = MaterialTheme.colorScheme.errorBackgroundDark,
        bottomContent = {
            ButtonSecondary(
                text = stringResource(id = R.string.global_error_backToHome_button),
                onClick = onClose,
                leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
            )
            Spacer(Modifier.size(Sizes.s04))
            ButtonPrimary(
                text = stringResource(id = R.string.global_error_retry_button),
                onClick = onRetry,
                leftIcon = painterResource(id = R.drawable.pilot_ic_retry),
            )
        },
        content = {
            WalletTexts.BodySmallCentered(
                text = stringResource(id = R.string.presentation_error_message),
                color = MaterialTheme.colorScheme.onError,
            )
        },
    )
}

@Composable
@WalletAllScreenPreview
private fun PresentationFailurePreview() {
    PilotWalletTheme {
        PresentationFailureContent(
            dateTime = "10th December 1990 | 11:17",
            onRetry = {},
            onClose = {},
        )
    }
}
