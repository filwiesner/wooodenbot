import com.ktmi.tmi.client.commands.join
import com.ktmi.tmi.client.commands.sendMessage
import com.ktmi.tmi.client.events.onConnected
import com.ktmi.tmi.client.events.onConnectionState
import com.ktmi.tmi.client.events.onMessage
import com.ktmi.tmi.client.events.onRoomState
import com.ktmi.tmi.dsl.builder.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect

fun main() {
    val token = System.getenv("OAUTH")

    var first = true
    tmi(token) {
        + Reconnect()

        onRoomState { println("Joined ${it.channel}") }

        onConnectionState { println(it) }

        onMessage { sendMessage("wooodenleg", it.message) }

        onConnected {
            if (first) {
                join("wooodenleg")
                first = false
            }
        }
    }
}