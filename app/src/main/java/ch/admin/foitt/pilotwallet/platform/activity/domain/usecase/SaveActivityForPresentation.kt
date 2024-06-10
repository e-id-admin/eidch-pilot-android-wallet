package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase

import android.graphics.drawable.Drawable
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityForPresentationError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import com.github.michaelbull.result.Result

interface SaveActivityForPresentation {
    suspend operator fun invoke(
        activityType: ActivityType,
        compatibleCredential: CompatibleCredential,
        credentialStatus: CredentialStatus,
        verifierLogo: Drawable?,
        verifierName: String,
    ): Result<Unit, SaveActivityForPresentationError>
}
