package ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model

import com.github.michaelbull.result.Result
import javax.crypto.Cipher

fun interface BiometricPromptWrapper {
    suspend fun launchPrompt(
        cipher: Cipher,
    ): Result<Cipher, BiometricAuthenticationError>
}
