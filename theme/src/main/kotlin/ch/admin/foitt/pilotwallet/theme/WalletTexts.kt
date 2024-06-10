package ch.admin.foitt.pilotwallet.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("detekt:TooManyFunctions")
object WalletTexts {

    @Composable
    fun Button(
        text: String,
        enabled: Boolean,
        modifier: Modifier = Modifier,
    ) {
        val textStyle = if (enabled) {
            MaterialTheme.typography.labelLarge
        } else {
            MaterialTheme.typography.labelLarge.copy(textDecoration = TextDecoration.LineThrough)
        }

        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = modifier,
        )
    }

    @Composable
    fun Headline(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = Colors.grey07,
        style = MaterialTheme.typography.headlineLarge,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun CredentialTitle(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onPrimaryContainer
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.headlineSmall,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier,
    )

    @Composable
    fun CredentialSubtitle(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        maxLines = 1,
    )

    @Composable
    fun CredentialStatus(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSecondary,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.End,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )

    @Composable
    fun TitleScreen(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun TitleScreenMultiLine(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        minLines = 2,
        maxLines = 2,
        modifier = modifier,
    )

    @Composable
    fun TitleScreenCentered(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onBackground
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun TitleTopBar(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun TitleMedium(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun TitleSmall(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun BodyLarge(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun Body(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onBackground,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun BodyCentered(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun BodySmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onBackground,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun BodySmallCentered(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onBackground,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun LabelMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.textLabels,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun LabelMedium(
        text: AnnotatedString,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.textLabels,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun LabelSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.textLabels,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun LabelSmallBoldUnderlined(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.textLabels,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        ),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun ListLabelMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onBackground,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontWeight = FontWeight.Medium,
        modifier = modifier,
    )

    @Composable
    fun ErrorLabel(
        text: String,
        modifier: Modifier = Modifier,
    ) = Text(
        text = text,
        color = MaterialTheme.colorScheme.onErrorContainer,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )

    @Composable
    fun InfoLabel(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSecondary,
    ) = Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        lineHeight = 18.sp,
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )

    @Composable
    fun TextLink(
        text: String,
        onClick: () -> Unit,
        rightIcon: Painter? = null,
    ) {
        val color = MaterialTheme.colorScheme.primary
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .drawBehind {
                    val strokeWidthPx = 1.dp.toPx()
                    val verticalOffset = size.height
                    drawLine(
                        color = color,
                        strokeWidth = strokeWidthPx,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset)
                    )
                }
        ) {
            val annotatedString = buildAnnotatedString {
                append(text)
            }
            Text(
                text = annotatedString,
                color = color,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
            rightIcon?.let {
                Spacer(modifier = Modifier.width(Sizes.s02))
                Icon(
                    painter = rightIcon,
                    tint = color,
                    contentDescription = null,
                )
            }
        }
    }
}
