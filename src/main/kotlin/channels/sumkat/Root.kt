package channels.sumkat

import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.plugins.container
import com.ktmi.tmi.messages.TextMessage
import helpers.Greet

// hug clap slap uno
fun ChannelScope.sumkat() {
    sumkatCommands()
    sumkatSocial()
}

val uwuList = arrayOf("uwu", "owo", "uwo", "owu", "úwú", "︠uw ︠u", "úwù", "ūwū", "ôwô", "ûwû", "u^u", "ùwú", "ùwù", "òwó", "òwò", "ówó", "öwö", "ovo", "°w°", "°v°")
fun TextMessage.hasUwUPermit(): Boolean = (isMe() || username
    .let { it == "bossbear" || it == "samipon"})

fun TextMessage.isMe(): Boolean = (username == "wooodenleg")
fun TextMessage.isMeOrRiek(): Boolean = (isMe() || username == "riekanka")
fun TextMessage.isModOrMe(): Boolean = isMod || isMe()