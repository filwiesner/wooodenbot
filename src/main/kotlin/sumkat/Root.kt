package sumkat

import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.builder.scopes.sendMessage
import onBotCommand

// hug clap slap uno
fun ChannelScope.sumkat() {

    onBotCommand {
        val words = it.message.substring(1).split(" ")
        val target = words.getOrNull(1)
        when (words[0]) {
            "hug" -> sendMessage("GivePLZ ${target ?: it.username} TakeNRG")
            "clap" -> sendMessage("${target ?: "This"} deserves my clapping. \uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F ")
            "slap" -> sendMessage("${target ?: "Hey"}, stop being toxic! \uD83D\uDC4A KAPOW")
            "uno" -> sendMessage("${it.displayName} uses reverse UNO card" + if (target != null) "on you $target" else "")
        }
    }
}