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

val startChannels = arrayOf("wooodenleg", "sumkat")
fun main() {
    val token = System.getenv("OAUTH")

    tmi(token) {
        + Reconnect()

        onRoomState { println("Joined $channel") }
        onConnectionState { println(it) }

        commonLogic()
        channel("#sumkat") { sumkat() }
        channel("#wooodenleg") { wooodenleg() }


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