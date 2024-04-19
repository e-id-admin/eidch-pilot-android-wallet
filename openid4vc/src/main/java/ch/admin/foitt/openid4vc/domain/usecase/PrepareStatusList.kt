package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.vcStatus.PrepareStatusListError
import com.github.michaelbull.result.Result
import java.net.URL

interface PrepareStatusList {
    suspend operator fun invoke(url: URL): Result<ByteArray, PrepareStatusListError>
}
