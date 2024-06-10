package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class GetDrawableImpl @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val appContext: Context,
) : GetDrawable {
    override suspend fun fromData(imageData: ByteArray) = get(imageData)
    override suspend fun fromUri(imageUri: Uri) = get(imageUri)

    private suspend fun get(imageParam: Any): Drawable? {
        val imageRequest = ImageRequest.Builder(appContext)
            .data(imageParam)
            // TODO define placeholder during loading
            // TODO define fallback resource
            .build()
        val result = imageLoader.execute(imageRequest)

        return result.drawable
    }
}
