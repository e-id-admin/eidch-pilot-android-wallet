package ch.admin.foitt.pilotwallet.platform.passphrase.domain.model

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfigAES
import javax.inject.Inject

class PassphraseStorageKeyConfig @Inject constructor() : KeystoreKeyConfig by KeystoreKeyConfigAES(
    encryptionKeyAlias = "walletPassphraseStorageKey",
    userAuthenticationRequired = true,
    randomizedEncryptionRequired = true,
)
