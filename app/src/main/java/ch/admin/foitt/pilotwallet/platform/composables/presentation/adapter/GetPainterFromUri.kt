package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import androidx.compose.ui.graphics.painter.Painter

fun interface GetPainterFromUri {
    suspend operator fun invoke(uriString: String?): Painter?
}
