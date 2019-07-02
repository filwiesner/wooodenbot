package helpers

import com.ktmi.tmi.client.commands.sendMessage
import com.ktmi.tmi.client.events.onTwitchMessage
import com.ktmi.tmi.dsl.plugins.Container
import com.ktmi.tmi.dsl.plugins.TwitchPlugin
import com.ktmi.tmi.messages.TextMessage
import database.Database
import database.Database.LastSeen
import database.now
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val ignoredUser = listOf("streamelements")
val greetings = arrayOf("Hello there", "Nice to see you", "Heyoooo", " GivePLZ ", "I am glad to see you here")

fun Container.Greet(hours: Int = 20, customMessages: Map<String, String> = mapOf()) = object : TwitchPlugin {
    override val name = "greet"

    init { launch {
        Database.toString()

        onTwitchMessage<TextMessage> { message ->
            println(!ignoredUser.contains(message.username))
            if (!ignoredUser.contains(message.username)) GlobalScope.launch {
                try {
                    val seen = LastSeen.get(message.channel, message.username)
                    println("Seen $seen")

                    if (seen == null) neverSeen(message)
                    else seen(message, seen)

                    LastSeen.set(message.channel, message.username)
                } catch (t: Throwable) { t.printStackTrace() }
            }
        }
    } }

    fun neverSeen(message: TextMessage) {
        sendMessage(message.channel, "I've never seen you before ${message.displayName} TakeNRG Welcome!")
    }

    fun seen(message: TextMessage, lastSeen: database.LastSeen) {
        val diff = ((now - lastSeen.timestamp) / 3600000).toInt()

        if (diff >= hours) when {
            customMessages.containsKey(message.username) ->
                sendMessage(message.channel, "${customMessages[message.username]} , I haven't seen you for some time! ($diff hours)")
            else ->
                sendMessage(message.channel, "${greetings.random()} ${message.displayName}. I haven't seen you for some time! ($diff hours)")
        }
    }
}