package ch.admin.foitt.pilotwallet.platform.passphrase

import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.model.KeystoreKeyConfig
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.GetOrCreateSecretKey
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.implementation.GetCipherForDecryptionImpl
import ch.admin.foitt.pilotwallet.platform.keystoreCrypto.domain.usecase.implementation.GetCipherForEncryptionImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.model.CiphertextWrapper
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.repository.PassphraseRepository
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.EncryptAndSavePassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.LoadAndDecryptPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.EncryptAndSavePassphraseImpl
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.implementation.LoadAndDecryptPassphraseImpl
import ch.admin.foitt.pilotwallet.util.assertErr
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.unwrapError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class EncryptDecryptPassphraseTest {
    @MockK
    private lateinit var mockGetOrCreateSecretKey: GetOrCreateSecretKey

    @MockK
    private lateinit var mockCipher: Cipher

    @MockK
    private lateinit var mockPassConfig: KeystoreKeyConfig

    private lateinit var decryptionUseCase: LoadAndDecryptPassphrase
    private lateinit var encryptionUseCase: EncryptAndSavePassphrase

    private lateinit var currentDecryptionCipher: Cipher
    private lateinit var currentEncryptionCipher: Cipher

    private val encryptionTransformation = "AES_256/GCM/NoPadding"
    private val algorithmName = "AES"
    private val keyBytes = Random(0).nextBytes(32)
    private val gcmTagLength = 128

    private fun createSecretKey(): SecretKey = SecretKeySpec(keyBytes, algorithmName)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val secretKey = createSecretKey()

        coEvery { mockGetOrCreateSecretKey.invoke(keystoreKeyConfig = mockPassConfig) } returns Ok(
            secretKey
        )
        every { mockPassConfig.encryptionTransformation } returns encryptionTransformation
        every { mockPassConfig.gcmAuthTagLength } returns gcmTagLength
        every { mockPassConfig.randomizedEncryptionRequired } returns true

        encryptionUseCase = EncryptAndSavePassphraseImpl(
            passphraseRepository = testPassphraseRepository
        )

        decryptionUseCase = LoadAndDecryptPassphraseImpl(
            passphraseRepository = testPassphraseRepository,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A passphrase is encrypted and decrypted successfully`() = runTest {
        val passphrases: List<String> = listOf(
            "",
            "KodGmxyJBHR&mZx3jpa_¶j3SZT@n^oaxUyVgT4QhEYPPqGcazdh9ndNgXW2!AbW2igRW6XEakUnzpZW",
            "0",
            "-1",
            " ",
            "!@#%^&*()_+-=[]{}|;':\",./<>?~`$¶",
        )

        for (passphrase in passphrases) {
            initializeCiphers()
            val encryptionResult = encryptionUseCase(
                passphrase = passphrase.toByteArray(Charsets.UTF_8),
                encryptionCipher = currentEncryptionCipher
            )

            Assert.assertNotNull(
                "passphrase encryption failed:\n" +
                    "passphrase $passphrase,\n" +
                    encryptionResult.getOrElse { it.throwable.localizedMessage },
                encryptionResult.get()
            )

            val decryptionResult = decryptionUseCase(
                decryptionCipher = currentDecryptionCipher
            )
            decryptionResult.assertOk()

            Assert.assertEquals(passphrase, String(decryptionResult.getOrThrow { it.throwable }))
        }
    }

    @Test
    fun `A encryption exception returns a failure`() = runTest {
        val exception = Exception("CipherError")
        coEvery { mockCipher.doFinal(any()) } throws exception
        val encryptionResult = encryptionUseCase(
            passphrase = "passphrase".toByteArray(Charsets.UTF_8),
            encryptionCipher = mockCipher
        )

        Assert.assertNotNull(encryptionResult.getError())
        val error = encryptionResult.unwrapError()
        Assert.assertEquals(exception, error.throwable)
    }

    @Test
    fun `An decryption exception returns a failure`() = runTest {
        val exception = Exception("CipherError")
        coEvery { mockCipher.doFinal(any()) } throws exception

        val decryptionResult = decryptionUseCase(
            decryptionCipher = mockCipher
        )

        decryptionResult.assertErr()
        val error = decryptionResult.unwrapError()
        Assert.assertEquals(exception, error.throwable)
    }

    private fun initializeCiphers() {
        val getCipherForEncryptionImpl = GetCipherForEncryptionImpl(
            mockGetOrCreateSecretKey,
        )

        val getCipherForDecryptionImpl = GetCipherForDecryptionImpl(
            mockGetOrCreateSecretKey,
        )

        currentEncryptionCipher = getCipherForEncryptionImpl(
            keystoreKeyConfig = mockPassConfig,
            initializationVector = null
        ).getOrThrow { Exception("Get cipher for encryption error") }
        testPassphraseRepository.currentIv = currentEncryptionCipher.iv
        currentDecryptionCipher = runBlocking {
            getCipherForDecryptionImpl(
                keystoreKeyConfig = mockPassConfig,
                initializationVector = testPassphraseRepository.currentIv
            ).getOrThrow { Exception("Get cipher for decryption error") }
        }
    }

    private val testPassphraseRepository = object : PassphraseRepository {
        var currentPassphraseCipherText: ByteArray = byteArrayOf()
        var currentIv: ByteArray = byteArrayOf()

        override suspend fun getPassphrase() =
            CiphertextWrapper(
                ciphertext = currentPassphraseCipherText,
                initializationVector = currentIv,
            )

        override suspend fun savePassphrase(passphraseWrapper: CiphertextWrapper) {
            currentPassphraseCipherText = passphraseWrapper.ciphertext
            currentIv = passphraseWrapper.initializationVector
        }

        override suspend fun deletePassphrase() {
            TODO("Not yet implemented")
        }

        override suspend fun savePassphraseWasDeleted(passphraseWasDeleted: Boolean) {
            TODO("Not yet implemented")
        }

        override suspend fun getPassphraseWasDeleted(): Boolean {
            TODO("Not yet implemented")
        }
    }
}
