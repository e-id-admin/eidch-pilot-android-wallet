package ch.admin.foitt.pilotwallet.feature.settings.presentation.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.settings.presentation.composables.SettingsLanguageItem
import ch.admin.foitt.pilotwallet.platform.composables.ScreenHeader
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination
import java.util.Locale

@Destination
@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel
) {
    viewModel.checkLanguageChangedInSettings()

    LanguageScreenContent(
        language = viewModel.selectedLanguage.collectAsStateWithLifecycle().value.displayLanguage,
        supportedLanguages = viewModel.supportedLanguages.collectAsStateWithLifecycle().value,
        onUpdateLanguage = viewModel::onUpdateLanguage,
    )
}

@Composable
fun LanguageScreenContent(
    language: String,
    supportedLanguages: List<Locale>,
    onUpdateLanguage: (Locale) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState, enabled = true)
            .padding(
                PaddingValues(
                    top = Sizes.s04,
                    bottom = Sizes.s24,
                    start = Sizes.s04,
                    end = Sizes.s04,
                )
            )
    ) {
        ScreenHeader(text = stringResource(id = R.string.settings_language))
        Spacer(modifier = Modifier.height(Sizes.s04))
        supportedLanguages.forEach { locale ->
            SettingsLanguageItem(
                title = locale.displayLanguage,
                isChecked = locale.displayLanguage == language
            ) {
                onUpdateLanguage(locale)
            }
        }
    }
}

@WalletAllScreenPreview
@Composable
fun SettingsScreenPreview() {
    PilotWalletTheme {
        LanguageScreenContent(
            language = "English",
            supportedLanguages = listOf(Locale("en"), Locale("de")),
            onUpdateLanguage = {}
        )
    }
}
