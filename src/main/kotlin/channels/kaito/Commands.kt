package channels.kaito

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import commandMark
import helpers.commands
import hugCommand
import unoCommand

fun TwitchScope.kaitoCommands() {

    commands(commandMark) {
        hugCommand()
        clapCommand()
        unoCommand()
    }
}