package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation

import android.graphics.drawable.Drawable
import android.net.Uri
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawable
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromUri
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import timber.log.Timber
import javax.inject.Inject

internal class GetDrawableFromUriImpl @Inject constructor(
    private val getDrawable: GetDrawable
) : GetDrawableFromUri {
    override suspend fun invoke(uriString: String?): Drawable? = runSuspendCatching {
        val imageUri = Uri.parse(uriString)
        imageUri?.let {
            getDrawable.fromUri(it)
        }
    }.getOrElse {
        Timber.w(message = "Failed getting Drawable from Url", t = it)
        null
    }
}
