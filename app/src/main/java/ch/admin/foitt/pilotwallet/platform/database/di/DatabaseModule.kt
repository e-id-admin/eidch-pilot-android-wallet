package ch.admin.foitt.pilotwallet.platform.database.di

import ch.admin.foitt.pilotwallet.platform.database.data.repository.DatabaseRepositoryImpl
import ch.admin.foitt.pilotwallet.platform.database.data.repository.SetupSqlCipherDatabaseRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.SetupDatabaseRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.ChangeDatabasePassphrase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CheckDatabasePassphrase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CloseAppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CreateAppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.IsAppDatabaseOpen
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.OpenAppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation.ChangeDatabasePassphraseImpl
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation.CheckDatabasePassphraseImpl
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation.CloseAppDatabaseImpl
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation.CreateAppDatabaseImpl
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation.IsAppDatabaseOpenImpl
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation.OpenAppDatabaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {
    @Singleton
    @Binds
    fun bindDatabaseRepository(repository: DatabaseRepositoryImpl): DatabaseRepository

    @Singleton
    @Binds
    fun bindSetupDatabaseRepository(repository: SetupSqlCipherDatabaseRepository): SetupDatabaseRepository
}

@Module
@InstallIn(ActivityRetainedComponent::class)
interface DatabaseBindModule {
    @Binds
    fun bindCreateDatabase(useCase: CreateAppDatabaseImpl): CreateAppDatabase

    @Binds
    fun bindOpenDatabase(useCase: OpenAppDatabaseImpl): OpenAppDatabase

    @Binds
    fun bindCloseDatabase(useCase: CloseAppDatabaseImpl): CloseAppDatabase

    @Binds
    fun bindCheckDatabasePassphrase(useCase: CheckDatabasePassphraseImpl): CheckDatabasePassphrase

    @Binds
    fun bindChangeDatabasePassphrase(useCase: ChangeDatabasePassphraseImpl): ChangeDatabasePassphrase

    @Binds
    fun bindIsAppDatabaseOpen(useCase: IsAppDatabaseOpenImpl): IsAppDatabaseOpen
}
