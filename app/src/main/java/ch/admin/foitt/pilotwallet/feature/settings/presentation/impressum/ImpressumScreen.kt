package ch.admin.foitt.pilotwallet.feature.settings.presentation.impressum

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.BuildConfig
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ImpressumScreen(viewModel: ImpressumViewModel) {
    ImpressumScreenContent(
        onGithub = viewModel::onGithub,
        onMoreInformation = viewModel::onMoreInformation,
        onLegals = viewModel::onLegals,
    )
}

@Composable
private fun ImpressumScreenContent(
    onGithub: () -> Unit,
    onMoreInformation: () -> Unit,
    onLegals: () -> Unit,
) {
    ScrollableWithStickyBottom(
        useStatusBarInsets = false,
        contentPadding = PaddingValues(
            vertical = Sizes.s05,
            horizontal = Sizes.s02
        ),
        scrollableContent = {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.pilot_ic_cards),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(Sizes.s06))
            Content(
                onGithub = onGithub,
                onMoreInformation = onMoreInformation,
                onLegals = onLegals,
            )
        },
        stickyBottomContent = {},
        stickyBottomPadding = PaddingValues(),
    )
}

@Composable
private fun Content(
    onGithub: () -> Unit,
    onMoreInformation: () -> Unit,
    onLegals: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = Sizes.s04,
                end = Sizes.s04,
                bottom = Sizes.s10
            )
    ) {
        WalletTexts.Body(text = stringResource(id = R.string.impressum_header_text))
        Spacer(Modifier.height(Sizes.s04))
        AppInfo(
            text = stringResource(id = R.string.impressum_build_number),
            value = BuildConfig.VERSION_CODE.toString()
        )
        HorizontalDivider(modifier = Modifier.height(Sizes.line01))
        AppInfo(
            text = stringResource(id = R.string.impressum_app_version),
            value = BuildConfig.VERSION_NAME
        )
        Spacer(Modifier.height(Sizes.s04))
        WalletTexts.TextLink(
            text = stringResource(id = R.string.impressum_github_link_text),
            rightIcon = painterResource(id = R.drawable.pilot_ic_link),
            onClick = onGithub,
        )
        Spacer(Modifier.height(Sizes.s08))
        WalletTexts.TitleSmall(text = stringResource(id = R.string.impressum_manager_title))
        Spacer(Modifier.height(Sizes.s06))
        Image(
            modifier = Modifier.fillMaxWidth(0.6f),
            contentScale = ContentScale.Inside,
            painter = painterResource(id = R.mipmap.pilot_ic_bit),
            contentDescription = null
        )
        Spacer(Modifier.height(Sizes.s12))
        WalletTexts.TitleSmall(text = stringResource(id = R.string.impressum_more_information_title))
        Spacer(Modifier.height(Sizes.s02))
        WalletTexts.TextLink(
            text = stringResource(id = R.string.impressum_more_information_link_text),
            rightIcon = painterResource(id = R.drawable.pilot_ic_link),
            onClick = onMoreInformation,
        )
        Spacer(Modifier.height(Sizes.s06))
        WalletTexts.TitleSmall(text = stringResource(id = R.string.impressum_legals_title))
        Spacer(Modifier.height(Sizes.s02))
        WalletTexts.TextLink(
            text = stringResource(id = R.string.impressum_legals_link_text),
            rightIcon = painterResource(id = R.drawable.pilot_ic_link),
            onClick = onLegals,
        )
        Spacer(Modifier.height(Sizes.s08))
        WalletTexts.TitleSmall(text = stringResource(id = R.string.impressum_disclaimer_title))
        Spacer(Modifier.height(Sizes.s02))
        WalletTexts.Body(text = stringResource(id = R.string.impressum_disclaimer_text))
    }
}

@Composable
private fun AppInfo(
    text: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Sizes.s04),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        WalletTexts.BodyLarge(text = text)
        WalletTexts.BodyLarge(text = value)
    }
}

@WalletAllScreenPreview
@Composable
fun ImpressumScreenPreview() {
    PilotWalletTheme {
        ImpressumScreenContent(
            onGithub = {},
            onMoreInformation = {},
            onLegals = {},
        )
    }
}
