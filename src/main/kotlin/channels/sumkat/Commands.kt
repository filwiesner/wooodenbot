package channels.sumkat

import com.ktmi.tmi.client.commands.action
import com.ktmi.tmi.client.commands.followOnly
import com.ktmi.tmi.client.commands.subOnly
import com.ktmi.tmi.client.commands.timeout
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import commandMark
import database.Database
import database.now
import helpers.commands
import helpers.isSubscriber
import kotlin.random.Random

fun ChannelScope.sumkatCommands() {

    commands(commandMark) {

        "commands" receive {
            reply("You can call me using following commands: \"hello, whoareyou, details, hug, ulthug, clap, slap, uno, sad, howlong, gn, srqueue, plug, hehe, cheerup, feels   \" CoolStoryBob")
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

        "cheerup {username} [index]" receive { parameters ->
            val username = parameters["username"]!!
            val index = parameters["index"]?.toIntOrNull() ?: Random.nextInt(0, cheers.size)

            if (index >= cheers.size)
                reply("The max index is ${cheers.size - 1}")
            else
                reply("$username ${cheers[index]} \uD83D\uDC9B TakeNRG [$index]")
        }

        "feels {username} [index]" receive { parameters ->
            val username = parameters["username"]!!
            val index = parameters["index"]?.toIntOrNull() ?: Random.nextInt(0, feels.size)

            if (index >= feels.size)
                reply("The max index is ${feels.size - 1}")
            else
                reply("$username ${feels[index]} \uD83D\uDC9B TakeNRG [$index]")
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