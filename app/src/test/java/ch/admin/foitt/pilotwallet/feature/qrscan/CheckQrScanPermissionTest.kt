package ch.admin.foitt.pilotwallet.feature.qrscan

import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.PermissionState
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.CheckQrScanPermission
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.implementation.CheckQrScanPermissionImpl
import ch.admin.foitt.pilotwallet.feature.qrscan.mock.InMemoryCameraIntroRepository
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.SpyK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CheckQrScanPermissionTest {

    @SpyK
    private var inMemoryCameraIntroRepository = InMemoryCameraIntroRepository()

    private lateinit var checkQrScanPermissionUseCase: CheckQrScanPermission

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        checkQrScanPermissionUseCase = CheckQrScanPermissionImpl(
            cameraIntroRepository = inMemoryCameraIntroRepository,
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given a fresh state, a permission check should return the intro state`() = runTest {
        val result = checkQrScanPermissionUseCase(
            permissionsAreGranted = false,
            rationaleShouldBeShown = false,
            promptWasTriggered = false,
        )

        Assert.assertFalse(inMemoryCameraIntroRepository.value)
        Assert.assertEquals(PermissionState.Intro, result)
    }

    @Test
    fun `Given the intro was shown, a rejected permission check without having shown rationale should return a permanently denied state`() = runTest {
        inMemoryCameraIntroRepository.value = true
        val result = checkQrScanPermissionUseCase(
            permissionsAreGranted = false,
            rationaleShouldBeShown = false,
            promptWasTriggered = false,
        )
        Assert.assertTrue(inMemoryCameraIntroRepository.value)
        Assert.assertEquals(PermissionState.Blocked, result)
    }

    @Test
    fun `A rejected prompt without rationale should return a permanently denied state`() = runTest {
        val result = checkQrScanPermissionUseCase(
            permissionsAreGranted = false,
            rationaleShouldBeShown = false,
            promptWasTriggered = true,
        )
        Assert.assertTrue(inMemoryCameraIntroRepository.value)
        Assert.assertEquals(PermissionState.Blocked, result)
    }
}
