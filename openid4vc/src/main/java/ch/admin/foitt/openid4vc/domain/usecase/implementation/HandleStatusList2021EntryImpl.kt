package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.vcStatus.HandleStatusList2021EntryError
import ch.admin.foitt.openid4vc.domain.model.vcStatus.toHandleStatusList2021EntryError
import ch.admin.foitt.openid4vc.domain.usecase.HandleStatusList2021Entry
import ch.admin.foitt.openid4vc.domain.usecase.PrepareStatusList
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapError
import java.net.URL
import javax.inject.Inject
import kotlin.experimental.and

class HandleStatusList2021EntryImpl @Inject constructor(
    private val prepareStatusList: PrepareStatusList
) : HandleStatusList2021Entry {
    override suspend fun invoke(url: URL, index: Int): Result<Boolean, HandleStatusList2021EntryError> = coroutineBinding {
        // check https://www.w3.org/TR/vc-bitstring-status-list/ for an explanation about the algorithm
        val statusList = prepareStatusList(url).mapError { error ->
            error.toHandleStatusList2021EntryError()
        }.bind()

        val byteIndex = index / 8
        val byte = statusList[byteIndex]
        val bitIndex = index % 8
        val bitStatus = byte and ((1 shl (7 - bitIndex)).toByte()) // 0 = valid, !0 = revoked/suspended

        bitStatus.toInt() != 0
    }
}
