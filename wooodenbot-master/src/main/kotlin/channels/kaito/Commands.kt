package channels.kaito

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import commandMark
import helpers.commands
import hugCommand
import unoCommand
import foodCommand

fun TwitchScope.kaitoCommands() {

    commands(commandMark) {
        hugCommand()
        clapCommand()
        unoCommand()
        foodCommand()
    }
}