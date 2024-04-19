package ch.admin.foitt.pilotwallet.feature.login.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(
    style = LoginNavAnimation::class,
)
fun LockScreen(
    viewModel: LockViewModel,
) {
    val currentActivity = LocalActivity.current
    DisposableEffect(viewModel) {
        currentActivity.lifecycle.addObserver(viewModel)
        onDispose {
            currentActivity.lifecycle.removeObserver(viewModel)
        }
    }

    BackHandler {
    }
    LockScreenContent()
}

@Composable
private fun LockScreenContent() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .size(Sizes.s24, Sizes.s24),
            painter = painterResource(id = R.drawable.ic_swiss_cross_small),
            contentDescription = "",
            contentScale = ContentScale.Fit,
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun LockScreenPreview() {
    PilotWalletTheme {
        LockScreenContent()
    }
}
