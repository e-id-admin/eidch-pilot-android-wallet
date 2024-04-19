package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Constraints
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Field
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Filter
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Format
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptor
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.JwtVc
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentialBodies
import ch.admin.foitt.pilotwallet.util.assertOk
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCompatibleCredentialsImplTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCompatibleCredentials: GetCompatibleCredentialsImpl

    @MockK
    private lateinit var mockGetAllCredentialBodies: GetAllCredentialBodies

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getCompatibleCredentials = GetCompatibleCredentialsImpl(mockGetAllCredentialBodies, testDispatcher)

        coEvery { mockGetAllCredentialBodies() } returns Ok(credentials)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getting compatible credentials where two match one field with one path should return both credentials`() =
        runTest(testDispatcher) {
            val inputDescriptors = createSingleFieldInputDescriptors(listOf(JSON_PATH_1))

            val result = getCompatibleCredentials(inputDescriptors).assertOk()

            assertEquals(2, result.size)
            assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
            assertEquals(CREDENTIAL_ID_2, result[1].credentialId)
        }

    @Test
    fun `getting compatible credentials where two match one field with two paths should return both credentials with corresponding path`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createSingleFieldInputDescriptors(listOf(JSON_PATH_3, JSON_PATH_4))

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(2, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
        assertEquals(JSON_PATH_3, result[0].requestedFields[0].jsonPath)
        assertEquals(CREDENTIAL_ID_3, result[1].credentialId)
        assertEquals(JSON_PATH_4, result[1].requestedFields[0].jsonPath)
    }

    @Test
    fun `getting compatible credentials where two match two fields with path should return both credentials`() = runTest(testDispatcher) {
        val inputDescriptors = createFieldPerPathInputDescriptors(listOf(JSON_PATH_1, JSON_PATH_2))

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(2, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
        assertEquals(CREDENTIAL_ID_2, result[1].credentialId)
    }

    @Test
    fun `getting compatible credentials where one match one field with path should return one credential`() = runTest(testDispatcher) {
        val inputDescriptors = createFieldPerPathInputDescriptors(listOf(JSON_PATH_3))

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(1, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
    }

    @Test
    fun `getting compatible credentials where one match two fields with path should return one credential`() = runTest(testDispatcher) {
        val inputDescriptors = createFieldPerPathInputDescriptors(listOf(JSON_PATH_2, JSON_PATH_3))

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(1, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
    }

    @Test
    fun `getting compatible credentials where two match one field with path and filter should return two credentials`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            Field(
                path = listOf(JSON_PATH_1),
                filter = Filter(
                    pattern = ".*1",
                    type = STRING_TYPE
                )
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(2, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
        assertEquals(CREDENTIAL_ID_2, result[1].credentialId)
    }

    @Test
    fun `getting compatible credentials where two match two fields (one with path and filter, one with path only) should return two credentials`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            listOf(
                Field(
                    path = listOf(JSON_PATH_1),
                    filter = Filter(
                        pattern = ".*1",
                        type = STRING_TYPE
                    )
                ),
                Field(path = listOf(JSON_PATH_2))
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(2, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
        assertEquals(CREDENTIAL_ID_2, result[1].credentialId)
    }

    @Test
    fun `getting compatible credentials where two match two fields with path and filter should return two credentials`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            listOf(
                Field(
                    path = listOf(JSON_PATH_1),
                    filter = Filter(
                        pattern = ".*1",
                        type = STRING_TYPE
                    )
                ),
                Field(
                    path = listOf(JSON_PATH_2),
                    filter = Filter(
                        pattern = ".*2",
                        type = STRING_TYPE
                    )
                )
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(2, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
        assertEquals(CREDENTIAL_ID_2, result[1].credentialId)
    }

    @Test
    fun `getting compatible credentials where one match two fields with path and filter should return one credential`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            listOf(
                Field(
                    path = listOf(JSON_PATH_3),
                    filter = Filter(
                        pattern = ".*3",
                        type = STRING_TYPE
                    )
                ),
                Field(
                    path = listOf(JSON_PATH_2),
                    filter = Filter(
                        pattern = ".*2",
                        type = STRING_TYPE
                    )
                )
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(1, result.size)
        assertEquals(CREDENTIAL_ID_1, result[0].credentialId)
    }

    @Test
    fun `getting compatible credentials where one match one field with path and number filter should return one credential`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            listOf(
                Field(
                    path = listOf(JSON_PATH_NUMBER),
                    filter = Filter(
                        pattern = "\\d+",
                        type = NUMBER_TYPE
                    )
                ),
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(1, result.size)
        assertEquals(CREDENTIAL_ID_3, result[0].credentialId)
    }

    @Test
    fun `getting compatible credentials where two match two fields with path and filter should return both credentials and the requested fields`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            listOf(
                Field(
                    path = listOf(JSON_PATH_1),
                    filter = Filter(
                        pattern = ".*1",
                        type = STRING_TYPE
                    )
                ),
                Field(
                    path = listOf(JSON_PATH_2),
                    filter = Filter(
                        pattern = ".*2",
                        type = STRING_TYPE
                    )
                )
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(2, result.size)
        assertEquals(JSON_PATH_1, result[0].requestedFields[0].jsonPath)
        assertEquals(JSON_1_VALUE_1, result[0].requestedFields[0].value)
        assertEquals(JSON_PATH_2, result[0].requestedFields[1].jsonPath)
        assertEquals(JSON_1_VALUE_2, result[0].requestedFields[1].value)

        assertEquals(JSON_PATH_1, result[1].requestedFields[0].jsonPath)
        assertEquals(JSON_2_VALUE_1, result[1].requestedFields[0].value)
        assertEquals(JSON_PATH_2, result[1].requestedFields[1].jsonPath)
        assertEquals(JSON_2_VALUE_2, result[1].requestedFields[1].value)
    }

    @Test
    fun `getting compatible credentials where no fields are requested should return no credentials`() = runTest(testDispatcher) {
        val inputDescriptors = createInputDescriptors(listOf())

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(0, result.size)
    }

    @Test
    fun `getting compatible credentials where none match one field with path should return no credentials`() = runTest(testDispatcher) {
        val inputDescriptors = createSingleFieldInputDescriptors(listOf(NOT_MATCHING_JSON_PATH))

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(0, result.size)
    }

    @Test
    fun `getting compatible credentials where none match one field with path and filter should return no credentials`() = runTest(
        testDispatcher
    ) {
        val inputDescriptors = createInputDescriptors(
            listOf(
                Field(
                    path = listOf(JSON_PATH_1),
                    filter = Filter(
                        pattern = "no_match",
                        type = STRING_TYPE
                    )
                ),
            )
        )

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(0, result.size)
    }

    @Test
    fun `getting compatible credentials where none is in database should return no credentials`() = runTest(testDispatcher) {
        coEvery { mockGetAllCredentialBodies() } returns Ok(emptyList())
        val inputDescriptors = createSingleFieldInputDescriptors(listOf(JSON_PATH_1))

        val result = getCompatibleCredentials(inputDescriptors).assertOk()

        assertEquals(0, result.size)
    }

    @Test
    fun `getting compatible credentials where database throws error should return error`() = runTest(testDispatcher) {
        coEvery { mockGetAllCredentialBodies() } returns Err(SsiError.Unexpected(null))
        val inputDescriptors = createSingleFieldInputDescriptors(listOf(JSON_PATH_1))

        val result = getCompatibleCredentials(inputDescriptors)

        assertEquals(Err(SsiError.Unexpected(null)), result)
    }

    private fun createSingleFieldInputDescriptors(jsonPaths: List<String>, filter: Filter? = null) =
        createInputDescriptors(
            fields = listOf(
                Field(
                    filter = filter,
                    path = jsonPaths
                )
            )
        )

    private fun createFieldPerPathInputDescriptors(jsonPaths: List<String>) =
        createInputDescriptors(
            fields = jsonPaths.map { path ->
                Field(path = listOf(path))
            }
        )

    private fun createInputDescriptors(field: Field) =
        listOf(
            InputDescriptor(
                constraints = Constraints(listOf(field)),
                format = Format(JwtVc("algorithm")),
                id = "id"
            )
        )

    private fun createInputDescriptors(fields: List<Field>) =
        listOf(
            InputDescriptor(
                constraints = Constraints(fields),
                format = Format(JwtVc("algorithm")),
                id = "id"
            )
        )

    companion object {
        private const val STRING_TYPE = "string"
        private const val NUMBER_TYPE = "number"
        private const val JSON_1_VALUE_1 = "value1"
        private const val JSON_1_VALUE_2 = "value2"
        private const val JSON_2_VALUE_1 = "value_1"
        private const val JSON_2_VALUE_2 = "value_2"
        private const val CREDENTIAL_JSON_1 = """{"test1": $JSON_1_VALUE_1, "test2": $JSON_1_VALUE_2, "test3": "value3"}"""
        private const val CREDENTIAL_JSON_2 = """{"test1": $JSON_2_VALUE_1, "test2": $JSON_2_VALUE_2}"""
        private const val CREDENTIAL_JSON_3 = """{"test4": "value4", "test_number": 1}"""
        private const val JSON_PATH_1 = "$.test1"
        private const val JSON_PATH_2 = "$.test2"
        private const val JSON_PATH_3 = "$.test3"
        private const val JSON_PATH_4 = "$.test4"
        private const val JSON_PATH_NUMBER = "$.test_number"
        private const val NOT_MATCHING_JSON_PATH = "$.test_not_matching"

        private const val CREDENTIAL_ID_1 = 1L
        private const val CREDENTIAL_ID_2 = 2L
        private const val CREDENTIAL_ID_3 = 3L

        private val credentials = listOf(
            CredentialBody(
                credentialId = CREDENTIAL_ID_1,
                body = CREDENTIAL_JSON_1
            ),
            CredentialBody(
                credentialId = CREDENTIAL_ID_2,
                body = CREDENTIAL_JSON_2
            ),
            CredentialBody(
                credentialId = CREDENTIAL_ID_3,
                body = CREDENTIAL_JSON_3
            ),
        )
    }
}
