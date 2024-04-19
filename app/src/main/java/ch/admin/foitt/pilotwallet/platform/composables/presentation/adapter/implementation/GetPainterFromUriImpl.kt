package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation

import android.net.Uri
import androidx.compose.ui.graphics.painter.Painter
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainter
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainterFromUri
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import timber.log.Timber
import javax.inject.Inject

internal class GetPainterFromUriImpl @Inject constructor(
    private val getPainter: GetPainter,
) : GetPainterFromUri {
    override suspend fun invoke(uriString: String?): Painter? = runSuspendCatching {
        val imageUri = Uri.parse(uriString)
        imageUri?.let { getPainter.fromUri(it) }
    }.getOrElse {
        Timber.w(message = "Failed getting Painter from Url", t = it)
        null
    }
}
