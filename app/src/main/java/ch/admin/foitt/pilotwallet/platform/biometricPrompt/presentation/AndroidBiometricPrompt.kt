package ch.admin.foitt.pilotwallet.platform.biometricPrompt.presentation

import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptType
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.crypto.Cipher
import kotlin.coroutines.resume

internal class AndroidBiometricPrompt(
    private val activity: FragmentActivity,
    private val allowedAuthenticators: Int,
    private val promptType: BiometricPromptType,
) : BiometricPromptWrapper {
    override suspend fun launchPrompt(
        cipher: Cipher,
    ): Result<Cipher, BiometricAuthenticationError> = coroutineBinding {
        val promptInfo = when (promptType) {
            BiometricPromptType.Setup -> createPromptInfoForEncryption()
            BiometricPromptType.Login -> createPromptInfoForDecryption()
        }.mapError { throwable ->
            BiometricAuthenticationError.Unexpected(throwable)
        }.bind()

        showPrompt(cipher, promptInfo)
            .mapError { error ->
                handlePromptError(error)
            }.bind()
    }

    private suspend fun showPrompt(
        cipher: Cipher,
        promptInfo: PromptInfo,
    ): Result<Cipher, BiometricPromptError> = suspendCancellableCoroutine { continuation ->
        val executor = ContextCompat.getMainExecutor(activity)
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errCode: Int, errString: CharSequence) {
                continuation.resume(Err(BiometricPromptError(errCode, errString.toString())))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                result.cryptoObject?.cipher?.let { authenticationCipher ->
                    continuation.resume(Ok(authenticationCipher))
                } ?: {
                    continuation.resume(Err(BiometricPromptError(0, "Missing Authentication Cipher")))
                }
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        prompt.authenticate(
            promptInfo,
            cryptoObject,
        )
    }

    private fun handlePromptError(
        biometricError: BiometricPromptError
    ) = when (biometricError.errorCode) {
        BiometricPrompt.ERROR_USER_CANCELED,
        BiometricPrompt.ERROR_CANCELED,
        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
        BiometricPrompt.ERROR_TIMEOUT -> {
            Timber.d("Authentication cancelled: ${biometricError.errorCode}, ${biometricError.message}")
            BiometricAuthenticationError.PromptCancelled
        }
        BiometricPrompt.ERROR_LOCKOUT,
        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
            Timber.w("Authentication lockout: ${biometricError.errorCode}, ${biometricError.message}")
            BiometricAuthenticationError.PromptLocked
        }
        else -> {
            Timber.e("Authentication failure: ${biometricError.errorCode}, ${biometricError.message}")
            BiometricAuthenticationError.PromptFailure(Exception(biometricError.message))
        }
    }

    private fun createPromptInfoForEncryption() = createPromptInfo(
        promptTitle = activity.getString(R.string.biometricSetup_title),
        promptSubtitle = "",
        promptDescription = activity.getString(R.string.biometricSetup_reason),
        promptCancel = activity.getString(R.string.biometricSetup_dismissButton),
        allowedAuthenticators = allowedAuthenticators,
    )

    private fun createPromptInfoForDecryption() = createPromptInfo(
        promptTitle = activity.getString(R.string.login_biometricTitle),
        promptSubtitle = "",
        promptDescription = "",
        promptCancel = activity.getString(R.string.global_cancel),
        allowedAuthenticators = allowedAuthenticators,
    )

    private fun createPromptInfo(
        promptTitle: String,
        promptSubtitle: String,
        promptDescription: String,
        promptCancel: String,
        allowedAuthenticators: Int,
    ): Result<PromptInfo, Throwable> = runSuspendCatching {
        PromptInfo.Builder().apply {
            setTitle(promptTitle)
            setSubtitle(promptSubtitle)
            setDescription(promptDescription)
            setAllowedAuthenticators(allowedAuthenticators)
            setConfirmationRequired(false)
            setNegativeButtonText(promptCancel)
        }.build()
    }

    private data class BiometricPromptError(val errorCode: Int, val message: String)
}
