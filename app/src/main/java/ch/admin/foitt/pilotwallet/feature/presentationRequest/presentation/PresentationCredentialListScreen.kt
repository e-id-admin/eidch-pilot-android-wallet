package ch.admin.foitt.pilotwallet.feature.presentationRequest.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.InvitationHeader
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.SpacerBottom
import ch.admin.foitt.pilotwallet.platform.composables.SpacerTop
import ch.admin.foitt.pilotwallet.platform.credential.presentation.CredentialStatusLabel
import ch.admin.foitt.pilotwallet.platform.credential.presentation.mock.CredentialMocks
import ch.admin.foitt.pilotwallet.platform.credential.presentation.model.CredentialCardState
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.PresentationCredentialListNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.Gradients
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts
import com.ramcosta.composedestinations.annotation.Destination

@Destination(
    navArgsDelegate = PresentationCredentialListNavArg::class,
)
@Composable
fun PresentationCredentialListScreen(viewModel: PresentationCredentialListViewModel) {
    PresentationCredentialListScreenContent(
        verifierName = viewModel.verifierName,
        verifierImage = viewModel.verifierLogo.collectAsStateWithLifecycle().value,
        credentialStates = viewModel.credentialStates.collectAsStateWithLifecycle().value,
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        onCredentialSelected = viewModel::onCredentialSelected,
        onBack = viewModel::onBack,
    )
}

@Composable
private fun PresentationCredentialListScreenContent(
    verifierName: String?,
    verifierImage: Painter?,
    credentialStates: List<CredentialCardState>,
    isLoading: Boolean,
    onCredentialSelected: (Int) -> Unit,
    onBack: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (
            topSpacerRef,
            mainContentRef,
            bottomSpacerRef,
            buttonRef,
        ) = createRefs()

        SpacerTop(
            backgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.constrainAs(topSpacerRef) {
                top.linkTo(parent.top)
            },
            useStatusBarInsets = true,
        )
        CompactCredentialList(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = Sizes.s04, end = Sizes.s04)
                .constrainAs(mainContentRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(buttonRef.top)
                    height = Dimension.fillToConstraints
                },
            contentPadding = PaddingValues(top = Sizes.s04, bottom = Sizes.s04), // add Spacers padding
            credentialStates = credentialStates,
            onCredentialSelected = onCredentialSelected,
            headerContent = {
                ListHeader(
                    verifierName = verifierName,
                    verifierImage = verifierImage,
                )
            },
        )
        ButtonOutlined(
            modifier = Modifier
                .padding(start = Sizes.s04, end = Sizes.s04, bottom = Sizes.s04)
                .navigationBarsPadding()
                .constrainAs(buttonRef) {
                    bottom.linkTo(parent.bottom)
                },
            text = stringResource(id = R.string.global_back_home),
            leftIcon = painterResource(id = R.drawable.pilot_ic_back_button),
            onClick = onBack,
        )
        SpacerBottom(
            backgroundColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.constrainAs(bottomSpacerRef) {
                bottom.linkTo(buttonRef.top)
            },
            useNavigationBarInsets = false,
        )
        LoadingOverlay(showOverlay = isLoading)
    }
}

@Composable
private fun ListHeader(verifierName: String?, verifierImage: Painter?) {
    InvitationHeader(
        inviterName = verifierName ?: stringResource(id = R.string.presentation_verifier_name_unknown),
        inviterImage = verifierImage,
        message = stringResource(id = R.string.presentation_verifier_text)
    )
    WalletTexts.TitleSmall(text = stringResource(id = R.string.presentation_select_credential_title))
    Spacer(modifier = Modifier.height(Sizes.s02))
    WalletTexts.Body(text = stringResource(id = R.string.presentation_select_credential_subtitle))
    Spacer(modifier = Modifier.height(Sizes.s04))
}

@Composable
private fun CompactCredentialList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    credentialStates: List<CredentialCardState>,
    onCredentialSelected: (Int) -> Unit,
    headerContent: @Composable () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        item {
            headerContent()
        }
        itemsIndexed(credentialStates) { index, state ->
            CompactCredential(
                modifier = Modifier.clickable { onCredentialSelected(index) },
                credentialState = state,
                showDivider = index != credentialStates.lastIndex,
            )
        }
    }
}

@Composable
private fun CompactCredential(
    modifier: Modifier = Modifier,
    credentialState: CredentialCardState,
    showDivider: Boolean,
) {
    Row(
        modifier = modifier
            .padding(start = Sizes.s02, top = Sizes.s04, end = Sizes.s02, bottom = Sizes.s04),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CredentialThumbnail(credentialState = credentialState)
        Spacer(modifier = Modifier.width(Sizes.s04))
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            WalletTexts.BodySmall(text = credentialState.title)
            credentialState.subtitle?.let {
                WalletTexts.LabelSmall(
                    text = credentialState.subtitle,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            credentialState.status?.let {
                CredentialStatusLabel(
                    status = credentialState.status,
                    validColor = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        Spacer(modifier = Modifier.width(Sizes.s04))
        Icon(
            painter = painterResource(id = R.drawable.pilot_ic_chevron),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
    if (showDivider) {
        HorizontalDivider()
    }
}

@Composable
private fun CredentialThumbnail(credentialState: CredentialCardState) {
    Surface(
        modifier = Modifier
            .width(Sizes.credentialThumbnailWidth)
            .height(Sizes.credentialThumbnailHeight),
        shape = RoundedCornerShape(size = Sizes.s01),
        color = credentialState.backgroundColor,
        contentColor = Color.Unspecified,
        border = BorderStroke(
            width = Sizes.line01,
            color = credentialState.borderColor,
        ),
        shadowElevation = Sizes.s01,
    ) {
        credentialState.logo?.let { logo ->
            Box(
                modifier = Modifier
                    .background(brush = Gradients.credentialThumbnailBrush())
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = logo,
                    contentDescription = null,
                    modifier = Modifier.size(Sizes.credentialThumbnailIconSize),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@WalletAllScreenPreview
@Composable
private fun PresentationCredentialListScreenPreview() {
    PilotWalletTheme {
        PresentationCredentialListScreenContent(
            verifierName = "My Verifier Name",
            verifierImage = painterResource(id = R.drawable.pilot_ic_strassenverkehrsamt),
            credentialStates = CredentialMocks.cardStates.toList().map { it.value() },
            isLoading = false,
            onCredentialSelected = {},
            onBack = {},
        )
    }
}
