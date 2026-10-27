/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 *
 * Copy-out / backup helper: shows where backups land in the guest, builds
 * clipboard commands (docker/podman cp + save), and lists backups that were
 * pulled onto the phone's Downloads folder.
 */
package com.abhiram.lidroid.ui.screens.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abhiram.lidroid.R
import com.abhiram.lidroid.ui.components.AdaptiveContainer
import com.abhiram.lidroid.ui.components.LiDroidBackIcon
import com.abhiram.lidroid.ui.components.LiDroidGhostButton
import com.abhiram.lidroid.ui.components.LiDroidGroup
import com.abhiram.lidroid.ui.components.LiDroidListRow
import com.abhiram.lidroid.ui.components.LiDroidPrimaryButton
import com.abhiram.lidroid.ui.components.LiDroidSectionLabel
import com.abhiram.lidroid.ui.components.LiDroidTopBar
import com.abhiram.lidroid.ui.theme.LiDroidTokens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainerBackupScreen(
    windowSizeClass: WindowSizeClass,
    onNavigateBack: () -> Unit,
    viewModel: ContainerBackupViewModel = hiltViewModel(),
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val copiedMessage = stringResource(R.string.container_backup_copied)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LiDroidTopBar(
                title = stringResource(R.string.container_backup_title),
                navigationIcon = {
                    LiDroidBackIcon(onClick = onNavigateBack, contentDescription = stringResource(R.string.back))
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        AdaptiveContainer(
            windowSizeClass = windowSizeClass,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = LiDroidTokens.Spacing.LG, vertical = LiDroidTokens.Spacing.LG),
                verticalArrangement = Arrangement.spacedBy(LiDroidTokens.Spacing.MD),
            ) {
                Text(
                    text = stringResource(R.string.container_backup_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                LiDroidSectionLabel(stringResource(R.string.container_backup_location))
                LiDroidGroup {
                    LiDroidListRow(
                        label = stringResource(R.string.container_backup_guest_path),
                        value = ui.guestPath,
                        mono = true,
                    )
                    if (ui.storageAccessEnabled) {
                        LiDroidListRow(
                            label = stringResource(R.string.container_backup_phone_path),
                            value = stringResource(R.string.container_backup_phone_path_value),
                            mono = true,
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.container_backup_downloads_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(
                                start = LiDroidTokens.Spacing.LG,
                                end = LiDroidTokens.Spacing.LG,
                                bottom = LiDroidTokens.Spacing.MD,
                            ),
                        )
                    }
                }

                LiDroidSectionLabel(stringResource(R.string.container_backup_export))
                if (!ui.vmRunning) {
                    Text(
                        text = stringResource(R.string.container_backup_vm_stopped),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                OutlinedTextField(
                    value = ui.containerName,
                    onValueChange = viewModel::setContainerName,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.container_backup_container_name)) },
                    singleLine = true,
                )
                LiDroidPrimaryButton(
                    text = stringResource(R.string.container_backup_copy_export),
                    onClick = {
                        if (viewModel.copyExportCommand()) {
                            scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
                        }
                    },
                    enabled = ui.containerName.isNotBlank(),
                )

                LiDroidSectionLabel(stringResource(R.string.container_backup_save_image))
                OutlinedTextField(
                    value = ui.imageRef,
                    onValueChange = viewModel::setImageRef,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.container_backup_image_ref)) },
                    placeholder = { Text(stringResource(R.string.container_backup_image_placeholder)) },
                    singleLine = true,
                )
                LiDroidGhostButton(
                    text = stringResource(R.string.container_backup_copy_save),
                    onClick = {
                        if (viewModel.copySaveCommand()) {
                            scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                LiDroidSectionLabel(stringResource(R.string.container_backup_tools))
                LiDroidGhostButton(
                    text = stringResource(R.string.container_backup_copy_list),
                    onClick = {
                        viewModel.copyListCommand()
                        scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                LiDroidGhostButton(
                    text = stringResource(R.string.container_backup_copy_all),
                    onClick = {
                        viewModel.copyAllCommand()
                        scope.launch { snackbarHostState.showSnackbar(copiedMessage) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(R.string.container_backup_terminal_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                )

                Spacer(Modifier.height(LiDroidTokens.Spacing.SM))
                LiDroidSectionLabel(stringResource(R.string.container_backup_on_phone))
                LiDroidGhostButton(
                    text = stringResource(R.string.container_backup_refresh),
                    onClick = viewModel::refresh,
                    modifier = Modifier.fillMaxWidth(),
                )
                LiDroidGroup {
                    if (ui.backupListError) {
                        Text(
                            text = stringResource(R.string.container_backup_list_error),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(LiDroidTokens.Spacing.LG),
                        )
                    } else if (ui.backupFiles.isEmpty()) {
                        Text(
                            text = stringResource(R.string.container_backup_none),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(LiDroidTokens.Spacing.LG),
                        )
                    } else {
                        ui.backupFiles.forEach { file ->
                            LiDroidListRow(
                                label = file.name,
                                value = "${viewModel.formatSize(file.sizeBytes)} · ${viewModel.formatDate(file.lastModifiedMs)}",
                                mono = true,
                            )
                        }
                    }
                }
            }
        }
    }
}
