package helpers

import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.filters.filterUserState
import com.ktmi.tmi.events.UserContext
import com.ktmi.tmi.messages.TextMessage
import com.ktmi.tmi.messages.isBroadcaster
import com.ktmi.tmi.messages.isMod
import com.ktmi.tmi.messages.isSubscriber
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private sealed class PatternPart(val name: String)
private class PatternPath(name: String) : PatternPart(name)
private class PatternRequired(name: String) : PatternPart(name)
private class PatternOptional(name: String) : PatternPart(name)
private class PatternVararg(name: String) : PatternPart(name)
private class PatternChoice(choices: String) : PatternPart(choices) {
    val choices = choices
        .split(',')
        .filter { it.isNotBlank() }
}

class CommandScope(
    val cmdMark: Char,
    val pattern: String,
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context) {
    private val parsedPattern = pattern
        .split(' ')
        .filter(String::isNotBlank)
        .map { part ->
            when ("${part[0]}${part[part.length - 1]}") {
                "{}" -> PatternRequired(part.substring(1 until (part.length - 1)))
                "[]" -> PatternOptional(part.substring(1 until (part.length - 1)))
                "<>" -> PatternVararg(part.substring(1 until (part.length - 1)))
                "||" -> PatternChoice(part.substring(1 until (part.length - 1)))
                else -> PatternPath(part)
            }
        }.also { parsed ->
            if (parsed
                    .map { if (it is PatternChoice) it.choices else listOf(it.name)}
                    .flatten()
                    .let { it.distinct().size != it.size }
            ) throw PatternParseException("Pattern names must be unique")
            if (parsed.any { it is PatternVararg } && parsed.last() !is PatternVararg)
                throw PatternParseException("Vararg parameter must be at the end of pattern")
            if (parsed.any { part -> part is PatternChoice && part.name.count { it == ',' } == 0 })
                throw PatternParseException("Choice pattern must have at least 2 options (separated with comma)")
        }

    private val isVararg = parsedPattern.lastOrNull() is PatternVararg
    private val min = parsedPattern.count { it !is PatternOptional && it !is PatternVararg }
    private val max = if (isVararg) Int.MAX_VALUE else parsedPattern.size
    private val path = parsedPattern
        .filter { it is PatternPath || it is PatternChoice }

    private fun parseInput(input: String): Map<String, String>? {
        val words = input
            .substring(1)
            .split(' ')
            .filter(String::isNotBlank)

        if (words.size !in min..max)
            return null
        if (!path.all { if (it is PatternChoice) it.choices.any { choice -> input.contains(choice)  } else input.contains(it.name) })
            return null

        var wordIndex = 0
        val result = mutableMapOf<String, String>()
        parsing@ for (patternIndex in 0 until parsedPattern.size) {
            if (wordIndex >= words.size) break@parsing

            val part = parsedPattern[patternIndex]
            val word = words[wordIndex]

            // Check path
            when (part) {
                is PatternPath ->
                    if (word == part.name) {
                        ++wordIndex
                        continue@parsing
                    } else return null
                is PatternChoice ->
                    if (part.choices.contains(word)) {
                        ++wordIndex
                        continue@parsing
                    } else return null
                is PatternRequired -> {
                    result[part.name] = word
                    ++wordIndex
                    continue@parsing
                }
                is PatternVararg ->
                    result[part.name] = words
                        .subList(wordIndex, words.size)
                        .joinToString(" ")
            }

            fun canUseOptional(): Boolean {
                val nextPathIndex = parsedPattern
                    .subList(patternIndex, parsedPattern.size)
                    .indexOfFirst { it is PatternPath || it is PatternChoice }
                    .let { if (it == -1) parsedPattern.size else it + patternIndex }

                val nextPathWordIndex =
                    if (nextPathIndex == parsedPattern.size) words.size
                    else parsedPattern[nextPathIndex].let { pattern ->
                        words.indexOfFirst {
                            if (pattern is PatternChoice) pattern.choices.contains(it)
                            else pattern.name == it
                        }
                    }

                val wordsLeft = nextPathWordIndex - wordIndex
                val requiredWords = parsedPattern.subList(patternIndex, nextPathIndex)
                    .count { it is PatternRequired }

                return wordsLeft > requiredWords
            }

            if (part is PatternOptional && canUseOptional()) {
                result[part.name] = word
                ++wordIndex
            }
        }

        return result
    }

    fun onReceive(action: suspend UserContext<TextMessage>.(Map<String, String>) -> Unit) {
        launch { getTwitchFlow().collect {
            if (it is TextMessage && it.message[0] == cmdMark) {
                val result = parseInput(it.message)
                if (result != null)
                    UserContext(it, it.username, it.channel).action(result)
            }
        } }
    }

    @TwitchDsl
    inline operator fun String.invoke(block: CommandScope.() -> Unit) =
        CommandScope(cmdMark, "$pattern $this", this@CommandScope, coroutineContext)
            .apply(block)

    @TwitchDsl
    infix fun String.receive(block: suspend UserContext<TextMessage>.(Map<String, String>) -> Unit) =
        CommandScope(cmdMark, "$pattern $this", this@CommandScope, coroutineContext)
            .onReceive(block)
}

class PatternParseException(message: String) : Exception(message)

@TwitchDsl
inline fun TwitchScope.commands(cmdMark: Char, block: CommandScope.() -> Unit) {
    CommandScope(cmdMark, "", this, coroutineContext)
        .apply(block)
}

@TwitchDsl
inline fun CommandScope.subscribers(block: CommandScope.() -> Unit) {
    filterUserState {
        withPredicate { it.isSubscriber }
        CommandScope(this@subscribers.cmdMark, this@subscribers.pattern, this, coroutineContext)
            .apply(block)
    }
}
@TwitchDsl
inline fun CommandScope.moderators(includingBroadcaster: Boolean = true, block: CommandScope.() -> Unit) {
    filterUserState {
        withPredicate { it.isMod || (includingBroadcaster && it.isBroadcaster)}
        CommandScope(this@moderators.cmdMark, this@moderators.pattern, this, coroutineContext)
            .apply(block)
    }
}