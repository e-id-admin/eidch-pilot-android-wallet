package ch.admin.foitt.pilotwallet.platform.activity.presentation.model

sealed interface ActivityListItem {
    val id: Long
    val dateTimeString: String
    data class PresentationDeclined(
        override val id: Long,
        override val dateTimeString: String,
        val verifier: String,
    ) : ActivityListItem
    data class PresentationAccepted(
        override val id: Long,
        override val dateTimeString: String,
        val verifier: String,
    ) : ActivityListItem
    data class CredentialReceived(
        override val id: Long,
        override val dateTimeString: String,
        val issuer: String,
    ) : ActivityListItem
}
