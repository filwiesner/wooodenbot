package channels.sumkat

import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.plugins.container
import com.ktmi.tmi.messages.TextMessage
import helpers.Greet

// hug clap slap uno
fun ChannelScope.sumkat() {
    container {
        + Greet(customMessages = knownUsers)
        sumkatCommands()
        sumkatSocial()
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

val uwuList = arrayOf("uwu", "owo", "uwo", "owu", "úwú", "︠uw ︠u", "úwù", "ūwū", "ôwô", "ûwû", "u^u", "ùwú", "ùwù", "òwó", "òwò", "ówó", "öwö", "ovo", "°w°", "°v°")
fun TextMessage.hasUwUPermit(): Boolean = (isMe() || username
    .let { it == "bossbear" || it == "samipon"})

fun TextMessage.isMe(): Boolean = (username == "wooodenleg")
fun TextMessage.isMeOrRiek(): Boolean = (isMe() || username == "riekanka")
fun TextMessage.isModOrMe(): Boolean = isMod || isMe()