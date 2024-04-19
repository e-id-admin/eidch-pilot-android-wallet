package ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfigAES
import javax.inject.Inject

class PassphrasePepperKeyConfig @Inject constructor() : KeystoreKeyConfig by KeystoreKeyConfigAES(
    encryptionKeyAlias = "walletPassphrasePepperKey",
    userAuthenticationRequired = false,
    randomizedEncryptionRequired = false,
)
