import channels.kaito.kaito
import channels.sumkat.sumkat
import channels.wooodenleg.wooodenleg
import com.ktmi.tmi.commands.join
import com.ktmi.tmi.dsl.builder.scopes.MainScope
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.dsl.builder.scopes.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect
import com.ktmi.tmi.events.onConnected
import com.ktmi.tmi.events.onConnectionState
import com.ktmi.tmi.events.onRoomState
import com.ktmi.tmi.events.onTwitchMessage
import com.ktmi.tmi.messages.UndefinedMessage
import common.commonLogic
import database.Database
import helpers.Greet

fun main() {
    Database.toString() // To wake the DB object
    val token = System.getenv("OAUTH")

    tmi(token) {
        + Reconnect()
        + Greet(customMessages = knownUsers)

        onRoomState { println("Joined $channel") }
        onConnectionState { println(it) }

        commonLogic()
        channel("sumkat") { sumkat() }
        channel("wooodenleg") { wooodenleg() }
        channel("pkmntrainerkaito") { kaito() }

        onTwitchMessage<UndefinedMessage> {
            println("Message not recognized: ${it.rawMessage}")
        }

        joinOnFirstConnect()
    }
}

fun MainScope.joinOnFirstConnect() {
    var first = true

    onConnected {
        val channels = Database.Channels.get()

        if (first) {
            join("wooodenleg")
            for (channel in channels)
                join(channel)

            first = false
        }
    }
}