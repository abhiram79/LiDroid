/*
 * LiDroid - Rootless Podman for Android
 * Copyright (C) 2024-2026 LiDroid contributors
 */
package com.abhiram.lidroid.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.LocalActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.abhiram.lidroid.engine.control.ControlProviderEntryPoint
import dagger.hilt.android.EntryPointAccessors
import com.abhiram.lidroid.ui.screens.home.HomeScreen
import com.abhiram.lidroid.ui.screens.settings.SettingsScreen
import com.abhiram.lidroid.ui.screens.setup.SetupScreen
import com.abhiram.lidroid.ui.screens.splash.SplashScreen
import com.abhiram.lidroid.ui.screens.terminal.TerminalScreen
import com.abhiram.lidroid.ui.screens.terminal.TerminalViewModel
import com.abhiram.lidroid.ui.screens.backup.ContainerBackupScreen
import com.abhiram.lidroid.ui.screens.status.StatusScreen
import com.abhiram.lidroid.ui.screens.x11.X11Screen
import com.abhiram.lidroid.ui.theme.LiDroidMotion

object Routes {
    const val SPLASH         = "splash"
    const val SETUP          = "setup"
    const val HOME           = "home"
    const val TERMINAL       = "terminal"
    const val TERMINAL_X11   = "terminal/x11"
    const val SETTINGS       = "settings"
    const val STATUS         = "status"
    const val CONTAINER_BACKUP = "container_backup"
}

@Composable
fun LiDroidNavGraph(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController(),
) {
    // Read isSetupDone from a Hilt-scoped helper so MainActivity doesn't need
    // a field-injected SettingsRepository just to drive the start destination.
    val isSetupDone by hiltViewModel<NavGraphViewModel>()
        .isSetupDone
        .collectAsStateWithLifecycle(initialValue = null)

    // Scoped to LiDroidNavGraph composable — survives all navigation including popUpTo(0)
    val terminalViewModel: TerminalViewModel = hiltViewModel()

    val context = LocalContext.current
    // adb `content call --method navigate` control surface (ControlProvider):
    // collect the Hilt-singleton route bus instead of field-injecting it, so
    // NavGraph doesn't need its own ViewModel wiring just for this. Emissions
    // while the wizard is showing are ignored - jumping mid-setup is meaningless.
    LaunchedEffect(navController) {
        val navigator = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ControlProviderEntryPoint::class.java,
        ).controlNavigator()
        navigator.routes.collect { route ->
            if (navController.currentDestination?.route != Routes.SETUP) {
                navController.navigate(route) { launchSingleTop = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        // One consistent expressive motion for every destination swap: a quick
        // fade with a subtle scale, eased — cause and effect, no theatrics.
        enterTransition = {
            fadeIn(tween(LiDroidMotion.Medium, easing = LiDroidMotion.Standard)) +
                scaleIn(
                    initialScale = 0.97f,
                    animationSpec = tween(LiDroidMotion.Medium, easing = LiDroidMotion.Standard),
                )
        },
        exitTransition = {
            fadeOut(tween(LiDroidMotion.Fast, easing = LiDroidMotion.EmphasizedAccelerate))
        },
        popEnterTransition = {
            fadeIn(tween(LiDroidMotion.Medium, easing = LiDroidMotion.Standard)) +
                scaleIn(
                    initialScale = 0.97f,
                    animationSpec = tween(LiDroidMotion.Medium, easing = LiDroidMotion.Standard),
                )
        },
        popExitTransition = {
            fadeOut(tween(LiDroidMotion.Fast, easing = LiDroidMotion.EmphasizedAccelerate)) +
                scaleOut(
                    targetScale = 0.98f,
                    animationSpec = tween(LiDroidMotion.Fast, easing = LiDroidMotion.EmphasizedAccelerate),
                )
        },
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                setupKnown = { isSetupDone != null },
                onDone = {
                    val destination = if (isSetupDone == true) Routes.HOME else Routes.SETUP
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.SETUP) {
            SetupScreen(
                windowSizeClass = windowSizeClass,
                onSetupComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                windowSizeClass = windowSizeClass,
                onNavigateToTerminal = {
                    navController.navigate(Routes.TERMINAL) { launchSingleTop = true }
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                },
                onNavigateToStatus = {
                    navController.navigate(Routes.STATUS) { launchSingleTop = true }
                },
                onNavigateToContainerBackup = {
                    navController.navigate(Routes.CONTAINER_BACKUP) { launchSingleTop = true }
                },
            )
        }

        composable(Routes.STATUS) {
            StatusScreen(
                windowSizeClass = windowSizeClass,
                onNavigateBack = {
                    if (navController.currentDestination?.route == Routes.STATUS) {
                        navController.popBackStack()
                    } else if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(Routes.TERMINAL) {
            TerminalScreen(
                windowSizeClass = windowSizeClass,
                viewModel = terminalViewModel,
                onNavigateBack = {
                    // Only pop if we're not already at HOME to avoid the warning
                    if (navController.currentDestination?.route == Routes.TERMINAL) {
                        navController.popBackStack()
                    } else if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onNavigateToX11 = {
                    navController.navigate(Routes.TERMINAL_X11) { launchSingleTop = true }
                },
            )
        }

        composable(Routes.TERMINAL_X11) {
            X11Screen(
                onNavigateBack = {
                    if (!navController.popBackStack(Routes.TERMINAL, inclusive = false)) {
                        navController.navigate(Routes.TERMINAL) { launchSingleTop = true }
                    }
                },
                onNavigateToTerminal = {
                    if (!navController.popBackStack(Routes.TERMINAL, inclusive = false)) {
                        navController.navigate(Routes.TERMINAL) { launchSingleTop = true }
                    }
                },
            )
        }

        composable(Routes.CONTAINER_BACKUP) {
            ContainerBackupScreen(
                windowSizeClass = windowSizeClass,
                onNavigateBack = {
                    if (navController.currentDestination?.route == Routes.CONTAINER_BACKUP) {
                        navController.popBackStack()
                    } else if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
            )
        }

        composable(Routes.SETTINGS) {
            val activity = LocalActivity.current
            val onLanguageChanged = remember(activity) {
                { activity?.recreate() ?: Unit }
            }
            SettingsScreen(
                windowSizeClass = windowSizeClass,
                onNavigateBack = {
                    if (navController.currentDestination?.route == Routes.SETTINGS) {
                        navController.popBackStack()
                    } else if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onLanguageChanged = onLanguageChanged,
                onNavigateToContainerBackup = {
                    navController.navigate(Routes.CONTAINER_BACKUP) { launchSingleTop = true }
                },
            )
        }
    }
}
