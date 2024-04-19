package ch.admin.foitt.pilotwallet.platform.byteArrayRepository.domain.repository

interface ByteArrayRepository {
    suspend fun get(): ByteArray
    suspend fun save(data: ByteArray)
}
