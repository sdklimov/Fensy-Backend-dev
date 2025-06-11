package ru.fensy.dev.proxy

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import ru.fensy.dev.proxy.domain.ModerationProxyResponse

@Service
class OpenAIModerationProxyService(
    private val openAIClient: WebClient,
) {

    suspend fun moderate(content: String): String {
        val requestBody = mapOf(
            "model" to MODEL,
            "tools" to TOOL_DEFINITION,
            "messages" to listOf(
                mapOf(
                    "role" to "system",
                    "content" to SYSTEM_PROMPT
                ),
                mapOf(
                    "role" to "user",
                    "content" to content
                )
            )
        )
        return openAIClient
            .post()
            .uri(URI)
            .bodyValue(requestBody)
            .retrieve()
            .awaitBody<ModerationProxyResponse>()
            .choices.first().message.toolCalls.first().function.arguments.result

    }

    companion object {
        private const val URI = "/chat/completions"
        private const val MODEL = "gpt-4o"
        private const val TOOL_TYPE = "function"
        private const val FUNCTION_NAME = "moderate"
        private const val FUNCTION_DESCRIPTION = "Фильтрует текст на мат и токсичность"
        private const val SYSTEM_PROMPT =
            "Ты фильтр оскорблений и токсичности. ВСЕГДА вызывай функцию 'moderate', чтобы вернуть результат — либо 'Flagged', либо 'OK'. Не отвечай обычным текстом. Флагируй только агрессивный или оскорбительный мат. Позитивный мат (например, 'ахуенный контент') — это OK."

        private val FUNCTION_PARAMETERS = mapOf(
            "type" to "object",
            "properties" to mapOf(
                "result" to mapOf(
                    "type" to "string",
                    "enum" to listOf("Flagged", "OK")
                )
            ),
            "required" to listOf("result")
        )
        private val TOOL_DEFINITION = listOf(
            mapOf(
                "type" to TOOL_TYPE,
                "function" to mapOf(
                    "name" to FUNCTION_NAME,
                    "description" to FUNCTION_DESCRIPTION,
                    "parameters" to FUNCTION_PARAMETERS
                )
            )
        )
    }

}
