package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import android.graphics.drawable.Drawable
import android.net.Uri

interface GetDrawable {
    suspend fun fromData(imageData: ByteArray): Drawable?
    suspend fun fromUri(imageUri: Uri): Drawable?
}
