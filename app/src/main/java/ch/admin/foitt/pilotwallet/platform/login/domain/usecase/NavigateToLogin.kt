package ch.admin.foitt.pilotwallet.platform.login.domain.usecase

import com.ramcosta.composedestinations.spec.Direction

fun interface NavigateToLogin {
    suspend operator fun invoke(): Direction
}
