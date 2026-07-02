package com.aichat.app.domain.provider

import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.impl.CerebrasProvider
import com.aichat.app.domain.provider.impl.ClaudeProvider
import com.aichat.app.domain.provider.impl.CohereProvider
import com.aichat.app.domain.provider.impl.CustomProvider
import com.aichat.app.domain.provider.impl.DeepSeekProvider
import com.aichat.app.domain.provider.impl.FireworksProvider
import com.aichat.app.domain.provider.impl.GeminiProvider
import com.aichat.app.domain.provider.impl.GroqProvider
import com.aichat.app.domain.provider.impl.HyperbolicProvider
import com.aichat.app.domain.provider.impl.LiteLlmProvider
import com.aichat.app.domain.provider.impl.LmStudioProvider
import com.aichat.app.domain.provider.impl.MistralProvider
import com.aichat.app.domain.provider.impl.MoonshotProvider
import com.aichat.app.domain.provider.impl.NovitaProvider
import com.aichat.app.domain.provider.impl.OllamaProvider
import com.aichat.app.domain.provider.impl.OpenAiCompatibleProvider
import com.aichat.app.domain.provider.impl.OpenAiProvider
import com.aichat.app.domain.provider.impl.OpenRouterProvider
import com.aichat.app.domain.provider.impl.PerplexityProvider
import com.aichat.app.domain.provider.impl.QwenProvider
import com.aichat.app.domain.provider.impl.SambanovaProvider
import com.aichat.app.domain.provider.impl.SiliconFlowProvider
import com.aichat.app.domain.provider.impl.TogetherProvider
import com.aichat.app.domain.provider.impl.VllmProvider
import com.aichat.app.domain.provider.impl.XaiProvider
import io.ktor.client.HttpClient

object ProviderFactory {
    fun create(type: ProviderType): AiProvider = when (type) {
        ProviderType.OPENAI -> OpenAiProvider()
        ProviderType.CLAUDE -> ClaudeProvider()
        ProviderType.GEMINI -> GeminiProvider()
        ProviderType.DEEPSEEK -> DeepSeekProvider()
        ProviderType.OPENROUTER -> OpenRouterProvider()
        ProviderType.GROQ -> GroqProvider()
        ProviderType.TOGETHER -> TogetherProvider()
        ProviderType.FIREWORKS -> FireworksProvider()
        ProviderType.CEREBRAS -> CerebrasProvider()
        ProviderType.MISTRAL -> MistralProvider()
        ProviderType.COHERE -> CohereProvider()
        ProviderType.PERPLEXITY -> PerplexityProvider()
        ProviderType.XAI -> XaiProvider()
        ProviderType.HYPERBOLIC -> HyperbolicProvider()
        ProviderType.NOVITA -> NovitaProvider()
        ProviderType.SILICONFLOW -> SiliconFlowProvider()
        ProviderType.QWEN -> QwenProvider()
        ProviderType.MOONSHOT -> MoonshotProvider()
        ProviderType.SAMBANOVA -> SambanovaProvider()
        ProviderType.OLLAMA -> OllamaProvider()
        ProviderType.LM_STUDIO -> LmStudioProvider()
        ProviderType.VLLM -> VllmProvider()
        ProviderType.LITELLM -> LiteLlmProvider()
        ProviderType.OPENAI_COMPATIBLE -> OpenAiCompatibleProvider()
        ProviderType.CUSTOM -> CustomProvider()
    }

    fun createWithClient(type: ProviderType, client: HttpClient): AiProvider {
        val provider = create(type)
        when (provider) {
            is BaseOpenAiCompatibleProvider -> provider.httpClient = client
            is ClaudeProvider -> provider.httpClient = client
            is GeminiProvider -> provider.httpClient = client
            is CohereProvider -> provider.httpClient = client
            is OllamaProvider -> provider.httpClient = client
        }
        return provider
    }
}
