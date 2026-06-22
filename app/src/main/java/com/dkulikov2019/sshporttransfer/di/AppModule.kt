package com.dkulikov2019.sshporttransfer.di

import android.content.Context
import androidx.room.Room
import com.dkulikov2019.sshporttransfer.data.local.db.AppDatabase
import com.dkulikov2019.sshporttransfer.data.local.db.dao.KnownHostDao
import com.dkulikov2019.sshporttransfer.data.local.db.dao.ProfileDao
import com.dkulikov2019.sshporttransfer.data.preferences.ThemePreferencesRepositoryImpl
import com.dkulikov2019.sshporttransfer.data.repository.KnownHostsRepositoryImpl
import com.dkulikov2019.sshporttransfer.data.repository.ProfileRepositoryImpl
import com.dkulikov2019.sshporttransfer.data.repository.SshTunnelRepositoryImpl
import com.dkulikov2019.sshporttransfer.data.secure.EncryptedCredentialsStore
import com.dkulikov2019.sshporttransfer.data.ssh.HostKeyVerifierImpl
import com.dkulikov2019.sshporttransfer.data.ssh.SshTunnelManager
import com.dkulikov2019.sshporttransfer.domain.repository.KnownHostsRepository
import com.dkulikov2019.sshporttransfer.domain.repository.ProfileRepository
import com.dkulikov2019.sshporttransfer.domain.repository.SecureCredentialsStore
import com.dkulikov2019.sshporttransfer.domain.repository.SshTunnelRepository
import com.dkulikov2019.sshporttransfer.domain.repository.ThemePreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    abstract fun bindSecureCredentialsStore(impl: EncryptedCredentialsStore): SecureCredentialsStore

    @Binds
    abstract fun bindKnownHostsRepository(impl: KnownHostsRepositoryImpl): KnownHostsRepository

    @Binds
    abstract fun bindSshTunnelRepository(impl: SshTunnelRepositoryImpl): SshTunnelRepository

    @Binds
    abstract fun bindThemePreferencesRepository(impl: ThemePreferencesRepositoryImpl): ThemePreferencesRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "ssh_port_transfer.db"
            ).build()

        @Provides
        fun provideProfileDao(database: AppDatabase): ProfileDao = database.profileDao()

        @Provides
        fun provideKnownHostDao(database: AppDatabase): KnownHostDao = database.knownHostDao()

        @Provides
        @Singleton
        fun provideHostKeyVerifier(
            knownHostsRepository: KnownHostsRepository
        ): HostKeyVerifierImpl = HostKeyVerifierImpl(knownHostsRepository)

        @Provides
        @Singleton
        fun provideSshTunnelManager(
            hostKeyVerifier: HostKeyVerifierImpl
        ): SshTunnelManager = SshTunnelManager(hostKeyVerifier)
    }
}
