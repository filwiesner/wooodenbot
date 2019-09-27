package channels.santa

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import commandMark
import cookieCommand
import goodnightCommand
import helpers.commands
import hugCommand
import sadCommand
import slapCommand
import unoCommand

fun TwitchScope.santaCommands() {

    commands(commandMark) {
        hugCommand()
        clapCommand()
        unoCommand()
        slapCommand()
        sadCommand()
        goodnightCommand()
        cookieCommand()

        "commands" receive {
            sendMessage("You can call me using following commands: \"hello, whoareyou, details, hug, clap, slap, uno, sad, howlong, gn, poll \" CoolStoryBob")
        }
    }
}