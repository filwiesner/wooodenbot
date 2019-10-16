package channels.kaito

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import commandMark
import hugCommand
import unoCommand
import foodCommand
import goodnightCommand

fun TwitchScope.kaitoCommands() {

    commands(commandMark) {
        hugCommand()
        clapCommand()
        unoCommand()
        foodCommand()
        goodnightCommand()

        "commands" receive {
            sendMessage("You can call me using following commands: \"hug, clap, uno, sad, gn, food, treat \" CoolStoryBob")
        }

        "treat {value}" receive { parameters ->
            when(val treat = parameters.getValue("value")) {
                "cat" -> sendMessage("The Cat shall get a Treat! \uD83D\uDC40")
                "dog" -> sendMessage("The Dogs Shall get a Treat! \uD83D\uDC40")
                else -> sendMessage("No I don't have a $treat \uD83D\uDC40")
            }
        }
    }
}