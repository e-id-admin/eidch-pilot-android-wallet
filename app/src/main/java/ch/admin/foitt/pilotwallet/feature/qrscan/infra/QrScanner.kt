package ch.admin.foitt.pilotwallet.feature.qrscan.infra

import androidx.annotation.CheckResult
import androidx.camera.view.PreviewView
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.FlashLightState
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.StateFlow

interface QrScanner {

    fun initAnalyser(onBarcodesScanned: (List<String>) -> Unit)

    @CheckResult
    fun registerScanner(previewView: PreviewView): Result<Unit, Throwable>

    fun unRegisterScanner()

    fun resumeScanner()

    fun pauseScanner()

    val flashLightState: StateFlow<FlashLightState>

    suspend fun toggleFlashLight()
}
