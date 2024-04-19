package ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase

fun interface GetPassphraseWasDeleted {
    suspend operator fun invoke(): Boolean
}
