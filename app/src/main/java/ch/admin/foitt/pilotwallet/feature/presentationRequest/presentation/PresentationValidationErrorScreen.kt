package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationValidationErrorNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(
    navArgsDelegate = PresentationValidationErrorNavArg::class,
)
fun PresentationValidationErrorScreen(viewModel: PresentationValidationErrorViewModel) {
    PresentationValidationErrorContent(
        fields = viewModel.fields,
        onClose = viewModel::onClose,
    )
}

@Composable
private fun PresentationValidationErrorContent(
    fields: List<String>,
    onClose: () -> Unit,
) {
    ScrollableWithStickyBottom(
        stickyBottomContent = {
            ButtonOutlined(
                text = stringResource(id = R.string.global_error_backToHome_button),
                rightIcon = painterResource(id = R.drawable.pilot_ic_next_button),
                onClick = onClose,
            )
        },
        scrollableContent = { ScrollableContent(fields) },
    )
}

@Composable
private fun ScrollableContent(fields: List<String>) {
    Column {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.pilote_ic_presentation_validation_error),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(Sizes.s04))
        WalletTexts.TitleScreen(
            text = stringResource(id = R.string.presentation_validationError_title),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(Sizes.s06))
        WalletTexts.Body(
            text = stringResource(id = R.string.presentation_validationError_message),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(Sizes.s04))
        SubmittedDataBox(fields = fields)
    }
}

@Composable
@WalletAllScreenPreview
private fun PresentationValidationErrorPreview() {
    PilotWalletTheme {
        PresentationValidationErrorContent(
            fields = listOf("name", "firstname", "country", "age", "employment"),
            onClose = {},
        )
    }
}
