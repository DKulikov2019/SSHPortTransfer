package com.dkulikov2019.sshporttransfer.data.preferences

import android.content.Context
import com.dkulikov2019.sshporttransfer.domain.model.ThemeMode
import com.dkulikov2019.sshporttransfer.domain.repository.ThemePreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class ThemePreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : ThemePreferencesRepository {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val themeModeFlow = MutableStateFlow(readThemeMode())

    override fun observeThemeMode(): Flow<ThemeMode> = themeModeFlow.asStateFlow()

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        sharedPreferences.edit()
            .putString(THEME_MODE_KEY, themeMode.name)
            .apply()
        themeModeFlow.value = themeMode
    }

    private fun readThemeMode(): ThemeMode {
        val raw = sharedPreferences.getString(THEME_MODE_KEY, ThemeMode.SYSTEM.name)
        return runCatching { ThemeMode.valueOf(raw ?: ThemeMode.SYSTEM.name) }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    private companion object {
        const val PREFS_NAME = "app_preferences"
        const val THEME_MODE_KEY = "theme_mode"
    }
}

