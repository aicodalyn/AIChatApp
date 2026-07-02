package com.aichat.app.ui.navigation

sealed class Screen(val route: String) {
    data object Conversations : Screen("conversations")
    data object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
    data object NewChat : Screen("chat/new")
    data object Providers : Screen("providers")
    data object ProviderDetail : Screen("providers/{providerId}") {
        fun createRoute(providerId: String) = "providers/$providerId"
    }
    data object Settings : Screen("settings")
    data object Lock : Screen("lock")
}
