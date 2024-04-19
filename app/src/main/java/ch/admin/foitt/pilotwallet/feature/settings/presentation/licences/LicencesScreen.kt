package ch.admin.foitt.pilotwallet.feature.settings.presentation.licences

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.SpacerBottom
import ch.admin.foitt.pilotwallet.platform.composables.SpacerTop
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults.libraryColors
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun LicencesScreen(viewModel: LicencesViewModel) {
    LicencesScreenContent(
        onMoreInformation = viewModel::onMoreInformation
    )
}

@Composable
private fun LicencesScreenContent(
    onMoreInformation: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Sizes.s02)
            .background(MaterialTheme.colorScheme.background),
    ) {
        val (
            topSpacerRef,
            mainContentRef,
            bottomSpacerRef,
        ) = createRefs()

        SpacerTop(
            backgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.constrainAs(topSpacerRef) {
                top.linkTo(parent.top)
            },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(mainContentRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
        ) {
            Licences(
                onMoreInformation = onMoreInformation,
            )
        }
        SpacerBottom(
            backgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.constrainAs(bottomSpacerRef) {
                bottom.linkTo(parent.bottom)
            },
        )
    }
}

@Composable
private fun Licences(
    onMoreInformation: () -> Unit
) {
    LibrariesContainer(
        modifier = Modifier
            .fillMaxSize(),
        header = {
            item {
                Column(
                    modifier = Modifier.padding(top = Sizes.s05, start = Sizes.s04, end = Sizes.s04)
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = painterResource(id = R.drawable.pilot_ic_licences),
                        contentDescription = null,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Fit,
                    )
                    Spacer(modifier = Modifier.height(Sizes.s06))
                    WalletTexts.Body(text = stringResource(id = R.string.licences_text))
                    Spacer(modifier = Modifier.height(Sizes.s06))
                    WalletTexts.TextLink(
                        text = stringResource(id = R.string.licences_more_information_text),
                        rightIcon = painterResource(id = R.drawable.pilot_ic_link),
                        onClick = onMoreInformation,
                    )
                    Spacer(modifier = Modifier.height(Sizes.s06))
                }
            }
        },
        colors = libraryColors(
            badgeBackgroundColor = MaterialTheme.colorScheme.primary,
            badgeContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        itemContentPadding = PaddingValues(horizontal = Sizes.s04, vertical = Sizes.s04),
        showLicenseBadges = false,
        showAuthor = false,
    )
}

@WalletAllScreenPreview
@Composable
fun LicencesScreenPreview() {
    PilotWalletTheme {
        LicencesScreenContent(
            onMoreInformation = {}
        )
    }
}
