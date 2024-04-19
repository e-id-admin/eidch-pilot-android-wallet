package ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.implementation

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetOrCreateSecretKeyError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetOrCreateSecretKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject

internal class GetOrCreateSecretKeyImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : GetOrCreateSecretKey {
    @CheckResult
    override fun invoke(keystoreKeyConfig: KeystoreKeyConfig): Result<SecretKey, GetOrCreateSecretKeyError> = runSuspendCatching {
        // First check if Secretkey was previously created for that keyName, then get and return it.
        val keyStore = KeyStore.getInstance(keystoreKeyConfig.keystoreName)
        keyStore.load(null)
        keyStore.getKey(keystoreKeyConfig.encryptionKeyAlias, null)?.let {
            return@runSuspendCatching it as SecretKey
        }

        // Key not found, we create it
        createSecretKey(keystoreKeyConfig)
    }.mapError { throwable ->
        GetOrCreateSecretKeyError.Unexpected(throwable)
    }

    private fun createSecretKey(keystoreKeyConfig: KeystoreKeyConfig): SecretKey {
        val secretKeyParams = KeyGenParameterSpec.Builder(
            keystoreKeyConfig.encryptionKeyAlias,
            keystoreKeyConfig.encryptionKeyPurpose,
        ).apply {
            setBlockModes(keystoreKeyConfig.encryptionBlockMode)
            setEncryptionPaddings(keystoreKeyConfig.encryptionPaddings)
            setKeySize(keystoreKeyConfig.encryptionKeySize)

            if (keystoreKeyConfig.userAuthenticationRequired) {
                linkToUserAuthentication(keystoreKeyConfig)
            }

            // Flag that prevent providing IV for encryption. Default to true.
            setRandomizedEncryptionRequired(keystoreKeyConfig.randomizedEncryptionRequired)

            if (isStrongBoxAvailable()) {
                setIsStrongBoxBacked(true)
            } else {
                Timber.w("Strongbox unavailable on device")
            }
        }.build()

        val keyGenerator = KeyGenerator.getInstance(
            keystoreKeyConfig.encryptionAlgorithm,
            keystoreKeyConfig.keystoreName
        )
        keyGenerator.init(secretKeyParams)
        return keyGenerator.generateKey()
    }

    private fun isStrongBoxAvailable(): Boolean {
        return appContext.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }

    private fun KeyGenParameterSpec.Builder.linkToUserAuthentication(keystoreKeyConfig: KeystoreKeyConfig) {
        setUserAuthenticationRequired(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setUserAuthenticationParameters(
                0,
                keystoreKeyConfig.allowedKeyStoreAuthenticators
            )
        } else {
            // Fallback for older android keystore versions.
            @Suppress("DEPRECATION")
            setUserAuthenticationValidityDurationSeconds(-1)
        }

        setInvalidatedByBiometricEnrollment(true)
    }
}
