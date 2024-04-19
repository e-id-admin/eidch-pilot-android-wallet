package ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.PermissionState

fun interface CheckQrScanPermission {
    @CheckResult
    suspend operator fun invoke(
        permissionsAreGranted: Boolean,
        rationaleShouldBeShown: Boolean,
        promptWasTriggered: Boolean,
    ): PermissionState
}
