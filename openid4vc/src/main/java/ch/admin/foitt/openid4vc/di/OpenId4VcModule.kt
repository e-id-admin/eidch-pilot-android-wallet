package ch.admin.foitt.openid4vc.di

import ch.admin.foitt.openid4vc.data.CredentialOfferRepositoryImpl
import ch.admin.foitt.openid4vc.data.PresentationRequestRepositoryImpl
import ch.admin.foitt.openid4vc.data.VCStatusRepositoryImpl
import ch.admin.foitt.openid4vc.domain.repository.CredentialOfferRepository
import ch.admin.foitt.openid4vc.domain.repository.PresentationRequestRepository
import ch.admin.foitt.openid4vc.domain.repository.VCStatusRepository
import ch.admin.foitt.openid4vc.domain.usecase.CreateCredentialRequestProofJwt
import ch.admin.foitt.openid4vc.domain.usecase.CreateDidJwk
import ch.admin.foitt.openid4vc.domain.usecase.CreateES512KeyPair
import ch.admin.foitt.openid4vc.domain.usecase.CreatePresentationRequestBody
import ch.admin.foitt.openid4vc.domain.usecase.CreateVerifiablePresentationToken
import ch.admin.foitt.openid4vc.domain.usecase.DeclinePresentation
import ch.admin.foitt.openid4vc.domain.usecase.DeleteKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerConfiguration
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerCredentialInformation
import ch.admin.foitt.openid4vc.domain.usecase.FetchIssuerPublicKeyInfo
import ch.admin.foitt.openid4vc.domain.usecase.FetchPresentationRequest
import ch.admin.foitt.openid4vc.domain.usecase.FetchVCStatus
import ch.admin.foitt.openid4vc.domain.usecase.FetchVerifiableCredential
import ch.admin.foitt.openid4vc.domain.usecase.GenerateKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.GetKeyPair
import ch.admin.foitt.openid4vc.domain.usecase.HandleStatusList2021Entry
import ch.admin.foitt.openid4vc.domain.usecase.PrepareStatusList
import ch.admin.foitt.openid4vc.domain.usecase.SendPresentation
import ch.admin.foitt.openid4vc.domain.usecase.implementation.CreateCredentialRequestProofJwtImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.CreateDidJwkImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.CreateES512KeyPairImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.CreatePresentationRequestBodyImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.CreateVerifiablePresentationTokenImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.DeclinePresentationImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.DeleteKeyPairImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.FetchIssuerConfigurationImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.FetchIssuerCredentialInformationImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.FetchIssuerPublicKeyInfoImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.FetchPresentationRequestImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.FetchVCStatusImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.FetchVerifiableCredentialImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.GenerateKeyPairImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.GetKeyPairImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.HandleStatusList2021EntryImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.PrepareStatusListImpl
import ch.admin.foitt.openid4vc.domain.usecase.implementation.SendPresentationImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class OpenId4VcModule {

    @ActivityRetainedScoped
    @Provides
    fun provideHttpClient(engine: HttpClientEngine, @Named("jsonSerializer") jsonSerializer: Json): HttpClient {
        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(jsonSerializer)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.INFO
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Named("jsonSerializer")
    fun provideJsonSerializer(): Json {
        return Json {
            ignoreUnknownKeys = false
            explicitNulls = false
        }
    }

    @Provides
    fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()
}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal interface OpenId4VCBindings {
    @Binds
    fun bindCredentialOfferRepository(
        repository: CredentialOfferRepositoryImpl
    ): CredentialOfferRepository

    @Binds
    fun bindPresentationRequestRepository(
        repository: PresentationRequestRepositoryImpl
    ): PresentationRequestRepository

    @Binds
    fun bindFetchIssuerCredentialInformation(
        useCase: FetchIssuerCredentialInformationImpl
    ): FetchIssuerCredentialInformation

    @Binds
    fun bindFetchVerifiableCredential(
        useCase: FetchVerifiableCredentialImpl
    ): FetchVerifiableCredential

    @Binds
    fun bindCreateCredentialRequestProofJwt(
        useCase: CreateCredentialRequestProofJwtImpl
    ): CreateCredentialRequestProofJwt

    @Binds
    fun bindCreateES512KeyPair(
        useCase: CreateES512KeyPairImpl
    ): CreateES512KeyPair

    @Binds
    fun bindFetchPresentationRequest(
        useCase: FetchPresentationRequestImpl
    ): FetchPresentationRequest

    @Binds
    fun bindVCStatusRepository(
        repository: VCStatusRepositoryImpl
    ): VCStatusRepository

    @Binds
    fun bindFetchVCStatus(
        useCase: FetchVCStatusImpl
    ): FetchVCStatus

    @Binds
    fun bindPrepareStatusList(
        useCase: PrepareStatusListImpl
    ): PrepareStatusList

    @Binds
    fun bindHandleStatusList2021Entry(
        useCase: HandleStatusList2021EntryImpl
    ): HandleStatusList2021Entry

    @Binds
    fun bindCreatePresentationRequest(
        useCase: CreateVerifiablePresentationTokenImpl
    ): CreateVerifiablePresentationToken

    @Binds
    fun bindSendPresentation(
        useCase: SendPresentationImpl
    ): SendPresentation

    @Binds
    fun bindDeclinePresentation(
        useCase: DeclinePresentationImpl
    ): DeclinePresentation

    @Binds
    fun bindCreatePresentationRequestBody(
        useCase: CreatePresentationRequestBodyImpl
    ): CreatePresentationRequestBody

    @Binds
    fun bindGetKeyPair(
        useCase: GetKeyPairImpl
    ): GetKeyPair

    @Binds
    fun bindFetchIssuerConfiguration(
        useCase: FetchIssuerConfigurationImpl
    ): FetchIssuerConfiguration

    @Binds
    fun bindFetchIssuerPublicKeyInfo(
        useCase: FetchIssuerPublicKeyInfoImpl
    ): FetchIssuerPublicKeyInfo

    @Binds
    fun generateKeyPair(
        useCase: GenerateKeyPairImpl
    ): GenerateKeyPair

    @Binds
    fun deleteKeyPair(
        useCase: DeleteKeyPairImpl
    ): DeleteKeyPair

    @Binds
    fun createDidJwk(
        useCase: CreateDidJwkImpl
    ): CreateDidJwk
}
