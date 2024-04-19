package ch.admin.foitt.pilotwallet.feature.policeQrCode.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PoliceQrCodeNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import ch.admin.foitt.pilotwallet.theme.policeQrCodeBackground
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = PoliceQrCodeNavArg::class
)
@Composable
fun PoliceQrCodeScreen(
    viewModel: PoliceQrCodeViewModel,
) {
    val painter = viewModel.imagePainter.collectAsStateWithLifecycle(null).value
    PoliceQrCodeScreenContent(
        painter = painter
    )
}

@Composable
private fun PoliceQrCodeScreenContent(
    painter: Painter?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Sizes.s06)
    ) {
        WalletTexts.TitleScreen(
            text = stringResource(id = R.string.police_control_qrcode_scan_text)
        )
        Spacer(modifier = Modifier.height(Sizes.s06))
        if (painter != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(shape = RoundedCornerShape(Sizes.s01))
                    .background(color = MaterialTheme.colorScheme.policeQrCodeBackground)
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Sizes.s08),
                    painter = painter,
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth,
                )
            }
            Spacer(modifier = Modifier.height(Sizes.s06))
        }
        WalletTexts.Body(
            text = stringResource(id = R.string.police_control_description_text),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun CredentialDetailScreenPreview() {
    PilotWalletTheme {
        PoliceQrCodeScreenContent(
            painter = painterResource(id = R.drawable.ic_launcher_background)
        )
    }
}
