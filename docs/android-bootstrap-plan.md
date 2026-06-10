# Android bootstrap commit plan

This document defines the first code bootstrap set for the Android Kotlin implementation of SSHPortTransfer.

## Scope of the first bootstrap commit

The first commit should create a compilable Android skeleton with:

- Gradle dependency baseline
- application class
- main activity
- package structure
- domain models
- repository interfaces
- use cases
- Room entities and DAO contracts
- secure storage abstraction
- SSH tunnel abstraction
- foreground service skeleton
- simple Compose navigation shell

The first commit does not need to include a working SSH connection yet.

## Files to create

```text
settings.gradle.kts
build.gradle.kts
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/com/dkulikov2019/sshporttransfer/SSHPortTransferApp.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/MainActivity.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/model/AuthType.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/model/ConnectionProfile.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/model/Credentials.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/model/TunnelState.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/repository/ProfileRepository.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/repository/SecureCredentialsStore.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/repository/SshTunnelRepository.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/repository/KnownHostsRepository.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/GetProfilesUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/GetProfileByIdUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/SaveProfileUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/DeleteProfileUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/ConnectTunnelUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/DisconnectTunnelUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/domain/usecase/ObserveTunnelStateUseCase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/local/db/entity/ConnectionProfileEntity.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/local/db/entity/KnownHostEntity.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/local/db/dao/ProfileDao.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/local/db/AppDatabase.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/local/mapper/ConnectionProfileMapper.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/secure/EncryptedCredentialsStore.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/ssh/model/ForwardConfig.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/ssh/SshTunnelManager.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/ssh/HostKeyVerifierImpl.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/repository/ProfileRepositoryImpl.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/repository/KnownHostsRepositoryImpl.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/data/repository/SshTunnelRepositoryImpl.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/service/TunnelServiceAction.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/service/TunnelNotificationFactory.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/service/TunnelForegroundService.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/profile/ProfilesUiState.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/profile/EditProfileUiState.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/profile/ProfilesViewModel.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/profile/EditProfileViewModel.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/tunnel/TunnelUiState.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/tunnel/TunnelViewModel.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/navigation/Destinations.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/presentation/navigation/AppNavGraph.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/ui/screens/ProfilesScreen.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/ui/screens/EditProfileScreen.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/ui/screens/TunnelStatusCard.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/ui/theme/Color.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/ui/theme/Theme.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/ui/theme/Type.kt
app/src/main/java/com/dkulikov2019/sshporttransfer/di/AppModule.kt
```

## Bootstrap coding rules

- New profile forms must be empty for connection fields.
- No hardcoded SSH host, port, username, or forwarding values.
- Passwords must be abstracted through secure storage interfaces.
- UI must compile without real SSH runtime implementation.
- Stub implementations are acceptable where integration is pending.
- Foreground service should compile and expose start and stop actions.
- Use `StateFlow` and coroutines for observable state.

## Suggested content direction

### Gradle
Configure:
- Kotlin Android
- Compose
- Hilt
- Room with kapt or KSP
- minSdk appropriate for current Android baseline
- targetSdk current stable baseline

### Domain models
Provide stable models exactly aligned with the specification.

### Repositories
Keep interfaces clean and independent from Android framework classes.

### Data layer
Use placeholder implementations where necessary, but keep method signatures production-oriented.

### Service
Service should:
- create a notification channel
- support explicit start/stop actions
- enter foreground mode
- expose placeholders for tunnel lifecycle integration

### UI shell
Provide:
- profiles screen with empty state
- edit screen with blank fields for new profile creation
- tunnel status card for current state rendering

## Recommended second commit after bootstrap

1. Wire Hilt modules.
2. Implement Room database creation.
3. Implement profile persistence.
4. Implement encrypted credentials storage.
5. Connect view models to repositories.
6. Add profile create/edit flow.

## Recommended third commit

1. Add sshj dependency integration details.
2. Implement host key verification flow.
3. Implement connect/disconnect.
4. Start local port forwarding.
5. Connect service with tunnel repository.
