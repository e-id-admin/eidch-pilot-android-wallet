package ch.admin.foitt.pilotwallet.platform.credential.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimImage
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimText
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import coil.compose.AsyncImage

@Composable
fun CredentialClaimsScreenContent(
    topContent: @Composable LazyItemScope.() -> Unit = {},
    bottomContent: @Composable LazyItemScope.() -> Unit = {},
    title: String,
    claims: List<CredentialClaimData>,
    credentialCardState: CredentialCardState,
) = LazyColumn(
    modifier = Modifier
        .fillMaxWidth()
) {
    item {
        topContent()
    }
    item {
        CredentialHalfCard(
            credentialCardState = credentialCardState,
        )
    }
    item {
        Spacer(modifier = Modifier.height(Sizes.s06))
        WalletTexts.TitleSmall(
            text = title,
            modifier = Modifier.padding(horizontal = Sizes.s08),
        )
        Spacer(modifier = Modifier.height(Sizes.s04))
    }
    itemsIndexed(claims) { index, claim ->
        Column(
            modifier = Modifier.padding(
                vertical = Sizes.s02,
                horizontal = Sizes.s08
            )
        ) {
            WalletTexts.LabelSmall(text = claim.localizedKey)
            when (claim) {
                is CredentialClaimText -> {
                    WalletTexts.BodyLarge(text = claim.value)
                }
                is CredentialClaimImage -> {
                    AsyncImage(
                        modifier = Modifier
                            .heightIn(max = Sizes.claimImageMaxHeight)
                            .fillMaxWidth(),
                        model = claim.imageData,
                        alignment = Alignment.TopStart,
                        contentScale = ContentScale.Fit,
                        contentDescription = null,
                        filterQuality = FilterQuality.High,
                    )
                }
            }
        }
        if (index < claims.lastIndex) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sizes.s08),
                thickness = Sizes.line01,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
    item {
        bottomContent()
    }
}

@WalletComponentPreview
@Composable
private fun CredentialClaimsScreenContentPreview() {
    PilotWalletTheme {
        CredentialClaimsScreenContent(
            topContent = { },
            bottomContent = { },
            title = stringResource(id = R.string.presentation_attributes_title),
            claims = CredentialMocks.claimList,
            credentialCardState = CredentialMocks.cardState01,
        )
    }
}
