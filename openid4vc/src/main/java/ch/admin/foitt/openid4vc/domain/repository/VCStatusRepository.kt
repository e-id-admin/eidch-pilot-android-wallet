package ch.admin.foitt.openid4vc.domain.repository

import androidx.annotation.CheckResult
import ch.admin.foitt.openid4vc.domain.model.vcStatus.VCStatusError
import com.github.michaelbull.result.Result
import java.net.URL

interface VCStatusRepository {
    @CheckResult
    suspend fun fetchVCStatus(url: URL): Result<String, VCStatusError>
}
