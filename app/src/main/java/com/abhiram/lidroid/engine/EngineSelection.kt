/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.engine

/**
 * User-facing backend choice. Persisted in DataStore as the enum name string.
 * AUTO = detect AVF at startup, fall back to QEMU. 99% case.
 */
enum class EngineSelection { AUTO, AVF, QEMU }
