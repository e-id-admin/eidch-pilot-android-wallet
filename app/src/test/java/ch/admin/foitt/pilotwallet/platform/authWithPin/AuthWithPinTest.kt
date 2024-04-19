package ch.admin.foitt.pilotwallet.platform.authWithPin

import android.annotation.SuppressLint
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.model.AuthWithPinError
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.AuthWithPin
import ch.admin.foitt.pilotwallet.platform.authWithPin.domain.usecase.implementation.AuthWithPinImpl
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashedData
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CheckDatabasePassphrase
import ch.admin.foitt.pilotwallet.platform.passphraseHashing.domain.usecase.HashPassphrase
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperPassphraseError
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.model.PepperedData
import ch.admin.foitt.pilotwallet.platform.passphrasePeppering.domain.usecase.PepperPassphrase
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AuthWithPinTest {

    @MockK
    private lateinit var mockHashPassphrase: HashPassphrase

    @MockK
    private lateinit var mockPepperPassphrase: PepperPassphrase

    @MockK
    private lateinit var mockCheckDatabasePassphrase: CheckDatabasePassphrase

    @MockK
    private lateinit var mockSetErrorDialogState: SetErrorDialogState

    private val hashedData = HashedData(byteArrayOf(0, 1), byteArrayOf(1, 0))
    private val pepperedData = PepperedData(byteArrayOf(0, 0), byteArrayOf(1, 1))

    private lateinit var testedUseCase: AuthWithPin

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { mockHashPassphrase(any(), any()) } returns Ok(hashedData)
        coEvery { mockPepperPassphrase(any(), any()) } returns Ok(pepperedData)
        coEvery { mockCheckDatabasePassphrase(any()) } returns Ok(Unit)
        coEvery { mockSetErrorDialogState.invoke(any()) } just runs

        testedUseCase = AuthWithPinImpl(
            hashPassphrase = mockHashPassphrase,
            pepperPassphrase = mockPepperPassphrase,
            checkDatabasePassphrase = mockCheckDatabasePassphrase,
            setErrorDialogState = mockSetErrorDialogState
        )
    }

    @SuppressLint("CheckResult")
    @Test
    fun `A successful auth with pin call follows specific steps`() = runTest {
        val result = testedUseCase(pin = "123")

        Assert.assertNotNull(result.get())
        Assert.assertNull(result.getError())

        coVerify(ordering = Ordering.ORDERED) {
            mockHashPassphrase.invoke(any(), any())
            mockPepperPassphrase.invoke(any(), any())
            mockCheckDatabasePassphrase.invoke(any())
        }
    }

    @Test
    fun `A failed hash fails the check and shows an error`() = runTest {
        coEvery { mockHashPassphrase(any(), any()) } returns Err(HashDataError.Unexpected(Exception()))

        val result = testedUseCase(pin = "123")

        Assert.assertTrue(result.getError() is AuthWithPinError.Unexpected)
        coVerify(exactly = 1) {
            mockSetErrorDialogState.invoke(any())
        }
    }

    @Test
    fun `A failed peppering should fail the check and shows an error`() = runTest {
        coEvery { mockPepperPassphrase(any(), any()) } returns Err(PepperPassphraseError.Unexpected(Exception()))

        val result = testedUseCase(pin = "123")

        Assert.assertTrue(result.getError() is AuthWithPinError.Unexpected)
        coVerify(exactly = 1) {
            mockSetErrorDialogState.invoke(any())
        }
    }

    @Test
    fun `A wrong passphrase should fail the check and return an error`() = runTest {
        coEvery { mockCheckDatabasePassphrase(any()) } returns Err(DatabaseError.WrongPassphrase(Exception()))

        val result = testedUseCase(pin = "123")

        Assert.assertTrue(result.getError() is AuthWithPinError.InvalidPassphrase)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
