package ch.admin.foitt.pilotwallet.platform.scaffold.presentation

import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.SystemBarsSetter
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.theme.DefaultDarkScrim
import ch.admin.foitt.pilotwallet.theme.DefaultLightScrim
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

abstract class ScreenViewModel(
    private val setTopBarState: SetTopBarState,
    val addBottomSystemBarPaddings: Boolean = true,
) : ViewModel() {
    protected abstract val topBarState: TopBarState

    fun syncScaffoldState(systemBarsSetter: SystemBarsSetter) {
        setTopBarState(topBarState)
        syncSystemBars(systemBarsSetter)
    }

    private fun syncSystemBars(
        systemBarsSetter: SystemBarsSetter
    ) {
        val topSystemBarStyle = SystemBarStyle.light(Color.TRANSPARENT, DefaultLightScrim.toArgb())
        val bottomSystemBarStyle = if (addBottomSystemBarPaddings) {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        } else {
            SystemBarStyle.dark(DefaultDarkScrim.toArgb())
        }

        systemBarsSetter(
            statusBarStyle = topSystemBarStyle,
            navigationBarStyle = bottomSystemBarStyle,
        )
    }

    protected fun <T> Flow<T>.toStateFlow(initialValue: T) = this.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue,
    )
}
