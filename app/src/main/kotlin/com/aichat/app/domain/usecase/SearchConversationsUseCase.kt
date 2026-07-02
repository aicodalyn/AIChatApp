package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.Conversation
import com.aichat.app.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
) {
    operator fun invoke(query: String): Flow<List<Conversation>> =
        conversationRepository.search(query)
}
