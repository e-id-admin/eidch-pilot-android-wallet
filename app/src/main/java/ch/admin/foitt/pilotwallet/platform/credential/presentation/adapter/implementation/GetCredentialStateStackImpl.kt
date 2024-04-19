package ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.implementation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialCardState
import ch.admin.foitt.pilotwallet.platform.credential.presentation.adapter.GetCredentialStateStack
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import javax.inject.Inject

internal class GetCredentialStateStackImpl @Inject constructor(
    private val getCredentialCardState: GetCredentialCardState,
) : GetCredentialStateStack {
    override suspend fun invoke(credentialPreviews: List<CredentialPreview>): List<CredentialCardState> =
        credentialPreviews
            .sortedByDescending { credentialPreview -> credentialPreview.credentialId }
            .mapIndexed { index, credentialPreview ->
                val credentialState = getCredentialCardState(credentialPreview)
                credentialState.copy(backgroundColor = credentialState.backgroundColor.toColorInStack(index))
            }
            .asReversed()

    /**
     * Apply a darkening effect on the card background, depending on the [stackPosition], to differentiate between cards.
     *
     * The function takes the color value represented by this string, or the defaultCardColor if the color is invalid.
     * Then apply the darkening filter on top of it, depending on the [stackPosition],
     * which starts with strength 0f and increase by steps of 0.25f, until a max of 0.75f at [stackPosition] 3.
     */
    private fun Color.toColorInStack(
        stackPosition: Int,
    ): Color {
        val hsvVal = FloatArray(3)
        android.graphics.Color.colorToHSV(this.toArgb(), hsvVal)
        hsvVal[2] *= maxOf(1f - stackPosition * (1f / CredentialCardState.lightLevels), 0.25f)
        val newColor: Int = android.graphics.Color.HSVToColor(hsvVal)
        return Color(newColor)
    }
}
