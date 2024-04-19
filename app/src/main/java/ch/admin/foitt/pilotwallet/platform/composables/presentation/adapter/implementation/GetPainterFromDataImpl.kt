package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation

import androidx.compose.ui.graphics.painter.Painter
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainter
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainterFromData
import ch.admin.foitt.pilotwallet.platform.utils.base64NonUrlStringToByteArray
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import timber.log.Timber
import javax.inject.Inject

internal class GetPainterFromDataImpl @Inject constructor(
    private val getPainter: GetPainter,
) : GetPainterFromData {
    override suspend fun invoke(base64ImageString: String?): Painter? = runSuspendCatching {
        val imageByteArray = base64ImageString?.base64NonUrlStringToByteArray()
        imageByteArray?.let { getPainter.fromData(it) }
    }.getOrElse {
        Timber.w(message = "Failed getting Painter from Data", t = it)
        null
    }
}
