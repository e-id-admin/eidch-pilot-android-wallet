package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import androidx.compose.ui.graphics.painter.Painter

fun interface GetPainterFromData {
    suspend operator fun invoke(base64ImageString: String?): Painter?
}
