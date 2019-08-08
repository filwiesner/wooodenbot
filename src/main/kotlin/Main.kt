import channels.kaito.kaito
import channels.kaito.kaitoCommands
import channels.sumkat.sumkat
import channels.wooodenleg.wooodenleg
import com.ktmi.tmi.client.commands.join
import com.ktmi.tmi.client.events.onConnected
import com.ktmi.tmi.client.events.onConnectionState
import com.ktmi.tmi.client.events.onRoomState
import com.ktmi.tmi.dsl.builder.MainScope
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.dsl.builder.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect
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
        channel("#sumkat") { sumkat() }
        channel("#wooodenleg") { wooodenleg() }
        channel("#pkmntrainerkaito") { kaito() }

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