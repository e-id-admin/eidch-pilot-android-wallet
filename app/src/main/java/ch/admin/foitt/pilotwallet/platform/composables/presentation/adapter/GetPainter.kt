package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import android.net.Uri
import androidx.compose.ui.graphics.painter.Painter

internal interface GetPainter {
    suspend fun fromData(imageData: ByteArray): Painter?
    suspend fun fromUri(imageUri: Uri): Painter?
}
