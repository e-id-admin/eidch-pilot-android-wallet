package ch.admin.foitt.pilotwallet.platform.biometrics

import android.annotation.SuppressLint
import ch.admin.foitt.pilotwallet.platform.appSetupState.domain.repository.UseBiometricLoginRepository
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.LaunchBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.BiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.ResetBiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.EnableBiometrics
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.implementation.EnableBiometricsImpl
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForEncryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetCipherForEncryption
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.InitializePassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.PassphraseStorageKeyConfig
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.InitializePassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.StorePassphrase
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.unwrapError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.crypto.Cipher

class EnableBiometricsTest {

    @MockK
    private lateinit var mockUseBiometricLoginRepository: UseBiometricLoginRepository

    @MockK
    private lateinit var mockGetCipherForEncryption: GetCipherForEncryption

    @MockK
    private lateinit var mockLaunchBiometricPrompt: LaunchBiometricPrompt

    @MockK
    private lateinit var mockInitializePassphrase: InitializePassphrase

    @MockK
    private lateinit var mockStorePassphrase: StorePassphrase

    @MockK
    private lateinit var mockCipher: Cipher

    @MockK
    private lateinit var mockPassphraseStorageKeyConfig: PassphraseStorageKeyConfig

    @MockK
    private lateinit var mockPromptWrapper: BiometricPromptWrapper

    @MockK
    lateinit var mockResetBiometrics: ResetBiometrics

    private lateinit var testedUseCase: EnableBiometrics

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testedUseCase = EnableBiometricsImpl(
            repo = mockUseBiometricLoginRepository,
            getCipherForEncryption = mockGetCipherForEncryption,
            launchBiometricPrompt = mockLaunchBiometricPrompt,
            initializePassphrase = mockInitializePassphrase,
            storePassphrase = mockStorePassphrase,
            passphraseStorageKeyConfig = mockPassphraseStorageKeyConfig,
            resetBiometrics = mockResetBiometrics,
        )

        // Sunny path by default
        coEvery { mockUseBiometricLoginRepository.saveUseBiometricLogin(any()) } just runs
        coEvery { mockGetCipherForEncryption(any(), any()) } returns Ok(mockCipher)
        coEvery { mockLaunchBiometricPrompt(any(), any()) } returns Ok(mockCipher)
        coEvery { mockInitializePassphrase(any(), any()) } returns Ok(Unit)
        coEvery { mockStorePassphrase(any(), any()) } returns Ok(Unit)
        coEvery { mockResetBiometrics() } returns Ok(Unit)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `A successful enable from the setup follows specific steps and returns a success`() = runTest {
        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)
        result.assertOk()

        coVerifyOrder {
            mockResetBiometrics.invoke()
            mockGetCipherForEncryption.invoke(any(), any())
            mockLaunchBiometricPrompt.invoke(any(), any())
            mockInitializePassphrase.invoke(any(), any())
            mockUseBiometricLoginRepository.saveUseBiometricLogin(any())
        }

        coVerify(exactly = 0) {
            mockStorePassphrase.invoke(any(), any())
        }
    }

    @SuppressLint("CheckResult")
    @Test
    fun `A successful enable from the settings follows specific steps and returns a success`() = runTest {
        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = false)
        Assert.assertNotNull(result.get())
        Assert.assertNull(result.getError())

        coVerifyOrder {
            mockResetBiometrics.invoke()
            mockGetCipherForEncryption.invoke(any(), any())
            mockLaunchBiometricPrompt.invoke(any(), any())
            mockStorePassphrase.invoke(any(), any())
            mockUseBiometricLoginRepository.saveUseBiometricLogin(any())
        }
        coVerify(exactly = 0) {
            mockInitializePassphrase.invoke(any(), any())
        }
    }

    @Test
    fun `A failure while resetting the biometric state should return an init error`() = runTest {
        coEvery { mockResetBiometrics() } returns Err(ResetBiometricsError.Unexpected(null))

        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        assertTrue(result.getError() is BiometricsError.Unexpected)
    }

    @Test
    fun `A cipher exception should return an init error`() = runTest {
        val cipherError = GetCipherForEncryptionError.Unexpected(Exception())
        coEvery { mockGetCipherForEncryption(any(), any()) } returns Err(cipherError)

        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        assertTrue(result.getError() is BiometricsError.Unexpected)

        val error = result.unwrapError() as BiometricsError.Unexpected
        assertEquals(cipherError.throwable, error.cause)
    }

    @Test
    fun `A biometric prompt cancellation should return a cancelled biometric init`() = runTest {
        coEvery {
            mockLaunchBiometricPrompt(any(), any())
        } returns Err(BiometricAuthenticationError.PromptCancelled)

        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        assertTrue(result.getError() is BiometricsError.Cancelled)
    }

    @Test
    fun `A biometric prompt cancellation should not initialize the wallet or store the passphrase`() = runTest {
        coEvery {
            mockLaunchBiometricPrompt(any(), any())
        } returns Err(BiometricAuthenticationError.PromptCancelled)

        testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        coVerify(exactly = 0) {
            mockInitializePassphrase.invoke(any(), any())
            mockStorePassphrase.invoke(any(), any())
        }
    }

    @Test
    fun `A biometric prompt failure should return enabling biometrics failed`() = runTest {
        coEvery {
            mockLaunchBiometricPrompt(any(), any())
        } returns Err(BiometricAuthenticationError.PromptFailure(Exception("")))

        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        assertTrue(result.getError() is BiometricsError.Unexpected)
    }

    @Test
    fun `A biometric prompt error should return enabling biometrics error`() = runTest {
        val exception = Exception("Unexpected Exception")
        coEvery {
            mockLaunchBiometricPrompt(any(), any())
        } returns Err(BiometricAuthenticationError.Unexpected(exception))

        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        assertTrue(result.getError() is BiometricsError.Unexpected)

        val error = result.unwrapError() as BiometricsError.Unexpected
        assertEquals(exception, error.cause)
    }

    @Test
    fun `A failed passphrase init should return enabling failed`() = runTest {
        val initPassphraseError = InitializePassphraseError.Unexpected(Exception())
        coEvery { mockInitializePassphrase(any(), any()) } returns Err(initPassphraseError)

        val result = testedUseCase(mockPromptWrapper, "pin", fromSetup = true)

        val error = result.unwrapError() as BiometricsError.Unexpected
        assertEquals(initPassphraseError.throwable, error.cause)
    }
}
