/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.navigation

import androidx.lifecycle.ViewModel
import com.abhiram.lidroid.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Exposes only the bits NavGraph itself needs — keeps MainActivity free of repo wiring. */
@HiltViewModel
class NavGraphViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val isSetupDone = settingsRepository.isSetupDone
}
