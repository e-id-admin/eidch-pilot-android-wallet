package ch.admin.foitt.openid4vc.domain.usecase

import ch.admin.foitt.openid4vc.domain.model.vcStatus.HandleStatusList2021EntryError
import com.github.michaelbull.result.Result
import java.net.URL

interface HandleStatusList2021Entry {
    suspend operator fun invoke(url: URL, index: Int): Result<Boolean, HandleStatusList2021EntryError>
}
