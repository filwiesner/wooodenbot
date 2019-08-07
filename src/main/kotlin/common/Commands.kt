package common

import com.ktmi.tmi.client.commands.action
import com.ktmi.tmi.dsl.builder.MainScope
import commandMark
import database.Database
import database.now
import helpers.commands
import helpers.isMod

fun MainScope.commonCommands() {
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
            if (seen != null) {
                sendMessage("I saw $username ${(now - seen.timestamp) / 3600000} hours ago")
            } else sendMessage("I've never seen $username in this chat")
        }

        suspend fun getActivePoll(name: String) = Database.Poll.get(name)
        "poll" {
            onReceive { sendMessage(
                "Create poll with '${commandMark}poll create <options>' and stop it with '${commandMark}poll stop'. " +
                        "See active poll with '${commandMark}poll active. " +
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