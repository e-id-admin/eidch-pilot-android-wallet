package ch.admin.foitt.pilotwallet.platform.database.domain.model

interface Claim {
    val key: String
    val value: String
    val valueType: String?
}
