package channels.sumkat

import com.ktmi.tmi.client.commands.action
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import commandMark
import database.Database
import database.now
import helpers.commands
import helpers.isSubscriber

fun ChannelScope.sumkatCommands() {

    commands(commandMark) {

        "commands" receive {
            reply("You can call me using following commands: \"hello, whoareyou, details, hug, ulthug, clap, slap, uno, sad, howlong, gn, srqueue, plug, hehe\" CoolStoryBob")
        }

        "hug [target]" receive { parameters ->
            val target = parameters["target"] ?: displayName
            reply("GivePLZ $target TakeNRG")
        }

        "ulthug [target]" receive { parameters ->
            if (isSubscriber) {
                val target = parameters["target"] ?: displayName
                reply("(･ω･)つ GivePLZ $target TakeNRG ⊂(･ω･)")
            } else reply("This command is for subscribers only")
        }

        "clap [target]" receive { parameters ->
            val target = parameters["target"]
            reply("${target ?: "This"} deserves my clapping. \uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F ")
        }

        "slap [target]" receive { parameters ->
            val target = parameters["target"]
            reply("${target ?: "Hey"}, stop being toxic! \uD83D\uDC4A KAPOW")
        }

        "uno [target]" receive { parameters ->
            val target = parameters["target"]
            if (target == null)
                reply("$displayName uses reverse UNO card")
            else
                reply("@$target $displayName uses reverse UNO card on you")
        }

        "sad [target]" receive { parameters ->
            val target = parameters["target"] ?: displayName
            reply("DepressoEspresso${target?.capitalize()}")
        }

        "howlong {username}" receive { parameters ->
            val username = parameters.getValue("username")
            val seen = Database.LastSeen.get(this@sumkatCommands.channel, username)
            if (seen != null) {
                reply("I saw $username ${(now - seen.timestamp) / 3600000} hours ago")
            } else reply("I've never seen $username in this chat")
        }

        "gn [target]" receive { parameters ->
            val target = parameters["target"] ?: displayName
            reply("Good night $target GivePLZ \uD83D\uDC9B")
        }

        "srqueue" receive {
            reply("You can see the song queue here: https://streamelements.com/sumkat/songrequest")
        }

        "plug" receive {
            if (isMod)
            action(this@sumkatCommands.channel, "Listen up! You can see this bitchboy® on youtube as well! \uD83D\uDC49 https://bit.ly/2XKY1FF Looking for some Twitter? Head to https://twitter.com/sumkat_stream")
        }

        "hehe" receive {
            reply("Kappa https://www.twitch.tv/sumkat/clip/EnchantingSpineyDogMcaT Kappa")
        }
    }
}