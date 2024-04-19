package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetPainter
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

internal class GetPainterImpl @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val appContext: Context,
) : GetPainter {
    override suspend fun fromData(imageData: ByteArray) = get(imageData)
    override suspend fun fromUri(imageUri: Uri) = get(imageUri)

    private suspend fun get(imageParam: Any): Painter? {
        val imageRequest = ImageRequest.Builder(appContext)
            .data(imageParam)
            // TODO define placeholder during loading
            // TODO define fallback resource
            .build()
        val result = imageLoader.execute(imageRequest)
        val painter: BitmapPainter? = result.drawable?.let { drawable ->
            when (drawable) {
                is BitmapDrawable -> BitmapPainter(drawable.bitmap.asImageBitmap(), filterQuality = FilterQuality.High)
                else -> {
                    Timber.e("Unsupported drawable")
                    null
                }
            }
        }
        return painter
    }
}
