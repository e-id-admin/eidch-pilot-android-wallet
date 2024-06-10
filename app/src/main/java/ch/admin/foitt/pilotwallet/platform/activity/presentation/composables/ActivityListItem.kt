package ch.admin.foitt.pilotwallet.platform.activity.presentation.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.preview.WalletComponentPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletIcons
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import ch.admin.foitt.pilotwallet.theme.textLabels
import ch.admin.foitt.pilotwallet.theme.warningLight

@Composable
fun ActivityCredentialReceived(
    modifier: Modifier = Modifier,
    issuerName: String,
    dateTimeString: String,
) = ActivityListItem(
    modifier = modifier,
    icon = R.drawable.pilot_ic_plus,
    iconTint = MaterialTheme.colorScheme.tertiary,
    title = issuerName,
    subtitle = R.string.activities_item_subtitle_credentialReceived,
    dateTimeString = dateTimeString,
    onClick = null,
)

@Composable
fun ActivityPresentationAccepted(
    modifier: Modifier = Modifier,
    verifierName: String,
    dateTimeString: String,
    onClick: (() -> Unit)? = null,
) = ActivityListItem(
    modifier = modifier,
    icon = R.drawable.pilot_ic_checkmark,
    iconTint = MaterialTheme.colorScheme.tertiary,
    title = verifierName,
    subtitle = R.string.activities_item_subtitle_presentationAccepted,
    dateTimeString = dateTimeString,
    onClick = onClick,
)

@Composable
fun ActivityPresentationDeclined(
    modifier: Modifier = Modifier,
    verifierName: String,
    dateTimeString: String,
    onClick: (() -> Unit)? = null,
) = ActivityListItem(
    modifier = modifier,
    icon = R.drawable.pilot_ic_cross_bb,
    iconTint = MaterialTheme.colorScheme.warningLight,
    title = verifierName,
    subtitle = R.string.activities_item_subtitle_presentationDeclined,
    dateTimeString = dateTimeString,
    onClick = onClick,
)

@Composable
fun ShowAllActivities(
    onClick: () -> Unit,
) = ActivityListItem(
    icon = R.drawable.pilot_ic_arrows,
    iconTint = MaterialTheme.colorScheme.primary,
    title = stringResource(id = R.string.credential_activities_footer_text),
    subtitle = null,
    dateTimeString = null,
    onClick = onClick,
)

@Composable
private fun ActivityListItem(
    @DrawableRes icon: Int,
    iconTint: Color,
    title: String,
    @StringRes subtitle: Int?,
    dateTimeString: String?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier
        .background(
            color = MaterialTheme.colorScheme.background
        )
        .run {
            onClick?.let { onClickMethod ->
                this.clickable { onClickMethod() }
            } ?: this
        }
        .padding(
            start = Sizes.s02,
            top = Sizes.s04,
            end = Sizes.s02,
            bottom = Sizes.s04,
        ),
    verticalAlignment = Alignment.CenterVertically,
) {
    WalletIcons.IconWithBackground(
        icon = painterResource(id = icon),
        iconTint = iconTint,
    )
    Spacer(Modifier.width(Sizes.s04))
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .weight(1f)
    ) {
        WalletTexts.ListLabelMedium(
            text = title,
        )
        subtitle?.let {
            WalletTexts.BodySmall(
                text = stringResource(id = subtitle),
                color = MaterialTheme.colorScheme.textLabels,
            )
        }
        dateTimeString?.let {
            WalletTexts.LabelSmall(
                text = dateTimeString,
            )
        }
    }
    onClick?.run {
        Spacer(Modifier.width(Sizes.s04))
        Icon(
            painter = painterResource(id = R.drawable.pilot_ic_chevron),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@WalletComponentPreview
@Composable
private fun ActivityListItemPreview() {
    PilotWalletTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Sizes.s02)
        ) {
            ActivityCredentialReceived(
                issuerName = "The issuer name",
                dateTimeString = "30. Jan. | 00:01",
            )
            ActivityPresentationAccepted(
                verifierName = "The verifier name",
                dateTimeString = "04. Sept. | 12:19",
                onClick = {},
            )
            ActivityPresentationDeclined(
                verifierName = "The verifier name 2",
                dateTimeString = "04. Sept. | 12:19",
                onClick = {},
            )
        }
    }
}
