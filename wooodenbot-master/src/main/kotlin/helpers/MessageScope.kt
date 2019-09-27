package helpers

import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.messages.TextMessage
import com.ktmi.tmi.messages.TwitchMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlin.coroutines.CoroutineContext

class MessageScope(
    matchers: List<(String) -> Boolean> = listOf(),
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context) {
    private val matchers = MutableList(matchers.size) { matchers[it] }

    override fun getTwitchFlow(): Flow<TwitchMessage> {
        return super.getTwitchFlow()
            .filter { message ->
                message is TextMessage &&
                        matchers.all { it(message.message) }
            }
    }

    fun matches(predicate: (String) -> Boolean) { matchers.add(predicate) }
    fun isJust(text: String) { matchers.add { it == text } }
    fun contains(text: String) { matchers.add { it.contains(text) } }
    fun contains(regex: Regex) { matchers.add { it.contains(regex) } }
    fun containsAny(vararg words: String ) { matchers.add { message -> words.any { message.contains(it) } } }
    fun containsAll(vararg words: String ) { matchers.add { message -> words.all { message.contains(it) } } }
}