package helpers

import com.ktmi.tmi.client.commands.sendMessage
import com.ktmi.tmi.client.events.onTwitchMessage
import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.messages.TextMessage
import kotlin.coroutines.CoroutineContext

class TextScope(
    build: TextScope.() -> Unit,
    parent: TwitchScope?,
    context: CoroutineContext
) : TwitchScope(parent, context) {
    private val listeners = mutableListOf<Pair< (TextMessage)->Boolean, (TextMessage)->Unit >>()

    init {
        this.build()

        onTwitchMessage<TextMessage> {
            for ( (predicate, action) in listeners )
                if (predicate(it)) action(it)
        }
    }

    fun containing(vararg words: String, action: (TextMessage) -> Unit) {
        listeners.add({ message: TextMessage ->
            words.all { message.message.contains(it) }
        } to action)
    }

    fun containing(predicate: (String) -> Boolean, action: (TextMessage) -> Unit) {
        listeners.add({ message: TextMessage -> predicate(message.message) } to action)
    }

    fun isJust(word: String, action: (TextMessage) -> Unit) {
        listeners.add({ message: TextMessage -> message.message == word } to action)
    }

    fun TextMessage.reply(msg: String) = sendMessage(channel, msg)
}

@TwitchDsl
fun TwitchScope.textMessage(block: TextScope.() -> Unit) =
    TextScope(block,this, coroutineContext)