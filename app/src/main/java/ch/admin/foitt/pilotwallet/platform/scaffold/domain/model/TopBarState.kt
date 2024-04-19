package ch.admin.foitt.pilotwallet.platform.scaffold.domain.model

import androidx.annotation.StringRes

sealed interface TopBarState {
    object Root : TopBarState
    object RootNoSettings : TopBarState
    data class DetailsWithCustomSettings(
        val onUp: () -> Unit,
        @StringRes
        val titleId: Int?,
        val onSettings: () -> Unit
    ) : TopBarState

    data class Details(
        val onUp: () -> Unit,
        @StringRes
        val titleId: Int?,
    ) : TopBarState

    object SystemBarPadding : TopBarState
    object None : TopBarState
}
