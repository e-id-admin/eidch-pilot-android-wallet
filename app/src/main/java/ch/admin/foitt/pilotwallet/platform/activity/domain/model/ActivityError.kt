package ch.admin.foitt.pilotwallet.platform.activity.domain.model

sealed interface ActivityError {
    data class Unexpected(val throwable: Throwable?) :
        ActivityRepositoryError,
        SaveActivityForPresentationError,
        SaveActivityError,
        SaveActivityVerifierError,
        SaveActivityVerifierCredentialClaimSnapshotError,
        DeleteActivityError
}

sealed interface ActivityRepositoryError
sealed interface SaveActivityForPresentationError
sealed interface SaveActivityError
sealed interface SaveActivityVerifierError
sealed interface SaveActivityVerifierCredentialClaimSnapshotError
sealed interface DeleteActivityError

fun ActivityRepositoryError.toSaveActivityError(): SaveActivityError = when (this) {
    is ActivityError.Unexpected -> this
}

fun ActivityRepositoryError.toSaveActivityVerifierError(): SaveActivityVerifierError = when (this) {
    is ActivityError.Unexpected -> this
}

fun ActivityRepositoryError.toSaveActivityVerifierCredentialClaimSnapshotError(): SaveActivityVerifierCredentialClaimSnapshotError =
    when (this) {
        is ActivityError.Unexpected -> this
    }

fun SaveActivityError.toSaveActivityForPresentationError(): SaveActivityForPresentationError = when (this) {
    is ActivityError.Unexpected -> this
}

fun SaveActivityVerifierError.toSaveActivityForPresentationError(): SaveActivityForPresentationError = when (this) {
    is ActivityError.Unexpected -> this
}

fun SaveActivityVerifierCredentialClaimSnapshotError.toSaveActivityForPresentationError(): SaveActivityForPresentationError = when (this) {
    is ActivityError.Unexpected -> this
}

fun ActivityRepositoryError.toDeleteActivityError(): DeleteActivityError = when (this) {
    is ActivityError.Unexpected -> this
}
