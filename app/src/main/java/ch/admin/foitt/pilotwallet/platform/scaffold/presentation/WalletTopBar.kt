@file:OptIn(ExperimentalMaterial3Api::class)

package ch.admin.foitt.pilotwallet.platform.scaffold.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
fun WalletTopBar(
    viewModel: WalletTopBarViewModel = hiltViewModel(),
) {
    val currentState = viewModel.state.collectAsStateWithLifecycle().value

    WalletTopAppBarContent(
        topBarState = currentState,
        onSettings = viewModel::openSettings
    )
}

@Composable
private fun WalletTopAppBarContent(
    topBarState: TopBarState,
    onSettings: () -> Unit,
) {
    when (topBarState) {
        TopBarState.Root -> TopBarTopLevelContent(
            onSettings = onSettings
        )

        TopBarState.RootNoSettings -> TopBarTopLevelContent()

        is TopBarState.DetailsWithCustomSettings -> TopBarBackArrow(
            titleId = topBarState.titleId,
            showSettingsButton = true,
            onUp = topBarState.onUp,
            onSettings = topBarState.onSettings
        )

        is TopBarState.Details -> TopBarBackArrow(
            titleId = topBarState.titleId,
            showSettingsButton = false,
            onUp = topBarState.onUp,
            onSettings = onSettings
        )

        TopBarState.SystemBarPadding -> SystemBarPadding()
        TopBarState.None -> {}
    }
}

@Composable
private fun TopBarTopLevelContent(onSettings: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            TopBarTitle()
        },
        navigationIcon = {},
        actions = {
            onSettings?.let {
                SettingsButton(onSettings = onSettings)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(),
    )
}

@Composable
private fun TopBarTitle() {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pilot_ic_swisscross_small),
            tint = Color.Unspecified,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(Sizes.s04))
        WalletTexts.Headline(
            text = stringResource(id = R.string.app_name),
        )
    }
}

@Composable
private fun TopBarBackArrow(
    @StringRes titleId: Int?,
    showSettingsButton: Boolean,
    onUp: () -> Unit,
    onSettings: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            titleId?.let {
                WalletTexts.TitleTopBar(
                    text = stringResource(id = titleId),
                )
            }
        },
        navigationIcon = {
            BackButton(onUp)
        },
        actions = {
            if (showSettingsButton) {
                SettingsButton(onSettings)
            }
        },
    )
}

@Composable
private fun BackButton(onUp: () -> Unit) {
    IconButton(onClick = onUp) {
        Icon(
            painter = painterResource(id = R.drawable.pilot_ic_back_navigation),
            contentDescription = stringResource(id = R.string.global_back),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun SettingsButton(onSettings: () -> Unit) {
    IconButton(onClick = onSettings) {
        Icon(
            painter = painterResource(id = R.drawable.pilot_ic_menu),
            contentDescription = stringResource(id = R.string.settings_title),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
fun SystemBarPadding() {
    Spacer(
        modifier = Modifier
            .windowInsetsTopHeight(WindowInsets.safeDrawing)
            .consumeWindowInsets(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
            )
    )
}

private class TopBarPreviewParamsProvider : PreviewParameterProvider<TopBarState> {
    override val values = sequenceOf(
        TopBarState.Root,
        TopBarState.RootNoSettings,
        TopBarState.Details(onUp = {}, titleId = R.string.settings_title),
        TopBarState.None
    )
}

@WalletComponentPreview
@Composable
private fun WalletTopBarPreview(
    @PreviewParameter(TopBarPreviewParamsProvider::class) previewParam: TopBarState,
) {
    PilotWalletTheme {
        WalletTopAppBarContent(
            topBarState = previewParam,
            onSettings = {},
        )
    }
}
