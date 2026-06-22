package com.dkulikov2019.sshporttransfer.domain.repository

import com.dkulikov2019.sshporttransfer.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface ThemePreferencesRepository {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)
}

