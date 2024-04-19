package ch.admin.foitt.pilotwallet.feature.login.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.NavigateToLogin
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LockViewModel @Inject constructor(
    private val navigateToLogin: NavigateToLogin,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState), DefaultLifecycleObserver {

    override val topBarState = TopBarState.None

    override fun onResume(owner: LifecycleOwner) {
        viewModelScope.launch {
            navigateToLogin().navigate()
        }
    }
}
