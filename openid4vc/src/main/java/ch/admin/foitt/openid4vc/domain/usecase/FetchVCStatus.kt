package ch.admin.foitt.openid4vc.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.vcStatus.FetchVCStatusError
import com.github.michaelbull.result.Result
import java.net.URL

interface FetchVCStatus {
    @CheckResult
    suspend operator fun invoke(statusListType: String, url: URL, index: Int): Result<Boolean, FetchVCStatusError>
}
