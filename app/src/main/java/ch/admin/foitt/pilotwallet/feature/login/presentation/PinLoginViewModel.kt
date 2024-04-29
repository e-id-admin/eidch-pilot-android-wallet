package ch.admin.foitt.pilotwallet.feature.login.presentation

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.GetLockoutDuration
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.IncreaseFailedLoginAttemptsCounter
import ch.admin.foitt.pilotwallet.feature.login.domain.usecase.ResetLockout
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.HandleDeeplink
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginError
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithPin
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinCheckResult
import ch.admin.foitt.pilotwallet.platform.pinInput.domain.model.PinInputResult
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.TopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetTopBarState
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel
import ch.admin.foitt.pilotwallet.platform.utils.trackCompletion
import com.github.michaelbull.result.mapBoth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class PinLoginViewModel @Inject constructor(
    private val loginWithPin: LoginWithPin,
    private val handleDeeplink: HandleDeeplink,
    private val resetLockout: ResetLockout,
    private val getLockoutDuration: GetLockoutDuration,
    private val increaseFailedLoginCounter: IncreaseFailedLoginAttemptsCounter,
    setTopBarState: SetTopBarState,
) : ScreenViewModel(setTopBarState) {

    override val topBarState = TopBarState.RootNoSettings

    private val _pinCheckResult = MutableStateFlow<PinCheckResult>(PinCheckResult.Reset)
    val pinCheckResult = _pinCheckResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _countdown = MutableStateFlow("")
    val countdown = _countdown.asStateFlow()

    init {
        viewModelScope.launch {
            checkForLockout()
        }
    }

    fun onValidPin(pin: String) {
        viewModelScope.launch {
            _pinCheckResult.value = loginWithPin(pin = pin).mapBoth(
                success = { PinCheckResult.Success },
                failure = { error ->
                    when (error) {
                        is LoginError.InvalidPassphrase -> {
                            increaseFailedLoginCounter()
                            checkForLockout()
                        }

                        else -> Timber.e(error.toString(), "Login with PIN failed")
                    }
                    PinCheckResult.Error
                }
            )
        }
    }

    fun onPinInputResult(result: PinInputResult) {
        _pinCheckResult.value = PinCheckResult.Reset
        if (result is PinInputResult.Success && countdown.value.isBlank()) {
            viewModelScope.launch {
                handleDeeplink(fromOnboarding = false).navigate()
            }.trackCompletion(_isLoading)
        }
    }

    private fun checkForLockout() {
        val lockoutDuration = getLockoutDuration()
        if (lockoutDuration > Duration.ZERO) {
            startCountdown(timeout = lockoutDuration)
        }
    }

    private fun startCountdown(timeout: Duration) {
        val countDown = object : CountDownTimer(timeout.toMillis(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                val duration = Duration.ofMillis(millisUntilFinished)
                val minutes = duration.toMinutes()
                val seconds = duration.minusMinutes(minutes).seconds.toString().padStart(2, '0')
                _countdown.value = "$minutes:$seconds"
            }

            override fun onFinish() {
                resetLockout()
                _countdown.value = ""
            }
        }
        countDown.start()
    }
}
