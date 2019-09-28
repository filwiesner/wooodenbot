package common

import com.ktmi.tmi.dsl.builder.scopes.MainScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import com.ktmi.tmi.events.isSubscriber
import com.ktmi.tmi.events.onMessage
import com.ktmi.tmi.messages.TextMessage
import commandMark
import database.Database
import database.now
import kotlinx.coroutines.delay

private val lastMessages = mutableMapOf<String, String>()

private val TextMessage.isBroadcasterOrMod: Boolean get() =
    badges?.let {
        it.containsKey("broadcaster") ||
                it.containsKey("moderator")
    } == true

fun MainScope.commonCommands() {
    onMessage {
        delay(1)
        lastMessages["$channel|${message.username}"] = text
    }

    commands(commandMark) {
        "hello" receive {
            action("MrDestructoid Beep Boop")
        }

        "whoareyou" receive {
            sendMessage("I am bot created by wooodenleg TakeNRG To see available commands write '${commandMark}commands'. " +
                    "For more info write '${commandMark}details'")
        }

        "details" receive {
            sendMessage("I am written in Kotlin/JVM using TmiK library and ~1000 lines of code long CoolCat." +
                    " I run 24/7 on Heroku server and save my data to MongoDB CoolStoryBob " +
                    "You can see my code here \uD83D\uDC49 https://github.com/wooodenleg/wooodenbot")
        }

        "|howlong,hl| {username}" receive { parameters ->
            val username = parameters.getValue("username").toLowerCase()
            val seen = Database.LastSeen.get(channel, username)

            sendMessage(
                if (seen != null)
                    when (val diffHrs = (now - seen.timestamp) / 3600000) {
                        in 0..47 -> "I saw $username $diffHrs hours ago"
                        else -> "I saw $username ${(diffHrs / 24).toInt()} days ago"
                    }
                else "I've never seen $username in this chat"
            )
        }

        "|howmany,hm| [hours] [username]" receive { paramaters ->
            var username = paramaters["username"]
            val hours: Int = paramaters["hours"].let {
                if (it != null) {
                    if (it.toIntOrNull() != null) it.toIntOrNull()
                    else {
                        username = it
                        null
                    }
                } else null
            } ?: 24

            sendMessage(
                if (username != null) {
                    val byUser = Database.Message.messageCountByUser(channel, username!!, hours)
                    val total = Database.Message.messageCount(channel, hours)
                    val percentage = String.format("%.1f", (byUser.toDouble() / (total.toDouble() / 100)))
                    "$username has written $byUser messages in last $hours hours ($percentage% of total messages)"
                } else
                    "${Database.Message.messageCount(channel, hours)} messages were written in this channel in last $hours hours"
            )
        }

        "|archive,ar|" {
            onReceive { sendMessage(
                "[Sub only] Archive message with '${commandMark}archive save {username} {name}'. (The 'name' is name of the quote) " +
                        "Show quote with '${commandMark}archive show {username} [name]'. " +
                        "Show all users quotes with '${commandMark}archive list {user}'."
            ) }

            "|save,sa| {user} {name}" receive { parameters ->
                if (!isSubscriber)
                    sendMessage("This command is for subscribers only, sorry")
                else {
                    val user = parameters.getValue("user").toLowerCase()
                    val name = parameters.getValue("name").toLowerCase()

                    val quote = lastMessages["$channel|$user"]

                    if (quote == null)
                        sendMessage("I can't see any messages from $user, sorry \uD83D\uDE14")
                    else {
                        val result = Database.Quotes.create(channel, user, now, quote, name, message.username)
                        if (result) {
                            sendMessage("Quote saved CorgiDerp")
                            lastMessages.remove("$channel|$name")
                        } else sendMessage("It seems that $user already has quote named $name")
                    }
                }
            }

            "|show,sh| {user} [name]" receive { parameters ->
                val user = parameters.getValue("user").toLowerCase()
                val name = parameters["name"]?.toLowerCase()

                sendMessage(
                    if (name != null) {
                        Database.Quotes.get(channel, user, name)?.toString()
                            ?: "I could not find $name by $user"
                    } else Database.Quotes.quoteList(channel, user).toList()
                        .let {
                            if (it.isEmpty()) "There are no quotes from user $user"
                            else it.random().toString()
                        }
                )
            }

            "list {user}" receive { parameters ->
                val user = parameters.getValue("user").toLowerCase()
                val list = Database.Quotes.quoteList(channel, user)
                if (list.isEmpty())
                    sendMessage("Either user $user does not exist or he does not have any quotes saved")
                else whisper(
                    list.joinToString(" | ") {
                        "${it.name} - \"${it.quote}\""
                    }
                )
            }
        }

        suspend fun getActivePoll(name: String) = Database.Poll.get(name)
        "poll" {
            onReceive { sendMessage(
                "Create poll with '${commandMark}poll create <options>' and stop it with '${commandMark}poll stop'. " +
                        "See active poll with '${commandMark}poll active'. " +
                        "If there is an active poll, you can vote for one of available options with '${commandMark}vote [option]'"
            ) }

            "|create,c| <options>" receive {
                val options = it.getValue("options").split(" ")
                val activePoll = getActivePoll(channel)

                if (message.isBroadcasterOrMod && activePoll == null) {
                    Database.Poll.create(channel, message.username, options)
                    sendMessage("Poll started! Write '${commandMark}vote {option}' to vote")
                }
                else if (!message.isBroadcasterOrMod && activePoll == null)
                    sendMessage("Only mods can create polls, sorry")
                else if (activePoll != null)
                    sendMessage("Another poll is already active")
            }

            "|active,a|" receive {
                val activePoll = getActivePoll(channel)
                if (activePoll == null)
                    sendMessage("There is no active poll")
                else
                    sendMessage("There is active poll by ${activePoll.author} with options: ${activePoll.options.joinToString(", ")}")
            }

            "|stop,s|" receive {
                val activePoll = getActivePoll(channel)
                if (activePoll != null) {
                    val res = Database.Poll.stop(activePoll.name) ?: return@receive
                    val result = res.options.map { option ->
                        option to res.votes.filter { it.option == option }.size
                    }
                    sendMessage("Results: ${result.joinToString { (name, count) -> "$name: $count" }}")
                } else
                    sendMessage("No poll is active right now")
            }
        }

        "vote {option}" receive {
            val activePoll = getActivePoll(channel)
            if (activePoll != null) {
                val success = Database.Poll.vote(channel, message.username, it.getValue("option"))
                if (success) whisper("Vote '${it.getValue("option")}' registered")
                else whisper("Error while registering vote '${it.getValue("option")}'. Make sure the option is spelled correctly")
            } else sendMessage("No poll is active right now")
        }

        "words [hours]" receive {
            val hours = it["hours"]?.toIntOrNull() ?: 24
            val words = Database.Message
                .messagesIn(channel, hours)
                .flatMap { it.message.split(' ') }

            val top3 = words
                .distinct()
                .associateWith { word -> words.count { it == word } }
                .entries
                .filter { it.key.length > 3 }
                .sortedByDescending { it.value }
                .take(3)
                .joinToString { "'${it.key}' (${it.value})" }

            sendMessage("Top three words in past $hours hours are $top3")
        }
    }
}