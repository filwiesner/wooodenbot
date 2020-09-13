import channels.kaito.kaito
import channels.santa.santa
import channels.sumkat.sumkat
import channels.wooodenleg.wooodenleg
import com.ktmi.tmi.commands.join
import com.ktmi.tmi.dsl.builder.scopes.MainScope
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.dsl.builder.scopes.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect
import com.ktmi.tmi.events.*
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

        onMessage { Database.Message.onMessage(message) }

        commonLogic()
        channel("wooodenleg") { wooodenleg() }
        channel("sumkat") { sumkat() }
        channel("sumkaito") { kaito() }
        channel("sgtsanta95") { santa() }

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