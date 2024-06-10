package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.CredentialActivityDetail
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model.GetCredentialActivityDetailFlowError
import ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.usecase.GetCredentialActivityDetailFlow
import ch.admin.foitt.pilotwallet.platform.activity.domain.model.ActivityError
import ch.admin.foitt.pilotwallet.platform.activity.domain.repository.ActivityRepository
import ch.admin.foitt.pilotwallet.platform.locale.domain.usecase.GetLocalizedDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.MapToCredentialClaimData
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivity
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivity.ACTIVITY_ID
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityClaim.activityClaim1
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityClaim.activityClaim2
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityClaim.activityClaimDisplay1
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityClaim.activityClaimDisplay2
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityClaim.activityClaimDisplays1
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityClaim.activityClaimDisplays2
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityDetail.activityDetail
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityVerifier
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredential.credentialDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredential.credentialDisplays
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimText1
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredentialClaim.credentialClaimText2
import ch.admin.foitt.pilotwallet.util.assertErrorType
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCredentialActivityDetailFlowImplTest {

    private lateinit var getCredentialActivityDetailFlow: GetCredentialActivityDetailFlow

    @MockK
    private lateinit var mockActivityRepository: ActivityRepository

    @MockK
    private lateinit var mockGetLocalizedDisplay: GetLocalizedDisplay

    @MockK
    private lateinit var mockMapToCredentialClaimData: MapToCredentialClaimData

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        getCredentialActivityDetailFlow = GetCredentialActivityDetailFlowImpl(
            activityRepository = mockActivityRepository,
            getLocalizedDisplay = mockGetLocalizedDisplay,
            mapToCredentialClaimData = mockMapToCredentialClaimData,
        )

        coEvery { mockActivityRepository.getActivityDetailFlowById(ACTIVITY_ID) } returns flow { emit(Ok(activityDetail)) }
        coEvery { mockGetLocalizedDisplay(displays = activityClaimDisplays1) } returns activityClaimDisplay1
        coEvery { mockGetLocalizedDisplay(displays = activityClaimDisplays2) } returns activityClaimDisplay2
        coEvery { mockGetLocalizedDisplay(displays = credentialDisplays) } returns credentialDisplay
        coEvery {
            mockMapToCredentialClaimData(
                claim = activityClaim1,
                displays = activityClaimDisplays1
            )
        } returns Ok(credentialClaimText1)
        coEvery {
            mockMapToCredentialClaimData(
                claim = activityClaim2,
                displays = activityClaimDisplays2
            )
        } returns Ok(credentialClaimText2)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `A valid activity id returns a flow with a activity detail`() = runTest {
        val activityDetailFlow = getCredentialActivityDetailFlow(ACTIVITY_ID)

        val activityDetails = activityDetailFlow.toList()
        assertEquals(1, activityDetails.size)
        assertEquals(expectedActivityDetail, activityDetails[0].assertOk())
    }

    @Test
    fun `An invalid activity id returns a flow with an error`() = runTest {
        coEvery { mockActivityRepository.getActivityDetailFlowById(any()) } returns
            flow { emit(Err(ActivityError.Unexpected(null))) }

        val activityDetailFlow = getCredentialActivityDetailFlow(11)

        val activityDetails = activityDetailFlow.toList()
        assertEquals(1, activityDetails.size)
        activityDetails[0].assertErrorType(GetCredentialActivityDetailFlowError::class)
    }

    @Test
    fun `Errors when no credential display is found are returned in a flow`() = runTest {
        coEvery { mockGetLocalizedDisplay(credentialDisplays) } returns null

        val activityDetailFlow = getCredentialActivityDetailFlow(ACTIVITY_ID)

        val activityDetails = activityDetailFlow.toList()
        assertEquals(1, activityDetails.size)
        activityDetails[0].assertErrorType(GetCredentialActivityDetailFlowError::class)
    }

    @Test
    fun `Errors when no activity claim display is found are returned in a flow`() = runTest {
        coEvery { mockMapToCredentialClaimData(activityClaim1, activityClaimDisplays1) } returns Err(SsiError.Unexpected(null))
        coEvery { mockMapToCredentialClaimData(activityClaim2, activityClaimDisplays2) } returns Err(SsiError.Unexpected(null))

        val activityDetailFlow = getCredentialActivityDetailFlow(ACTIVITY_ID)

        val activityDetails = activityDetailFlow.toList()
        assertEquals(1, activityDetails.size)
        activityDetails[0].assertErrorType(GetCredentialActivityDetailFlowError::class)
    }

    companion object {
        private val expectedActivityDetail = CredentialActivityDetail(
            id = ACTIVITY_ID,
            actor = MockActivityVerifier.VERIFIER_NAME,
            actorLogo = MockActivityVerifier.VERIFIER_LOGO,
            credentialPreview = MockCredential.expectedCredentialPreview,
            createdAt = MockActivity.CREATED_AT,
            type = MockActivity.ACTIVITY_TYPE,
            claims = listOf(credentialClaimText1, credentialClaimText2),
        )
    }
}
