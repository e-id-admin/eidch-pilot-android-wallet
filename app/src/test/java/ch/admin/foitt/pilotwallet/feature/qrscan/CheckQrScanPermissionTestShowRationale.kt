package ch.admin.foitt.pilotwallet.feature.qrscan

import ch.admin.foitt.pilotwallet.feature.qrscan.domain.model.PermissionState
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.CheckQrScanPermission
import ch.admin.foitt.pilotwallet.feature.qrscan.domain.usecase.implementation.CheckQrScanPermissionImpl
import ch.admin.foitt.pilotwallet.feature.qrscan.mock.InMemoryCameraIntroRepository
import ch.admin.foitt.pilotwallet.util.getFlagLists
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class CheckQrScanPermissionTestShowRationale {
    @SpyK
    private var inMemoryCameraIntroRepository = InMemoryCameraIntroRepository()

    private lateinit var checkQrScanPermissionUseCase: CheckQrScanPermission

    @BeforeEach
    fun setup() {
        checkQrScanPermissionUseCase = CheckQrScanPermissionImpl(
            cameraIntroRepository = inMemoryCameraIntroRepository,
        )
    }

    @TestFactory
    fun `When permissions are denied, a rationale flag should always lead to the rationale state`(): List<DynamicTest> {
        val expected = PermissionState.Rationale
        return getAllExpectedPermissionsStates().map { currentPermissionState ->
            DynamicTest.dynamicTest("$currentPermissionState should return $expected") {
                inMemoryCameraIntroRepository.value = currentPermissionState.permissionWasTriggeredOnce
                runTest {
                    val actual = checkQrScanPermissionUseCase(
                        permissionsAreGranted = currentPermissionState.resultPermissionGranted,
                        rationaleShouldBeShown = currentPermissionState.resultRationaleShouldBeShown,
                        promptWasTriggered = currentPermissionState.resultPromptWasTriggered,
                    )
                    assertEquals(expected, actual)
                }
            }
        }
    }

    private fun getAllExpectedPermissionsStates() = getFlagLists(0, 3).map { flags ->
        PermissionsState(
            resultPermissionGranted = false,
            permissionWasTriggeredOnce = flags.getOrElse(0) { false },
            resultRationaleShouldBeShown = true,
            resultPromptWasTriggered = flags.getOrElse(1) { false },
        )
    }
}
