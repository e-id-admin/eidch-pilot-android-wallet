package ch.admin.foitt.pilotwallet.feature.login

import android.annotation.SuppressLint
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashedData
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.OpenAppDatabase
import ch.admin.foitt.pilotwallet.platform.login.domain.model.LoginError
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation.LoginWithPinImpl
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.HashPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperedData
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import ch.admin.foitt.pilotwallet.util.assertErrorType
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LoginWithPinTest {
    @MockK
    private lateinit var mockOpenAppDatabase: OpenAppDatabase

    @MockK
    private lateinit var mockPepperPassphrase: PepperPassphrase

    @MockK
    private lateinit var mockHashPassphrase: HashPassphrase

    private lateinit var useCase: LoginWithPinImpl

    private val hashedData = HashedData(byteArrayOf(0, 1), byteArrayOf(1, 0))
    private val pepperedData = PepperedData(byteArrayOf(0, 0), byteArrayOf(1, 1))

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        coEvery { mockOpenAppDatabase(any()) } returns Ok(Unit)
        coEvery { mockHashPassphrase(any(), any()) } returns Ok(hashedData)
        coEvery { mockPepperPassphrase(any(), any()) } returns Ok(pepperedData)

        useCase = LoginWithPinImpl(
            hashPassphrase = mockHashPassphrase,
            openAppDatabase = mockOpenAppDatabase,
            pepperPassphrase = mockPepperPassphrase,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @SuppressLint("CheckResult")
    @Test
    fun `A Successful CheckPin call specific steps`() = runTest {
        val result = useCase("x")

        Assert.assertNotNull(result.get())
        coVerify(ordering = Ordering.ORDERED) {
            mockHashPassphrase.invoke(any(), any())
            mockPepperPassphrase.invoke(any(), any())
            mockOpenAppDatabase.invoke(any())
        }
    }

    @Test
    fun `A failed hash should fail the pin check and return an unexpected error`() = runTest {
        coEvery { mockHashPassphrase(any(), any()) } returns Err(HashDataError.Unexpected(Exception()))

        val result = useCase("x")

        result.assertErrorType(LoginError.Unexpected::class)
    }

    @Test
    fun `A failed peppering should fail the pin check and return an unexpected error`() = runTest {
        val exception = Exception("pepper failed")
        coEvery { mockPepperPassphrase(any(), any()) } returns Err(PepperPassphraseError.Unexpected(exception))

        val result = useCase("x")

        result.assertErrorType(LoginError.Unexpected::class)
    }

    @Test
    fun `A wrong sqlcipher passphrase should fail the pin check and return an invalid passphrase error`() = runTest {
        coEvery { mockOpenAppDatabase(any()) } returns Err(DatabaseError.WrongPassphrase(Exception()))

        val result = useCase("x")

        result.assertErrorType(LoginError.InvalidPassphrase::class)
    }

    @Test
    fun `An SQLite exception should fail the pin check and return an unexpected error`() = runTest {
        coEvery { mockOpenAppDatabase(any()) } returns Err(DatabaseError.SetupFailed(Exception()))

        val result = useCase("x")

        result.assertErrorType(LoginError.Unexpected::class)
    }
}
