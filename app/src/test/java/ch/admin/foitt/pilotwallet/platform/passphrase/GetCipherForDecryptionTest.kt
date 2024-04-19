package ch.admin.foitt.pilotwallet.platform.passphrase

import android.annotation.SuppressLint
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetCipherForDecryptionError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.GetOrCreateSecretKeyError
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetCipherForDecryption
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetOrCreateSecretKey
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.implementation.GetCipherForDecryptionImpl
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.SecretKey

class GetCipherForDecryptionTest {

    @MockK
    private lateinit var mockGetOrCreateSecretKey: GetOrCreateSecretKey

    @MockK
    private lateinit var mockCipher: Cipher

    @MockK
    private lateinit var mockSecretKey: SecretKey

    @MockK
    private lateinit var mockPassConfig: KeystoreKeyConfig

    private lateinit var testedUseCase: GetCipherForDecryption

    private val initializationVector = byteArrayOf(1, 1, 1, 1)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic(Cipher::class)

        coEvery { mockGetOrCreateSecretKey(any()) } returns Ok(mockSecretKey)

        coEvery { mockSecretKey.format } returns "AES"

        coEvery { Cipher.getInstance(any()) } returns mockCipher

        coEvery { mockCipher.init(any(), any(), any<AlgorithmParameterSpec>()) } just runs

        coEvery { mockPassConfig.encryptionTransformation } returns "AES/GCM/NoPadding"
        coEvery { mockPassConfig.gcmAuthTagLength } returns 128

        testedUseCase = GetCipherForDecryptionImpl(
            getOrCreateSecretKey = mockGetOrCreateSecretKey,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `Getting an initialized Cipher follow specific steps`() = runTest {
        val cipherResult = testedUseCase(
            keystoreKeyConfig = mockPassConfig,
            initializationVector = initializationVector
        )

        Assert.assertNotNull(cipherResult.get())

        coVerify(exactly = 1) {
            mockGetOrCreateSecretKey.invoke(any())
            mockCipher.init(any(), any<SecretKey>(), any<AlgorithmParameterSpec>())
        }
    }

    @Test
    fun `A Cipher exception returns a failure`() = runTest {
        val exception = NoSuchAlgorithmException("Algo exception")
        coEvery { Cipher.getInstance(any()) } throws exception
        val cipherResult = testedUseCase(
            keystoreKeyConfig = mockPassConfig,
            initializationVector = initializationVector
        )

        Assert.assertNotNull(cipherResult.getError())
        Assert.assertTrue(cipherResult.getError() is GetCipherForDecryptionError.Unexpected)
    }

    @Test
    fun `A SecretKey exception returns a failure`() = runTest {
        val keyError = GetOrCreateSecretKeyError.Unexpected(KeyStoreException("SecretKey exception"))
        coEvery { mockGetOrCreateSecretKey(any()) } returns Err(keyError)
        val cipherResult = testedUseCase(
            keystoreKeyConfig = mockPassConfig,
            initializationVector = initializationVector
        )

        Assert.assertNotNull(cipherResult.getError())
        Assert.assertTrue(cipherResult.getError() is GetCipherForDecryptionError.Unexpected)
    }
}
