package ch.admin.foitt.openid4vc.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.vcStatus.HandleStatusList2021EntryError
import ch.admin.foitt.openid4vc.domain.model.vcStatus.PrepareStatusListError
import ch.admin.foitt.openid4vc.domain.usecase.HandleStatusList2021Entry
import ch.admin.foitt.openid4vc.domain.usecase.PrepareStatusList
import ch.admin.foitt.openid4vc.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.zip.ZipException

class HandleStatusList2021EntryImplTest {
    @MockK
    private lateinit var mockPrepareStatusList: PrepareStatusList

    private lateinit var handleStatusList2021Entry: HandleStatusList2021Entry

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        handleStatusList2021Entry = HandleStatusList2021EntryImpl(
            mockPrepareStatusList
        )

        coEvery { mockPrepareStatusList.invoke(any()) } returns Ok(getMockStatusListValid())
    }

    @Test
    fun `fetching status of an valid credential returns false`() = runTest {
        val result = handleStatusList2021Entry.invoke(mockUrl, mockIndex).assertOk()

        assertEquals(false, result)
    }

    @Test
    fun `fetching status of a revoked credential returns true`() = runTest {
        coEvery { mockPrepareStatusList.invoke(any()) } returns Ok(getMockStatusListRevoked())

        val result = handleStatusList2021Entry.invoke(mockUrl, mockIndex).assertOk()

        assertEquals(true, result)
    }

    @Test
    fun `No internet during fetching returns an error`() = runTest {
        coEvery { mockPrepareStatusList.invoke(any()) } returns Err(PrepareStatusListError.NetworkError)

        val result = handleStatusList2021Entry.invoke(mockUrl, 1)

        assertNotNull(result.getError())
        assertTrue(result.getError() is HandleStatusList2021EntryError.NetworkError)
    }

    @Test
    fun `Fetching a non base64 encoded list returns an error`() = runTest {
        coEvery {
            mockPrepareStatusList.invoke(any())
        } returns Err(PrepareStatusListError.Unexpected(IllegalArgumentException()))

        val result = handleStatusList2021Entry.invoke(mockUrl, 1)

        assertNotNull(result.getError())
        assertTrue(result.getError() is HandleStatusList2021EntryError.Unexpected)
    }

    @Test
    fun `Fetching a non giz compressed list returns an error`() = runTest {
        coEvery { mockPrepareStatusList.invoke(any()) } returns Err(PrepareStatusListError.Unexpected(ZipException()))

        val result = handleStatusList2021Entry.invoke(mockUrl, 1)

        assertNotNull(result.getError())
        assertTrue(result.getError() is HandleStatusList2021EntryError.Unexpected)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    companion object {
        private const val mockIndex = 783
        private const val listIndex = mockIndex / 8
        private val mockUrl = URL("https://example.com")

        private fun getMockStatusListRevoked(): ByteArray {
            val list = mutableListOf<Byte>()
            for (i in 0 until listIndex) {
                list.add(i, 0)
            }
            list.add(listIndex, 1)
            return list.toByteArray()
        }

        private fun getMockStatusListValid(): ByteArray {
            val list = mutableListOf<Byte>()
            for (i in 0 until listIndex) {
                list.add(i, 0)
            }
            list.add(listIndex, 4)
            return list.toByteArray()
        }
    }
}
