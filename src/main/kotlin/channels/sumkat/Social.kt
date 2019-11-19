package channels.sumkat

import com.ktmi.tmi.dsl.builder.ChannelContextScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import com.ktmi.tmi.events.displayName
import com.ktmi.tmi.events.onBitsBadgeTier
import com.ktmi.tmi.events.onMessage
import helpers.textMessage

const val qweartyId = 101010482
fun ChannelContextScope.sumkatSocial() {

    commands('!') {
        "sr <song>" receive {
            if (message.userId == qweartyId) {
                sendMessage("!removesong $displayName")
                timeout(60)
                whisper("You are not allowed to request songs, ma dude")
            }
        }
    }

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

        containing("thank", "you", "wooodenbot") {
            it.reply("No problem")
        }
    }

    onBitsBadgeTier {
        sendMessage("PogChamp that's ${message.threshold} bits ma dude. Keep it up!")
    }
}

val smileys = arrayOf(
    "https://bit.ly/2KLFd1S",
    "https://bit.ly/2LA3Tva",
    "https://bit.ly/2LAIpOO"
)
