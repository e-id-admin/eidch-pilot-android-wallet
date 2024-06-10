@file:OptIn(ExperimentalMaterial3Api::class)

package ch.admin.foitt.pilotwallet.feature.credentialActivities.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
fun SwipeToDeleteContainer(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        positionalThreshold = { _ ->
            200.0f
        },
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        modifier = modifier.animateContentSize(),
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            BackgroundContent(
                dismissState = dismissState
            )
        }
    ) {
        content()
    }
}

@Composable
private fun BackgroundContent(
    dismissState: SwipeToDismissBoxState,
) {
    val backgroundColor by getBackgroundColor(dismissState = dismissState)
    val iconTint by getIconTint(dismissState = dismissState)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(end = Sizes.s04),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.pilot_ic_bin_bb),
                contentDescription = null,
                tint = iconTint,
            )
            WalletTexts.LabelSmall(
                text = stringResource(id = R.string.activities_item_delete_button),
                color = iconTint,
            )
        }
    }
}

@Composable
private fun getBackgroundColor(
    dismissState: SwipeToDismissBoxState,
) = animateColorAsState(
    targetValue = when (dismissState.targetValue) {
        SwipeToDismissBoxValue.Settled -> Color.LightGray
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    },
    label = "BackgroundColorAnimation",
)

@Composable
private fun getIconTint(
    dismissState: SwipeToDismissBoxState,
) = animateColorAsState(
    targetValue = when (dismissState.targetValue) {
        SwipeToDismissBoxValue.Settled -> Color.Black
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onError
        else -> Color.Transparent
    },
    label = "BackgroundIconTintAnimation",
)
