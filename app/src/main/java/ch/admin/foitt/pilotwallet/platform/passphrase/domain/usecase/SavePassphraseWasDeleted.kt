package ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase

fun interface SavePassphraseWasDeleted {
    suspend operator fun invoke(passphraseWasDeleted: Boolean)
}
