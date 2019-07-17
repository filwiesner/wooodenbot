package channels.sumkat

import ThrottleOut
import com.ktmi.tmi.client.events.onBitsBadgeTier
import com.ktmi.tmi.client.events.onMessage
import com.ktmi.tmi.client.events.onSubGift
import com.ktmi.tmi.dsl.builder.GlobalContextScope
import com.ktmi.tmi.dsl.plugins.container
import helpers.textMessage

fun GlobalContextScope.sumkatSocial() {

    onMessage { when {
        (!message.hasUwUPermit()) && uwuList.any { text.toLowerCase().contains(it) } ->
            sendMessage("UwU = \uD83E\uDD2E")

        !message.isMe() && text.toLowerCase().contains("denpuppy") ->
            sendMessage("Don't call him that BibleThump His username is wooodenleg")

        text == "SLEEP IS FOR THE WEAK!" && message.displayName == "StreamElements" ->
            sendMessage("\uD83D\uDC46 not true! Sleep is good for your body GivePLZ")
    } }

    val howAddSong = { message: String ->
        message.toLowerCase().let {
            it.contains("how") &&
                    (it.contains("add") || it.contains("request")) &&
                    it.contains("song")
        }
    }

    textMessage {

        containing(howAddSong) {
            it.reply("You can add songs with following command: !sr <YT link>")
        }

        isJust(":)") {
            it.reply("/w ${it.username} ${smileys.random()}")
        }
    }

    onBitsBadgeTier {
        sendMessage("PogChamp that's ${message.threshold} bits ma dude. Keep it up!")
    }
}

val smileys = arrayOf(
    "https://bit.ly/2XlSIIw",
    "https://bit.ly/2LA3Tva",
    "https://bit.ly/2LAIpOO"
)
