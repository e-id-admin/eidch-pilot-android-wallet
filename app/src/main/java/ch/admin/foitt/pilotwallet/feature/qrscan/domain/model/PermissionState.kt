package ch.admin.foitt.pilotwallet.feature.qrscan.domain.model

sealed interface PermissionState {
    data object Initial : PermissionState
    data object Granted : PermissionState
    data object Blocked : PermissionState
    data object Intro : PermissionState
    data object Rationale : PermissionState
}
