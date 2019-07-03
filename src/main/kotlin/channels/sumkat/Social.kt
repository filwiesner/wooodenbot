package channels.sumkat

import com.ktmi.tmi.client.events.onMessage
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.builder.scopes.sendMessage
import helpers.textMessage

fun ChannelScope.sumkatSocial() {

    onMessage { mesage ->
        val text = mesage.message
        when {

            (!mesage.hasUwUPermit()) && uwuList.any { text.toLowerCase().contains(it) } ->
                sendMessage("UwU = \uD83E\uDD2E")

            !mesage.isMeOrRiek() && text.toLowerCase().contains("denpuppy") ->
                sendMessage("Don't call him that BibleThump His username is wooodenleg")

            text == "SLEEP IS FOR THE WEAK!" && mesage.displayName == "StreamElements" ->
                sendMessage("\uD83D\uDC46 not true! Sleep is good for your body GivePLZ")
        }
    }

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
            it.reply("/w ${it.username} https://bit.ly/2XlSIIw")
        }
    }
}
