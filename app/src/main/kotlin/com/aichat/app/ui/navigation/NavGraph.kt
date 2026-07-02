package com.aichat.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aichat.app.data.local.security.PinManager
import com.aichat.app.ui.screens.chat.ChatScreen
import com.aichat.app.ui.screens.conversations.ConversationsScreen
import com.aichat.app.ui.screens.lock.LockScreen
import com.aichat.app.ui.screens.providers.ProvidersScreen
import com.aichat.app.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Conversations.route,
    isLocked: Boolean = false,
    onUnlock: () -> Unit = {},
    pinManager: PinManager? = null,
    modifier: Modifier = Modifier,
) {
    if (isLocked) {
        LockScreen(pinManager = pinManager, onUnlock = onUnlock)
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Screen.Conversations.route) {
            ConversationsScreen(
                onConversationClick = { id ->
                    navController.navigate(Screen.Chat.createRoute(id))
                },
                onNewChat = {
                    navController.navigate(Screen.NewChat.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
            ChatScreen(
                conversationId = conversationId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.NewChat.route) {
            ChatScreen(
                conversationId = null,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Providers.route) {
            ProvidersScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onManageProviders = {
                    navController.navigate(Screen.Providers.route)
                },
            )
        }
    }
}
