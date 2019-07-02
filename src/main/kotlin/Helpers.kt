import com.ktmi.tmi.client.events.onTwitchMessage
import com.ktmi.tmi.dsl.builder.TwitchDsl
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.filters.filter
import com.ktmi.tmi.dsl.plugins.Container
import com.ktmi.tmi.dsl.plugins.TwitchPlugin
import com.ktmi.tmi.messages.TextMessage
import org.joda.time.DateTime

@TwitchDsl
fun TwitchScope.onBotCommand(cmdMark: Char = commandMark, block: suspend (TextMessage) -> Unit ) {
    filter {
        withPredicate { it is TextMessage && it.message.startsWith(cmdMark) }
        onTwitchMessage<TextMessage> { block(it) }
    }
}

fun Container.ThrottleOut(interval: Long = 5_000) = object : TwitchPlugin {
    override val name = "throttle_out"

    private var lastSent = 0L

    override fun filterOutgoing(message: String): Boolean {
        val now = DateTime.now().millis

        return (now - lastSent > interval)
            .also { if (it) lastSent = now }
    }
}