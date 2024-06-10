package ch.admin.foitt.pilotwallet.feature.settings.presentation.verification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import ch.admin.foitt.pilotwallet.theme.policeQrCodeBackground
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination
fun GetVerifiedScreen(viewModel: GetVerifiedViewModel) {
    GetVerifiedScreenContent(
        onVerifier = viewModel::onVerifier,
    )
}

@Composable
private fun GetVerifiedScreenContent(
    onVerifier: () -> Unit,
) = ScrollableWithStickyBottom(
    stickyBottomContent = {},
    stickyBottomPadding = PaddingValues(),
    useStatusBarInsets = false,
    contentPadding = PaddingValues(
        vertical = Sizes.s05,
        horizontal = Sizes.s02
    ),
) {
    Image(
        modifier = Modifier.fillMaxWidth(),
        painter = painterResource(id = R.drawable.pilot_ic_qr_scan),
        contentDescription = null,
        alignment = Alignment.Center,
        contentScale = ContentScale.Fit
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    MainContent(
        onVerifier = onVerifier,
    )
}

@Composable
private fun MainContent(
    onVerifier: () -> Unit,
) = Column(
    modifier = Modifier.padding(horizontal = Sizes.s04)
) {
    WalletTexts.Body(
        text = stringResource(id = R.string.get_verified_text)
    )
    Spacer(modifier = Modifier.height(Sizes.s06))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(Sizes.s01))
            .background(color = MaterialTheme.colorScheme.policeQrCodeBackground)
            .padding(
                top = Sizes.s12,
                start = Sizes.s12,
                end = Sizes.s12,
                bottom = Sizes.s08,
            )
    ) {
        Box(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.surface
            )
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Sizes.s07),
                painter = painterResource(id = R.drawable.pilot_qr_verifier_link),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
            )
        }
        Spacer(modifier = Modifier.height(Sizes.s03))
        WalletTexts.TextLink(
            text = stringResource(id = R.string.get_verified_link_text),
            rightIcon = painterResource(id = R.drawable.pilot_ic_link),
            onClick = onVerifier,
        )
    }
}

@Composable
@WalletAllScreenPreview
private fun GetVerifiedScreenPreview() {
    PilotWalletTheme {
        GetVerifiedScreenContent(
            onVerifier = {},
        )
    }
}
