package channels.kaito

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import commandMark
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