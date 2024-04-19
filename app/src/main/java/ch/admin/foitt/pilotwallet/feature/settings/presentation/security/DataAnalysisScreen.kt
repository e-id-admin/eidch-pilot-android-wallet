package ch.admin.foitt.pilotwallet.feature.settings.presentation.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun DataAnalysisScreen() {
    DataAnalysisScreenContent()
}

@Composable
private fun DataAnalysisScreenContent() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(
                start = Sizes.s08,
                top = Sizes.s05,
                end = Sizes.s08,
                bottom = Sizes.s05
            ),
    ) {
        WalletTexts.TitleScreen(
            text = stringResource(id = R.string.dataAnalysis_title),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(Sizes.s10))
        WalletTexts.Body(
            text = stringResource(id = R.string.dataAnalysis_text),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun DataAnalysisScreenContentPreview() {
    PilotWalletTheme {
        DataAnalysisScreenContent()
    }
}
