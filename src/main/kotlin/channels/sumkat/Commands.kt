package channels.sumkat

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import com.ktmi.tmi.events.displayName
import com.ktmi.tmi.events.isMod
import com.ktmi.tmi.events.isSubscriber
import commandMark
import cookieCommand
import goodnightCommand
import hugCommand
import sadCommand
import slapCommand
import unoCommand
import foodCommand
import kotlin.random.Random

fun TwitchScope.sumkatCommands() {

    commands(commandMark) {
        hugCommand()
        clapCommand()
        unoCommand()
        slapCommand()
        sadCommand()
        goodnightCommand()
        cookieCommand()
        foodCommand()

        "commands" receive {
            sendMessage("You can call me using following commands: \"hello, whoareyou, details, hug, ulthug, clap, slap, uno, sad, howlong, gn, srqueue, plug, hehe, cheerup, feels, poll, food \" CoolStoryBob")
        }

        "ulthug [target]" receive { parameters ->
            if (isSubscriber) {
                val target = parameters["target"] ?: displayName
                sendMessage("(･ω･)つ GivePLZ $target TakeNRG ⊂(･ω･)")
            } else sendMessage("This command is for subscribers only")
        }

        "|srqueue,sq|" receive {
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