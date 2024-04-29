package ch.admin.foitt.pilotwallet.feature.login

import android.annotation.SuppressLint
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricAuthenticationError
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.model.BiometricPromptWrapper
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.LaunchBiometricPrompt
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.model.BiometricsError
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.GetBiometricsCipher
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.ResetBiometrics
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.OpenAppDatabase
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginError
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginWithBiometricsError
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.LoginWithBiometrics
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.LoginWithBiometricsImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.LoadAndDecryptPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.LoadAndDecryptPassphrase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.crypto.Cipher

class LoginWithBiometricsTest {
    @MockK
    private lateinit var mockLoadAndDecryptPassphrase: LoadAndDecryptPassphrase

    @MockK
    private lateinit var mockLaunchBiometricPrompt: LaunchBiometricPrompt

    @MockK
    private lateinit var mockCipher: Cipher

    @MockK
    private lateinit var mockGetBiometricsCipher: GetBiometricsCipher

    @MockK
    private lateinit var mockOpenAppDatabase: OpenAppDatabase

    @MockK
    private lateinit var mockResetBiometrics: ResetBiometrics

    @MockK
    private lateinit var mockWrapper: BiometricPromptWrapper

    private lateinit var testedUseCase: LoginWithBiometrics

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testedUseCase = LoginWithBiometricsImpl(
            loadAndDecryptPassphrase = mockLoadAndDecryptPassphrase,
            launchBiometricPrompt = mockLaunchBiometricPrompt,
            getBiometricsCipher = mockGetBiometricsCipher,
            openAppDatabase = mockOpenAppDatabase,
            resetBiometrics = mockResetBiometrics,
        )

        // Sunny path by default
        coEvery { mockGetBiometricsCipher() } returns Ok(mockCipher)
        coEvery { mockOpenAppDatabase(any()) } returns Ok(Unit)
        coEvery { mockLoadAndDecryptPassphrase(any()) } returns Ok(byteArrayOf(1, 2))
        coEvery { mockLaunchBiometricPrompt(any(), any()) } returns Ok(mockCipher)
        coEvery { mockResetBiometrics() } returns Ok(Unit)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `A successful login follow specific steps and return a success`() = runTest {
        val result: Result<Unit, LoginWithBiometricsError> = testedUseCase(mockWrapper)
        Assert.assertNotNull(result.get())

        coVerify(ordering = Ordering.ORDERED) {
            mockGetBiometricsCipher.invoke()
            mockLaunchBiometricPrompt.invoke(any(), any())
            mockLoadAndDecryptPassphrase.invoke(any())
            mockOpenAppDatabase.invoke(any())
        }
    }

    @Test
    fun `A failed passphrase decryption should show an error`() = runTest {
        val loadError = LoadAndDecryptPassphraseError.Unexpected(Exception("failedDecryption"))
        coEvery { mockLoadAndDecryptPassphrase(any()) } returns Err(loadError)

        val result: Result<Unit, LoginWithBiometricsError> = testedUseCase(mockWrapper)

        Assert.assertTrue(result.getError() is LoginError.Unexpected)

        val error = result.getError() as LoginError.Unexpected
        Assert.assertEquals(loadError.throwable, error.cause)
    }

    @Test
    fun `A wrong passphrase should show an authentication error`() = runTest {
        val error = DatabaseError.WrongPassphrase(Exception())
        coEvery { mockOpenAppDatabase(any()) } returns Err(error)

        val result: Result<Unit, LoginWithBiometricsError> = testedUseCase(mockWrapper)

        Assert.assertTrue(result.getError() is LoginError.InvalidPassphrase)
    }

    @Test
    fun `A cipher exception should show a login error`() = runTest {
        val cipherError = BiometricsError.Unexpected(Exception("CipherException"))
        coEvery { mockGetBiometricsCipher() } returns Err(cipherError)

        val result: Result<Unit, LoginWithBiometricsError> = testedUseCase(mockWrapper)

        Assert.assertTrue(result.getError() is LoginError.Unexpected)

        val error = result.getError() as LoginError.Unexpected
        Assert.assertEquals(cipherError.cause, error.cause)
    }

    @Test
    fun `A biometric prompt cancellation should return a cancelled login`() = runTest {
        coEvery {
            mockLaunchBiometricPrompt(any(), any())
        } returns Err(BiometricAuthenticationError.PromptCancelled)

        val result: Result<Unit, LoginWithBiometricsError> = testedUseCase(mockWrapper)

        Assert.assertTrue(result.getError() is LoginError.Cancelled)
    }

    @SuppressLint("CheckResult")
    @Test
    fun `An invalid key exception resets the biometrics`() = runTest {
        coEvery { mockGetBiometricsCipher() } returns Err(BiometricsError.InvalidatedKey)

        val result = testedUseCase(mockWrapper)

        coVerify(exactly = 0) {
            mockLaunchBiometricPrompt.invoke(any(), any())
            mockLoadAndDecryptPassphrase.invoke(any())
            mockOpenAppDatabase.invoke(any())
        }
        coVerifyOrder {
            mockGetBiometricsCipher()
            mockResetBiometrics()
        }
        Assert.assertTrue(result.getError() is LoginError.BiometricsChanged)
    }
}
