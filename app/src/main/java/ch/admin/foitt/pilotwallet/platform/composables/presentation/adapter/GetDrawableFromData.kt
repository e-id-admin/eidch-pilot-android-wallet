package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import android.graphics.drawable.Drawable

fun interface GetDrawableFromData {
    suspend operator fun invoke(base64ImageString: String?): Drawable?
}
