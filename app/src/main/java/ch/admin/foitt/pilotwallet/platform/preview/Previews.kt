package ch.admin.foitt.pilotwallet.platform.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

internal object PreviewConst {
    const val LightPreviewBackgroundColor: Long = 0xFFCCCCCC
    const val DarkPreviewBackgroundColor: Long = 0xFF333333

    const val SmallWidthDp: Int = 300
    const val SmallHeightDp: Int = 535

    const val MediumWidthDp: Int = 360
    const val MediumHeightDp: Int = 640

    const val LargeWidthDp: Int = 411
    const val LargeHeightDp: Int = 891
}

@Preview(
    name = "default dark",
    group = "default",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = PreviewConst.DarkPreviewBackgroundColor,
    widthDp = PreviewConst.MediumWidthDp,
    heightDp = PreviewConst.MediumHeightDp,
)
@Preview(
    name = "default light",
    group = "default",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    backgroundColor = PreviewConst.LightPreviewBackgroundColor,
    widthDp = PreviewConst.MediumWidthDp,
    heightDp = PreviewConst.MediumHeightDp,
)
internal annotation class WalletDefaultScreenPreview

@Preview(
    name = "small",
    group = "small",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    backgroundColor = PreviewConst.LightPreviewBackgroundColor,
    widthDp = PreviewConst.SmallWidthDp,
    heightDp = PreviewConst.SmallHeightDp,
)
internal annotation class WalletSmallScreenPreview

@Preview(
    name = "large",
    group = "large",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    backgroundColor = PreviewConst.LightPreviewBackgroundColor,
    widthDp = PreviewConst.LargeWidthDp,
    heightDp = PreviewConst.LargeHeightDp,
)
internal annotation class WalletLargeScreenPreview

@WalletDefaultScreenPreview
@WalletLargeScreenPreview
@WalletSmallScreenPreview
internal annotation class WalletAllScreenPreview

@Preview(
    name = "default light",
    group = "default",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    backgroundColor = PreviewConst.LightPreviewBackgroundColor,
)
@Preview(
    name = "default dark",
    group = "default",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = PreviewConst.DarkPreviewBackgroundColor,
)
internal annotation class WalletComponentPreview
