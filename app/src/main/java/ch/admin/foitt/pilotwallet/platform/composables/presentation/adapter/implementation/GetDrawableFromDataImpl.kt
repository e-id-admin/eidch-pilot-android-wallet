package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.implementation

import android.graphics.drawable.Drawable
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawable
import ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter.GetDrawableFromData
import ch.admin.foitt.pilotwallet.platform.utils.base64NonUrlStringToByteArray
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOrElse
import timber.log.Timber
import javax.inject.Inject

internal class GetDrawableFromDataImpl @Inject constructor(
    private val getDrawable: GetDrawable
) : GetDrawableFromData {
    override suspend fun invoke(base64ImageString: String?): Drawable? = runSuspendCatching {
        val imageByteArray = base64ImageString?.base64NonUrlStringToByteArray()
        imageByteArray?.let {
            getDrawable.fromData(it)
        }
    }.getOrElse {
        Timber.w(message = "Failed getting Drawable from Data", t = it)
        null
    }
}
