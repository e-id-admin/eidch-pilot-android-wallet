package ch.admin.foitt.pilotwallet.feature.presentationRequest.di

import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.GeneratePresentationMetadata
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.SubmitPresentation
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.implementation.GeneratePresentationMetadataImpl
import ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.usecase.implementation.SubmitPresentationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface PresentationRequestModule {

    @Binds
    fun bindGeneratePresentationMetadata(
        useCase: GeneratePresentationMetadataImpl
    ): GeneratePresentationMetadata

    @Binds
    fun bindSubmitPresentation(
        useCase: SubmitPresentationImpl
    ): SubmitPresentation
}
