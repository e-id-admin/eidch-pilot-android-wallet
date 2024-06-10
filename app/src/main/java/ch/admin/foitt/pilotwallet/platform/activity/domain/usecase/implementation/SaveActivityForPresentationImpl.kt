package ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.implementation

import android.graphics.drawable.Drawable
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityForPresentationError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityVerifierCredentialClaimSnapshotError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.SaveActivityVerifierError
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.toSaveActivityForPresentationError
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivity
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityForPresentation
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityVerifier
import ch.admin.foitt.pilotwallet.platform.activity.domain.usecase.SaveActivityVerifierCredentialClaimsSnapshot
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.utils.toBase64EncodedBitmap
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import javax.inject.Inject

class SaveActivityForPresentationImpl @Inject constructor(
    private val saveActivity: SaveActivity,
    private val saveActivityVerifier: SaveActivityVerifier,
    private val saveActivityVerifierCredentialClaimsSnapshot: SaveActivityVerifierCredentialClaimsSnapshot,
) : SaveActivityForPresentation {
    override suspend fun invoke(
        activityType: ActivityType,
        compatibleCredential: CompatibleCredential,
        credentialStatus: CredentialStatus,
        verifierLogo: Drawable?,
        verifierName: String
    ): Result<Unit, SaveActivityForPresentationError> = coroutineBinding {
        val activityToSave = Activity(
            credentialId = compatibleCredential.credentialId,
            type = activityType,
            credentialSnapshotStatus = credentialStatus
        )
        val activityId = saveActivity(activityToSave).mapError(SaveActivityError::toSaveActivityForPresentationError).bind()
        val verifierImage = verifierLogo?.toBase64EncodedBitmap()?.mapError(::toSaveActivityForPresentationError)?.bind()
        val activityVerifierToSave = ActivityVerifier(
            activityId = activityId,
            name = verifierName,
            logo = verifierImage
        )

        val activityVerifierId = saveActivityVerifier(
            activityVerifierToSave
        ).mapError(SaveActivityVerifierError::toSaveActivityForPresentationError).bind()

        when (activityType) {
            ActivityType.CREDENTIAL_RECEIVED -> {}
            ActivityType.PRESENTATION_DECLINED,
            ActivityType.PRESENTATION_ACCEPTED -> {
                saveActivityVerifierCredentialClaimsSnapshot(
                    compatibleCredential,
                    activityVerifierId
                ).mapError(SaveActivityVerifierCredentialClaimSnapshotError::toSaveActivityForPresentationError).bind()
            }
        }
    }

    private fun toSaveActivityForPresentationError(throwable: Throwable): SaveActivityForPresentationError = ActivityError.Unexpected(
        throwable
    )
}
