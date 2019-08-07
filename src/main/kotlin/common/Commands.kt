package common

import com.ktmi.tmi.client.commands.action
import com.ktmi.tmi.client.events.onMessage
import com.ktmi.tmi.dsl.builder.MainScope
import commandMark
import database.Database
import database.now
import helpers.commands
import helpers.isMod
import helpers.isSubscriber
import kotlinx.coroutines.delay

private val lastMessages = mutableMapOf<String, String>()

fun MainScope.commonCommands() {
    onMessage {
        delay(1)
        lastMessages["$channel|${message.username}"] = text
    }

    commands(commandMark) {
        "hello" receive {
            action(channel, "MrDestructoid Beep Boop")
        }

        "whoareyou" receive {
            sendMessage("I am bot created by wooodenleg TakeNRG To see available commands write '${commandMark}commands'. " +
                    "For more info write '${commandMark}details'")
        }

        "details" receive {
            sendMessage("I am written in Kotlin/JVM using TmiK library and ~750 lines of code long CoolCat." +
                    " I run 24/7 on Heroku server and save my data to MongoDB")
        }

        "howlong {username}" receive { parameters ->
            val username = parameters.getValue("username")
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

        "archive" {
            onReceive { sendMessage(
                "[Sub only] Archive message with '${commandMark}archive save {username} {name}'. (The 'name' is name of the quote) " +
                        "Show quote with '${commandMark}archive show {username} [name]'. " +
                        "Show all users quotes with '${commandMark}archive list {user}'."
            ) }

            "save {user} {name}" receive { parameters ->
                if (!isSubscriber)
                    sendMessage("This command is for subscribers only, sorry")
                else {
                    val user = parameters.getValue("user").toLowerCase()
                    val name = parameters.getValue("name")

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

            "show {user} [name]" receive { parameters ->
                val user = parameters.getValue("user").toLowerCase()
                val name = parameters["name"]

                sendMessage(
                    if (name != null) {
                        Database.Quotes.get(channel, user, name)?.toString()
                            ?: "I could not find $name by $user"
                    } else Database.Quotes.quoteList(channel, user).random().toString()
                )
            }

            "list {user}" receive { parameters ->
                val user = parameters.getValue("user").toLowerCase()
                val list = Database.Quotes.quoteList(channel, user)
                if (list.isEmpty())
                    sendMessage("Either user $user does not exist or he does not have any quotes saved")
                else sendMessage("/w ${message.username} ${list.map {
                    "${it.name} - \"${it.quote}\""
                }}")
            }
        }

        suspend fun getActivePoll(name: String) = Database.Poll.get(name)
        "poll" {
            onReceive { sendMessage(
                "Create poll with '${commandMark}poll create <options>' and stop it with '${commandMark}poll stop'. " +
                        "See active poll with '${commandMark}poll active'. " +
                        "If there is an active poll, you can vote for one of available options with '${commandMark}vote [option]'"
            ) }

            "create <options>" receive {
                val options = it.getValue("options").split(" ")
                val activePoll = getActivePoll(channel)

                if (isMod && activePoll == null) {
                    Database.Poll.create(channel, message.username, options)
                    sendMessage("Poll started! Write '${commandMark}vote {option}' to vote")
                }
                else if (!isMod && activePoll == null)
                    sendMessage("Only mods can create polls, sorry")
                else if (activePoll != null)
                    sendMessage("Another poll is already active")
            }

            "active" receive {
                val activePoll = getActivePoll(channel)
                if (activePoll == null)
                    sendMessage("There is no active poll")
                else
                    sendMessage("There is active poll by ${activePoll.author} with options: ${activePoll.options.joinToString(", ")}")
            }

            "stop" receive {
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
                if (success) sendMessage("/w ${message.username} Vote '${it.getValue("option")}' registered")
                else sendMessage("/w ${message.username} Error while registering vote '${it.getValue("option")}'. Make sure the option is spelled correctly")
            } else sendMessage("No poll is active right now")
        }
    }
}