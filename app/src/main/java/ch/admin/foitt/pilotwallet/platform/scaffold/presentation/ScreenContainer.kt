package ch.admin.foitt.pilotwallet.platform.scaffold.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScreenContainer(
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        topBar = { WalletTopBar() },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .consumeWindowInsets(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(paddingValues = paddingValues)
            ) {
                content()
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    )
    ErrorDialog()
}
