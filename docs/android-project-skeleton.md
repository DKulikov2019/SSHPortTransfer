# Android project skeleton for SSHPortTransfer

## Recommended package structure

```text
app/src/main/java/com/dkulikov2019/sshporttransfer/
├─ SSHPortTransferApp.kt
├─ MainActivity.kt
├─ di/
│  ├─ AppModule.kt
│  ├─ DataModule.kt
│  ├─ DomainModule.kt
│  └─ ServiceModule.kt
├─ domain/
│  ├─ model/
│  │  ├─ AuthType.kt
│  │  ├─ ConnectionProfile.kt
│  │  ├─ Credentials.kt
│  │  └─ TunnelState.kt
│  ├─ repository/
│  │  ├─ ProfileRepository.kt
│  │  ├─ SecureCredentialsStore.kt
│  │  ├─ SshTunnelRepository.kt
│  │  └─ KnownHostsRepository.kt
│  └─ usecase/
│     ├─ GetProfilesUseCase.kt
│     ├─ GetProfileByIdUseCase.kt
│     ├─ SaveProfileUseCase.kt
│     ├─ DeleteProfileUseCase.kt
│     ├─ ConnectTunnelUseCase.kt
│     ├─ DisconnectTunnelUseCase.kt
│     └─ ObserveTunnelStateUseCase.kt
├─ data/
│  ├─ local/
│  │  ├─ db/
│  │  │  ├─ AppDatabase.kt
│  │  │  ├─ dao/
│  │  │  │  └─ ProfileDao.kt
│  │  │  └─ entity/
│  │  │     ├─ ConnectionProfileEntity.kt
│  │  │     └─ KnownHostEntity.kt
│  │  └─ mapper/
│  │     └─ ConnectionProfileMapper.kt
│  ├─ repository/
│  │  ├─ ProfileRepositoryImpl.kt
│  │  └─ KnownHostsRepositoryImpl.kt
│  ├─ secure/
│  │  └─ EncryptedCredentialsStore.kt
│  └─ ssh/
│     ├─ SshTunnelManager.kt
│     ├─ SshTunnelRepositoryImpl.kt
│     ├─ HostKeyVerifierImpl.kt
│     └─ model/
│        └─ ForwardConfig.kt
├─ presentation/
│  ├─ profile/
│  │  ├─ ProfilesViewModel.kt
│  │  ├─ EditProfileViewModel.kt
│  │  ├─ ProfilesUiState.kt
│  │  └─ EditProfileUiState.kt
│  ├─ tunnel/
│  │  ├─ TunnelViewModel.kt
│  │  └─ TunnelUiState.kt
│  └─ navigation/
│     ├─ AppNavGraph.kt
│     └─ Destinations.kt
├─ service/
│  ├─ TunnelForegroundService.kt
│  ├─ TunnelServiceAction.kt
│  └─ TunnelNotificationFactory.kt
└─ ui/
   ├─ screens/
   │  ├─ ProfilesScreen.kt
   │  ├─ EditProfileScreen.kt
   │  └─ TunnelStatusCard.kt
   ├─ components/
   │  ├─ AppTextField.kt
   │  ├─ PrimaryButton.kt
   │  └─ StatusBadge.kt
   └─ theme/
      ├─ Color.kt
      ├─ Theme.kt
      └─ Type.kt
```

## Gradle dependencies

Use the following dependency groups as the initial baseline:

### Core Android
- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.activity:activity-compose`

### Compose
- Compose BOM
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.navigation:navigation-compose`
- debug: `androidx.compose.ui:ui-tooling`

### Architecture
- `androidx.lifecycle:lifecycle-viewmodel-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`

### DI
- Hilt:
  - `com.google.dagger:hilt-android`
  - `com.google.dagger:hilt-compiler`
  - `androidx.hilt:hilt-navigation-compose`

### Room
- `androidx.room:room-runtime`
- `androidx.room:room-ktx`
- `androidx.room:room-compiler`

### Security
- `androidx.security:security-crypto`

### SSH
- `com.hierynomus:sshj`

### Testing
- `junit:junit`
- `androidx.test.ext:junit`
- `androidx.test.espresso:espresso-core`
- `androidx.compose.ui:ui-test-junit4`

## AndroidManifest baseline

The manifest should include at minimum:

- `android.permission.INTERNET`
- `android.permission.FOREGROUND_SERVICE`
- application class declaration
- foreground service declaration for `TunnelForegroundService`

## Suggested initial file responsibilities

### Application
- `SSHPortTransferApp.kt`
  - Hilt application entry point.

### Activity
- `MainActivity.kt`
  - Hosts Compose navigation and app scaffold.

### Domain models
- `AuthType.kt`
  - Supported authentication types.
- `ConnectionProfile.kt`
  - Main profile model used by the app.
- `Credentials.kt`
  - Password or future key-based credentials.
- `TunnelState.kt`
  - Connection state contract.

### Repositories
- `ProfileRepository.kt`
  - CRUD for profile metadata.
- `SecureCredentialsStore.kt`
  - Read/write secrets outside Room.
- `SshTunnelRepository.kt`
  - Connect, disconnect, observe state.
- `KnownHostsRepository.kt`
  - Trusted host key persistence.

### Use cases
- One class per operation for clear UI/domain boundaries.

### Room
- `ConnectionProfileEntity.kt`
  - Database entity for non-secret profile fields.
- `KnownHostEntity.kt`
  - Saved host fingerprints.
- `ProfileDao.kt`
  - CRUD and observe operations.
- `AppDatabase.kt`
  - Room database root.

### Secure storage
- `EncryptedCredentialsStore.kt`
  - Uses EncryptedSharedPreferences or keystore-backed approach.

### SSH
- `SshTunnelManager.kt`
  - Low-level sshj integration.
- `SshTunnelRepositoryImpl.kt`
  - Domain-facing adapter around tunnel manager.
- `HostKeyVerifierImpl.kt`
  - Integrates trust-on-first-use flow with stored fingerprints.
- `ForwardConfig.kt`
  - Internal forwarding configuration model.

### Service
- `TunnelForegroundService.kt`
  - Holds active tunnel lifecycle independent of UI.
- `TunnelServiceAction.kt`
  - Start/stop/connect/disconnect intent contracts.
- `TunnelNotificationFactory.kt`
  - Builds ongoing notification and action buttons.

### Presentation
- `ProfilesViewModel.kt`
  - List, remove, start connection.
- `EditProfileViewModel.kt`
  - Form editing and validation.
- `TunnelViewModel.kt`
  - Active tunnel state binding.

### UI
- `ProfilesScreen.kt`
  - Profiles list screen.
- `EditProfileScreen.kt`
  - Empty-input form for new profile creation.
- `TunnelStatusCard.kt`
  - Compact connection state/status block.

## Important implementation notes

- New profile forms must not contain prefilled connection values.
- Secrets must never be stored in Room entities.
- SSH logic must not live in composables.
- Foreground service is required for active tunnel runtime.
- The initial implementation may support only password auth, but interfaces should keep `PRIVATE_KEY` in the model.
- Host key verification should be designed so the UI can present fingerprint confirmation before trust is persisted.

## Suggested implementation order

1. Gradle setup and manifest.
2. Package structure and empty files.
3. Domain models and repository interfaces.
4. Room entities, DAO, and repository implementation.
5. Secure credentials store abstraction.
6. Foreground service skeleton.
7. SSH tunnel manager abstraction.
8. Compose screens and view models.
9. Host key verification flow.
10. Connect/disconnect integration and testing.
