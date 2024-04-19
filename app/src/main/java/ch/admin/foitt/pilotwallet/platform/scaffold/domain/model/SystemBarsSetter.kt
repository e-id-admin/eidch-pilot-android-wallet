package ch.admin.foitt.pilotwallet.platform.scaffold.domain.model

import androidx.activity.SystemBarStyle

fun interface SystemBarsSetter {
    operator fun invoke(
        statusBarStyle: SystemBarStyle,
        navigationBarStyle: SystemBarStyle,
    )
}
