import com.ktmi.tmi.client.commands.join
import com.ktmi.tmi.client.events.*
import com.ktmi.tmi.dsl.builder.MainScope
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.dsl.builder.tmi
import com.ktmi.tmi.dsl.plugins.Reconnect
import common.commonLogic
import helpers.Greet
import helpers.commands
import channels.sumkat.sumkat
import channels.wooodenleg.wooodenleg

val startChannels = arrayOf("wooodenleg", "sumkat")
fun main() {
    val token = System.getenv("OAUTH")

    tmi(token) {
        + Reconnect()
        + Greet(customMessages = knownUsers)

        onRoomState { println("Joined ${it.channel}") }
        onConnectionState { println(it) }

        commonLogic()
        channel("#sumkat") { sumkat() }
        channel("#wooodenleg") { wooodenleg() }


        joinOnFirstConnect(startChannels)
    }
}

val knownUsers = mapOf(
    "wooodenleg"         to "Master wooodenleg is here BlessRNG ",
    "riekanka"           to "Eeeeeeee, riekanka is here GivePLZ \uD83D\uDC9B",
    "bossbear"           to "Hello boss KonCha ",
    "pkmntrainerkaito"   to "Welcome Kaito! (pls dont ban me)",
    "scare_less"         to "Scarlett is here! GivePLZ",
    "smolsasa"           to "Oh hello Sanne, how was your day?",
    "smrtka314cz"        to "Hello Smrtka! Can I go to your wedding?",
    "samipon"            to "Hello Sam GivePLZ ",
    "eglorian"           to "Eggy! Welcome OSFrog ",
    "pizzabeth"          to "Beth! Hello there GivePLZ",
    "moofle"             to "Moof! <insert mooflePet emote here>",
    "katanarrrr"         to "Hello Katanar, I love you GivePLZ ",
    "silie18"            to "Hello Silie \uD83D\uDC9B",
    "hauntedghostkiller" to "Sorry I always say how long you weren't here BibleThump",
    "secret_chopper"     to "Hello Choppy! I love you \uD83D\uDC9B"
)

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