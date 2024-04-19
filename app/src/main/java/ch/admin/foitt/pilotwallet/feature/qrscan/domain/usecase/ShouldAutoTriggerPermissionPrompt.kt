package ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase

import androidx.annotation.CheckResult

fun interface ShouldAutoTriggerPermissionPrompt {
    @CheckResult
    suspend operator fun invoke(): Boolean
}
