package ch.admin.foitt.pilotwallet.feature.settings.presentation.language

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetCurrentAppLocale
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetSupportedAppLanguages
import ch.admin.foitt.pilotwallet.platform.navigation.NavigationManager
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val navManager: NavigationManager,
    getSupportedAppLanguages: GetSupportedAppLanguages,
    private val getCurrentAppLocale: GetCurrentAppLocale,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.Details(navManager::popBackStack, null)

    private val defaultLanguage = Locale("en")

    private var _selectedLanguage = MutableStateFlow(defaultLanguage)
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private var _supportedLanguages = MutableStateFlow(listOf(defaultLanguage))
    val supportedLanguages = _supportedLanguages.asStateFlow()

    init {
        _supportedLanguages.value = getSupportedAppLanguages()
        _selectedLanguage.value = getCurrentAppLocale()
    }

    fun checkLanguageChangedInSettings() {
        val currentLanguage = getCurrentAppLocale()
        if (currentLanguage.language != selectedLanguage.value.language) {
            _selectedLanguage.value = currentLanguage
        }
    }

    fun onUpdateLanguage(locale: Locale) {
        _selectedLanguage.value = locale
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale.language))
    }
}
