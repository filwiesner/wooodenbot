package channels.santa

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import commandMark
import cookieCommand
import goodnightCommand
import hugCommand
import sadCommand
import slapCommand
import unoCommand
import foodCommand

fun TwitchScope.santaCommands() {

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
            sendMessage("You can call me using following commands: \"hello, whoareyou, details, hug, clap, slap, uno, sad, howlong, gn, poll, food \" CoolStoryBob")
        }
    }
}