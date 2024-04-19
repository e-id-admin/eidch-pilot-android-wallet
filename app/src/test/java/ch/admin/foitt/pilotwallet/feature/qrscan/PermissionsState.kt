package ch.admin.foitt.pilotwallet.feature.qrscan

data class PermissionsState(
    val permissionWasTriggeredOnce: Boolean,
    val resultPermissionGranted: Boolean,
    val resultRationaleShouldBeShown: Boolean,
    val resultPromptWasTriggered: Boolean,
)
