package ch.admin.foitt.openid4vc.domain.model

interface KeyPairError {
    data object NotFound : GetKeyPairError
    data object DeleteFailed : DeleteKeyPairError
    data class Unexpected(val throwable: Throwable) : CreateES512KeyPairError, GetKeyPairError, DeleteKeyPairError
}

sealed interface CreateES512KeyPairError
sealed interface GetKeyPairError

sealed interface DeleteKeyPairError
