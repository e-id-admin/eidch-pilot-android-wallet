package ch.admin.foitt.pilotwallet.feature.sessionTimeout.domain

import com.ramcosta.composedestinations.spec.Direction

interface SessionTimeoutNavigation {
    suspend operator fun invoke(): Direction?
}
