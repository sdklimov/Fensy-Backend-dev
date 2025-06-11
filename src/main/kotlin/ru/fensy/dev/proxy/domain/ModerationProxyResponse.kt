package ru.fensy.dev.proxy.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class ModerationProxyResponse(
    val choices: List<Choice>,
)

data class Choice(
    val message: Message,
)

data class Message(
    @JsonProperty("tool_calls")
    val toolCalls: List<ToolCall> = emptyList(),
)

data class ToolCall(
    val function: FunctionCall,
)

data class FunctionCall(
    @JsonDeserialize(using = ArgumentDeserializer::class)
    val arguments: Argument,
)

data class Argument(
    val result: String,
)

class ArgumentDeserializer : JsonDeserializer<Argument>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Argument {
        val jsonString = p.text

        // Используем ObjectMapper для парсинга строки в объект Argument
        return jacksonObjectMapper().readValue(jsonString, Argument::class.java)
    }
}