package channels.sumkat

import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.container
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.events.displayName
import com.ktmi.tmi.events.isSubscriber
import com.ktmi.tmi.messages.TextMessage
import com.ktmi.tmi.messages.isMod
import com.ktmi.tmi.messages.parseTwitchPairSet
import helpers.Greet
import helpers.defGreetings

fun TwitchScope.sumkat() = channel("sumkat") {
    container {
        + Greet { lastSeenHours ->
            if (lastSeenHours == null)
                sendMessage("I've never seen you before $displayName TakeNRG Welcome!")
            else if (lastSeenHours > 48) {
                if (isSubscriber)
                    sendMessage("${defGreetings.random()} $displayName, you have been subbed for ${
                    message.badgeInfo
                        ?.parseTwitchPairSet()
                        ?.get("subscriber") 
                    } months")
                else
                    sendMessage("${defGreetings.random()} $displayName")
            }
        }

        sumkatCommands()
        sumkatSocial()
    }
}

val uwuList = arrayOf("uwu", "owo", "uwo", "owu", "úwú", "︠uw ︠u", "úwù", "ūwū", "ôwô", "ûwû", "u^u", "ùwú", "ùwù", "òwó", "òwò", "ówó", "öwö", "ovo", "°w°", "°v°")
fun TextMessage.hasUwUPermit(): Boolean = (isMe() || username
    .let { it == "bossbear" || it == "samipon"})

fun TextMessage.isMe(): Boolean = (username == "wooodenleg")
fun TextMessage.isModOrMe(): Boolean = isMod || isMe()