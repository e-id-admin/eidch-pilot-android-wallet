package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import androidx.compose.ui.graphics.Color

fun interface GetColor {
    operator fun invoke(colorString: String?): Color?
}
