package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.vcStatus.FetchVCStatusError
import ch.admin.foitt.openid4vc.domain.model.vcStatus.toFetchVCStatusError
import ch.admin.foitt.openid4vc.domain.usecase.FetchVCStatus
import ch.admin.foitt.openid4vc.domain.usecase.HandleStatusList2021Entry
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import java.net.URL
import javax.inject.Inject

class FetchVCStatusImpl @Inject constructor(
    private val handleStatusList2021Entry: HandleStatusList2021Entry,
) : FetchVCStatus {
    override suspend fun invoke(statusListType: String, url: URL, index: Int): Result<Boolean, FetchVCStatusError> = coroutineBinding {
        when (statusListType) {
            statusList2021Entry -> {
                handleStatusList2021Entry(url, index)
                    .mapError { error ->
                        error.toFetchVCStatusError()
                    }.bind()
            }
            else -> Err(FetchVCStatusError.UnsupportedStatusListFormat).bind<Boolean>()
        }
    }

    companion object {
        private const val statusList2021Entry = "StatusList2021Entry"
    }
}
