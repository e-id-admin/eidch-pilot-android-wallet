package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation

import ch.admin.foitt.openid4vc.di.DefaultDispatcher
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Field
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Filter
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptor
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CompatibleCredential
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialBody
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCompatibleCredentialsError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.PresentationRequestField
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetAllCredentialBodies
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.GetCompatibleCredentials
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class GetCompatibleCredentialsImpl @Inject constructor(
    private val getAllCredentialBodies: GetAllCredentialBodies,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : GetCompatibleCredentials {
    override suspend fun invoke(
        inputDescriptors: List<InputDescriptor>
    ): Result<List<CompatibleCredential>, GetCompatibleCredentialsError> = withContext(defaultDispatcher) {
        coroutineBinding {
            val credentials = getAllCredentialBodies()
                .mapError { error ->
                    error as GetCompatibleCredentialsError
                }.bind()
            getCompatibleCredentials(credentials, inputDescriptors).bind()
        }
    }

    private fun getCompatibleCredentials(
        credentialBodies: List<CredentialBody>,
        inputDescriptors: List<InputDescriptor>
    ): Result<List<CompatibleCredential>, GetCompatibleCredentialsError> = runSuspendCatching {
        val inputDescriptor = inputDescriptors.first() // TODO: support multiple inputDescriptors
        credentialBodies.map { credentialBody ->
            val fields = getMatchingFields(credentialBody.body, inputDescriptor.constraints.fields)
            CompatibleCredential(credentialBody.credentialId, fields)
        }.filter { credential ->
            credential.requestedFields.isNotEmpty()
        }
    }.mapError { throwable ->
        SsiError.Unexpected(throwable)
    }

    private fun getMatchingFields(json: String, fields: List<Field>): List<PresentationRequestField> {
        val document = JsonPath.using(Configuration.defaultConfiguration().addOptions(Option.ALWAYS_RETURN_LIST)).parse(json)
        val matchingFields = mutableListOf<PresentationRequestField>()
        for (field in fields) {
            val matchingField = getMatchingField(document, field.path)
            val filter = field.filter
            if (matchingField == null || filter != null && !doesAnyValueMatch(matchingField.second, filter)) {
                return emptyList()
            } else if (matchingField.second.size == 1) {
                val requestedField = PresentationRequestField(
                    jsonPath = matchingField.first,
                    value = matchingField.second.first().toString()
                )
                matchingFields.add(requestedField)
            } // TODO: support arrays by applying jsonPath predicates to get evaluated jsonPaths and apply them on the json with actual values
        }
        return matchingFields
    }

    private fun getMatchingField(document: DocumentContext, jsonPaths: List<String>): Pair<String, List<Any>>? {
        jsonPaths.forEach { path ->
            try {
                return path to document.read(path)
            } catch (e: Exception) {
                // jsonPath does not match credential format, try next one
                Timber.e(e)
            }
        }
        return null
    }

    private fun doesAnyValueMatch(values: List<Any>, filter: Filter): Boolean =
        values.any { value ->
            val regex = filter.pattern.toRegex() // TODO: support more filtering options
            regex.containsMatchIn(value.toString())
        }
}
