package channels.qwearty

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import commandMark
import goodnightCommand
import hugCommand
import unoCommand

fun TwitchScope.qweartyCommands() {

    commands(commandMark) {
        hugCommand()
        clapCommand()
        unoCommand()
        goodnightCommand()

        "commands" receive {
            sendMessage("You can call me using following commands: \"hug, clap, uno, gn \" CoolStoryBob")
        }
    }
}