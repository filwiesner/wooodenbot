import com.ktmi.tmi.client.commands.join
import com.ktmi.tmi.client.commands.sendMessage
import com.ktmi.tmi.client.events.*
import com.ktmi.tmi.dsl.builder.MainScope
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.dsl.builder.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect
import com.ktmi.tmi.dsl.plugins.container
import com.ktmi.tmi.messages.UndefinedMessage
import common.commonLogic
import database.Database
import helpers.Greet
import kotlinx.coroutines.sync.Mutex
import sumkat.sumkat

val startChannels = arrayOf("wooodenleg")
fun main() {
    val token = System.getenv("OAUTH")

    tmi(token) {
        + Reconnect()
        + Greet()

        onRoomState { println("Joined ${it.channel}") }
        onConnectionState { println(it) }

        commonLogic()
        channel("sumkat") { sumkat() }

        joinOnFirstConnect(startChannels)
    }
}

fun MainScope.joinOnFirstConnect(channels: Array<String>) {
    var first = true

    onConnected {
        if (first) {
            for (channel in channels)
                join(channel)

            first = false
        }
    }
}