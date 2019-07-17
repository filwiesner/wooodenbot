package helpers

import com.ktmi.tmi.client.commands.sendMessage
import com.ktmi.tmi.client.events.UserContext
import com.ktmi.tmi.client.events.onTwitchMessage
import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.filters.filter
import com.ktmi.tmi.messages.TextMessage
import kotlin.coroutines.CoroutineContext

private sealed class PatternPart(val name: String)
private class PatternPath(name: String) : PatternPart(name)
private class PatternRequired(name: String) : PatternPart(name)
private class PatternOptional(name: String) : PatternPart(name)
private class PatternVararg(name: String) : PatternPart(name)

class CommandScope(
    val pattern: String,
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context) {
    private val parsed = pattern
        .split(' ')
        .filter(String::isNotBlank)
        .map { part ->
            when ("${part[0]}${part[part.length - 1]}") {
                "{}" -> PatternRequired(part.substring(1 until (part.length - 1)))
                "[]" -> PatternOptional(part.substring(1 until (part.length - 1)))
                "<>" -> PatternVararg(part.substring(1 until (part.length - 1)))
                else -> PatternPath(part)
            }
        }.also { parsed ->
            if (parsed.filter { it is PatternOptional }.size > 1)
                throw PatternParseException("Multiple optional path parameters are not allowed")
            if (parsed.any { it is PatternVararg } && parsed.last() !is PatternVararg)
                throw PatternParseException("vararg parameter must be at the end of pattern")
        }

    private fun parseInput(input: String): Map<String, String>? {
        val words = input
            .substring(1)
            .split(' ')
            .filter(String::isNotBlank)

        val required = parsed.filter { it !is PatternOptional && it !is PatternVararg }

        if (parsed.none { it is PatternVararg } && words.size !in required.size..parsed.size) return null
        else if (parsed.any { it is PatternVararg } && words.size < required.size) return null

        val result = mutableMapOf<String, String>()

        var index = 0
        for (part in parsed) {
            when (part) {
                is PatternPath ->
                    if (part.name != words[index]) return null
                    else ++index
                is PatternRequired -> {
                    result[part.name] = words[index]
                    ++index
                }
                is PatternOptional -> {
                    if ((parsed.size == words.size) || (parsed.last() is PatternVararg && parsed.size < words.size)) {
                        result[part.name] = words[index]
                        ++index
                    }
                }
                is PatternVararg ->
                    result[part.name] = words.subList(index, words.size).joinToString(" ")
            }
        }

        return result
    }

    fun onReceive(action: suspend UserContext<TextMessage>.(Map<String, String>) -> Unit) =
        onTwitchMessage<TextMessage> {
            val result = parseInput(it.message)
            if (result != null)
                UserContext(it, it.username, it.channel).action(result)
        }


    @TwitchDsl
    inline operator fun String.invoke(block: CommandScope.() -> Unit) =
        CommandScope("$pattern $this", this@CommandScope, coroutineContext)
            .apply(block)

    @TwitchDsl
    infix fun String.receive(block: suspend UserContext<TextMessage>.(Map<String, String>) -> Unit) =
        CommandScope("$pattern $this", this@CommandScope, coroutineContext)
            .onReceive(block)
}

class PatternParseException(message: String) : Exception(message)

@TwitchDsl
inline fun TwitchScope.commands(cmdMark: Char, block: CommandScope.() -> Unit) {
    filter {
        withPredicate { it is TextMessage && it.message[0] == cmdMark}
        CommandScope("", this, coroutineContext)
            .apply(block)
    }
}