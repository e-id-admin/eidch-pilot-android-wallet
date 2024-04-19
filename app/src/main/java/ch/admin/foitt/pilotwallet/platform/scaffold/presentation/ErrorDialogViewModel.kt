package ch.admin.foitt.pilotwallet.platform.scaffold.presentation

import androidx.lifecycle.ViewModel
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.model.ErrorDialogState
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.repository.ErrorDialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ErrorDialogViewModel @Inject constructor(
    private val errorDialogRepository: ErrorDialogRepository,
) : ViewModel() {

    val state get() = errorDialogRepository.state

    fun setState(dialogState: ErrorDialogState) {
        errorDialogRepository.setState(dialogState)
    }

    fun onDismiss() {
        errorDialogRepository.setState(ErrorDialogState.Closed)
    }
}
