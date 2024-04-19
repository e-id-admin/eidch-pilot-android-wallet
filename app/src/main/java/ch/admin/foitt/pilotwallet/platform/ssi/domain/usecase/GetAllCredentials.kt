package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetAllCredentialsError
import com.github.michaelbull.result.Result

interface GetAllCredentials {
    suspend operator fun invoke(): Result<List<Credential>, GetAllCredentialsError>
}
