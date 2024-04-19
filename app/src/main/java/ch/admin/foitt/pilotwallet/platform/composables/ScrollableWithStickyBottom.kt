package ch.admin.foitt.pilotwallet.platform.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ch.admin.foitt.pilotwallet.theme.Sizes

@Composable
fun ScrollableWithStickyBottom(
    modifier: Modifier = Modifier,
    useStatusBarInsets: Boolean = true,
    useNavigationBarInsets: Boolean = false,
    stickyBottomPadding: PaddingValues = PaddingValues(
        start = Sizes.s04,
        end = Sizes.s04,
        bottom = Sizes.s04
    ),
    stickyBottomContent: @Composable ColumnScope.() -> Unit,
    contentPadding: PaddingValues = PaddingValues(
        start = Sizes.s06,
        end = Sizes.s06,
        top = Sizes.s16,
        bottom = Sizes.s06
    ),
    scrollableContent: @Composable ColumnScope.() -> Unit,
) = ScrollableWithStickyBottomContainer(
    modifier = modifier,
    stickyBottomPadding = stickyBottomPadding,
    stickyBottomContent = stickyBottomContent,
    useStatusBarInsets = useStatusBarInsets,
    useNavigationBarInsets = useNavigationBarInsets,
) {
    Column(
        modifier = Modifier.scrollingBehavior(
            useStatusBarInsets = useStatusBarInsets,
            contentPadding = contentPadding,
        ),
    ) {
        scrollableContent()
    }
}

@Composable
fun ScrollableBoxWithStickyBottom(
    modifier: Modifier = Modifier,
    useStatusBarInsets: Boolean = true,
    useNavigationBarInsets: Boolean = false,
    stickyBottomPadding: PaddingValues = PaddingValues(
        start = Sizes.s04,
        end = Sizes.s04,
        bottom = Sizes.s04
    ),
    stickyBottomContent: @Composable ColumnScope.() -> Unit,
    contentPadding: PaddingValues = PaddingValues(
        start = Sizes.s06,
        end = Sizes.s06,
        top = Sizes.s16,
        bottom = Sizes.s06
    ),
    scrollableContent: @Composable BoxScope.() -> Unit,
) = ScrollableWithStickyBottomContainer(
    modifier = modifier,
    stickyBottomPadding = stickyBottomPadding,
    stickyBottomContent = stickyBottomContent,
    useStatusBarInsets = useStatusBarInsets,
    useNavigationBarInsets = useNavigationBarInsets,
) {
    Box(
        modifier = Modifier.scrollingBehavior(
            useStatusBarInsets = useStatusBarInsets,
            contentPadding = contentPadding,
        ),
    ) {
        scrollableContent()
    }
}

@Composable
private fun ScrollableWithStickyBottomContainer(
    modifier: Modifier = Modifier,
    stickyBottomPadding: PaddingValues,
    stickyBottomContent: @Composable ColumnScope.() -> Unit,
    useStatusBarInsets: Boolean,
    useNavigationBarInsets: Boolean,
    content: @Composable BoxScope.() -> Unit,
) = ConstraintLayout(
    modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
) {
    val (
        topSpacerRef,
        mainContentRef,
        bottomSpacerRef,
        stickyBottomRef,
    ) = createRefs()

    SpacerTop(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.constrainAs(topSpacerRef) {
            top.linkTo(parent.top)
        },
        useStatusBarInsets = useStatusBarInsets,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .constrainAs(mainContentRef) {
                top.linkTo(parent.top)
                bottom.linkTo(stickyBottomRef.top)
                height = Dimension.fillToConstraints
            }
    ) {
        content()
    }
    SpacerBottom(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.constrainAs(bottomSpacerRef) {
            bottom.linkTo(stickyBottomRef.top)
        },
        useNavigationBarInsets = useNavigationBarInsets,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(stickyBottomPadding)
            .constrainAs(stickyBottomRef) {
                bottom.linkTo(parent.bottom)
            }
            .navigationBarsPadding(),
    ) {
        stickyBottomContent()
    }
}

private fun Modifier.scrollingBehavior(
    useStatusBarInsets: Boolean,
    contentPadding: PaddingValues,
) = composed {
    this
        .verticalScroll(rememberScrollState())
        .run {
            if (useStatusBarInsets) {
                this.statusBarsPadding()
            } else {
                this
            }
        }
        .padding(contentPadding)
}
