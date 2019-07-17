package channels.sumkat

import com.ktmi.tmi.dsl.builder.TwitchScope
import commandMark
import database.Database
import database.Poll
import database.now
import helpers.commands
import helpers.displayName
import helpers.isMod
import helpers.isSubscriber
import kotlin.random.Random

fun TwitchScope.sumkatCommands() {

    commands(commandMark) {

        "commands" receive {
            sendMessage("You can call me using following commands: \"hello, whoareyou, details, hug, ulthug, clap, slap, uno, sad, howlong, gn, srqueue, plug, hehe, cheerup, feels, poll \" CoolStoryBob")
        }

        "hug [target]" receive { parameters ->
            val target = parameters["target"] ?: displayName
            sendMessage("GivePLZ $target TakeNRG")
        }

        "ulthug [target]" receive { parameters ->
            if (isSubscriber) {
                val target = parameters["target"] ?: displayName
                sendMessage("(･ω･)つ GivePLZ $target TakeNRG ⊂(･ω･)")
            } else sendMessage("This command is for subscribers only")
        }

        "clap [target]" receive { parameters ->
            val target = parameters["target"]
            sendMessage("${target ?: "This"} deserves my clapping. \uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F ")
        }

        "slap [target]" receive { parameters ->
            val target = parameters["target"]
            sendMessage("${target ?: "Hey"}, stop being toxic! \uD83D\uDC4A KAPOW")
        }

        "uno [target]" receive { parameters ->
            val target = parameters["target"]
            if (target == null)
                sendMessage("$displayName uses reverse UNO card")
            else
                sendMessage("@$target $displayName uses reverse UNO card on you")
        }

        "sad [target]" receive { parameters ->
            val target = parameters["target"] ?: displayName
            sendMessage("DepressoEspresso${target?.capitalize()}")
        }

        "howlong {username}" receive { parameters ->
            val username = parameters.getValue("username")
            val seen = Database.LastSeen.get(channel, username)
            if (seen != null) {
                sendMessage("I saw $username ${(now - seen.timestamp) / 3600000} hours ago")
            } else sendMessage("I've never seen $username in this chat")
        }

        "gn [target]" receive { parameters ->
            val target = parameters["target"] ?: displayName
            sendMessage("Good night $target GivePLZ \uD83D\uDC9B")
        }

        "srqueue" receive {
            sendMessage("You can see the song queue here: https://streamelements.com/sumkat/songrequest")
        }

        "plug" receive {
            if (isMod) action("Listen up! You can see this bitchboy® on youtube as well! \uD83D\uDC49 https://bit.ly/2XKY1FF Looking for some Twitter? Head to https://twitter.com/sumkat_stream")
        }

        "hehe" receive {
            sendMessage("Kappa https://www.twitch.tv/sumkat/clip/EnchantingSpineyDogMcaT Kappa")
        }

        "cheerup {username} [index]" receive { parameters ->
            val username = parameters["username"]!!
            val index = parameters["index"]?.toIntOrNull() ?: Random.nextInt(0, cheers.size)

            if (index >= cheers.size)
                sendMessage("The max index is ${cheers.size - 1}")
            else
                sendMessage("$username ${cheers[index]} \uD83D\uDC9B TakeNRG [$index]")
        }

        "feels {username} [index]" receive { parameters ->
            val username = parameters["username"]!!
            val index = parameters["index"]?.toIntOrNull() ?: Random.nextInt(0, feels.size)

            if (index >= feels.size)
                sendMessage("The max index is ${feels.size - 1}")
            else
                sendMessage("$username ${feels[index]} \uD83D\uDC9B TakeNRG [$index]")
        }

        var activePoll: Poll? = null
        "poll" {
            onReceive { sendMessage("Create poll with '${commandMark}poll create <options>' and stop it with '${commandMark}poll stop'") }
            
            "create <options>" receive {
                val options = it.getValue("options").split(" ")

                if (isMod && activePoll == null) {
                    activePoll = Database.Poll.create(channel, message.username, options)
                    sendMessage("Poll started! Write '${commandMark}vote {option}' to vote")
                }
                else if (!isMod)
                    sendMessage("Only mods can create polls, sorry")
                else if (activePoll != null)
                    sendMessage("Another poll is already active")
            }

            "active" receive {
                if (activePoll == null)
                    sendMessage("There is no active poll")
                else
                    sendMessage("There is active poll by ${activePoll!!.author} with options: ${activePoll!!.options.joinToString { ", " }}")
            }

            "stop" receive {
                if (activePoll != null) {
                    val res = Database.Poll.stop(activePoll!!.name) ?: return@receive
                    val result = res.options.map { option ->
                        option to res.votes.filter { it.option == option }.size
                    }
                    sendMessage("Results: ${result.joinToString { (name, count) -> "$name: $count" }}")
                    activePoll = null
                } else
                    sendMessage("No poll is active right now")
            }
        }

        "vote {option}" receive {
            if (activePoll != null)
                Database.Poll.vote(channel, message.username, it.getValue("option"))
            else sendMessage("No poll is active right now")
        }
    }
}

val cheers = arrayOf(
    "Here, something to cheer you up GivePLZ https://www.youtube.com/watch?v=HiLWCf2MPHQ",
    "Oh, you feeling bad? :( Think of your favourite animal sleeping! Isn't it nice?",
    "Look at this bad boooy GivePLZ https://www.youtube.com/watch?v=c31JT-9i-eI TakeNRG He will cheer you up",
    "You know what I'm doing? Keeping my fingers crossed for you!",
    "Roses are red, violets aren't blue, world isn't so bad, 'cause this chat appreciates you!",
    "Snails are cuuuute! https://youtu.be/vSxITYkF-YA"
)
val feels = arrayOf(
    "Listen to this my friend \uD83D\uDC49 https://soundcloud.com/matejhlozanek/zvir",
    "Listen to this my friend \uD83D\uDC49 https://www.youtube.com/watch?v=8wE8eOCMu7A",
    "Listen to this my friend \uD83D\uDC49 https://www.youtube.com/watch?v=wbqrd30vQnU",
    "Listen to this my friend \uD83D\uDC49 https://www.youtube.com/watch?v=H9c42KyVCw0",
    "Listen to this my friend \uD83D\uDC49 https://www.youtube.com/watch?v=AuFiBjNTB9o",
    "Listen to this my friend \uD83D\uDC49 https://www.youtube.com/watch?v=CvFH_6DNRCY"
)