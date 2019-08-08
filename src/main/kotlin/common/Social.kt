package common

import ThrottleOut
import com.ktmi.tmi.dsl.builder.MainScope
import com.ktmi.tmi.dsl.plugins.container
import commandMark
import helpers.textMessage

fun MainScope.commonSocial() {
    
    val howAreYou = { message: String ->
        message.contains(username) && message.contains("how are you")
    }

    textMessage {

        containing(howAreYou) {
            it.reply("I am fine, how about you ${it.displayName}? TakeNRG")
        }

        containing("what", "is", "command", "mark") {
            it.reply("My current command mark is '$commandMark'")
        }

        containing("what is love") { it.reply("baby don't hurt me") }

        container {
            + ThrottleOut(10_000)

            textMessage {
                isJust("F") { it.reply("F") }
            }
        }
    }
}