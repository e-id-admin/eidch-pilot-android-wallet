package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetAllCredentialBodiesError
import com.github.michaelbull.result.Result

fun interface GetAllCredentialBodies {
    @CheckResult
    suspend operator fun invoke(): Result<List<CredentialBody>, GetAllCredentialBodiesError>
}
