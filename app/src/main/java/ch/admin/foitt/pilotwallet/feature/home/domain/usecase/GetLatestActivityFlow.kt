package ch.admin.foitt.pilotwallet.feature.home.domain.usecase

import ch.admin.foitt.pilotwallet.feature.home.domain.model.GetLatestActivityFlowError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityWithVerifier
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface GetLatestActivityFlow {
    operator fun invoke(): Flow<Result<ActivityWithVerifier?, GetLatestActivityFlowError>>
}
