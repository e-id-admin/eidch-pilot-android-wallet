package ch.admin.foitt.pilotwallet.feature.qrscan

import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.PermissionState
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.CheckQrScanPermission
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.implementation.CheckQrScanPermissionImpl
import ch.admin.foitt.pilotwallet.feature.qrscan.mock.InMemoryCameraIntroRepository
import ch.admin.foitt.pilotwallet.util.getFlagLists
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.SpyK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
internal class CheckQrScanPermissionTestPermissionGranted(
    private val currentPermissionsState: PermissionsState,
) {
    companion object {
        @JvmStatic
        private val permissionGranted = PermissionsState(
            resultPermissionGranted = true,
            permissionWasTriggeredOnce = false,
            resultRationaleShouldBeShown = false,
            resultPromptWasTriggered = false,
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun permissionsStates(): List<PermissionsState> = getFlagLists(0, 7)
            .map { flags ->
                permissionGranted.copy(
                    permissionWasTriggeredOnce = flags.getOrElse(0) { false },
                    resultRationaleShouldBeShown = flags.getOrElse(1) { false },
                    resultPromptWasTriggered = flags.getOrElse(2) { false },
                )
            }
    }

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
    fun `When permissions are granted the usecase should always return PermissionState Granted`() = runTest {
        inMemoryCameraIntroRepository.value = currentPermissionsState.permissionWasTriggeredOnce
        val result = checkQrScanPermissionUseCase(
            permissionsAreGranted = currentPermissionsState.resultPermissionGranted,
            rationaleShouldBeShown = currentPermissionsState.resultRationaleShouldBeShown,
            promptWasTriggered = currentPermissionsState.resultPromptWasTriggered,
        )
        Assert.assertEquals(result, PermissionState.Granted)
    }
}
