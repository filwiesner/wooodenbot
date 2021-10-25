package channels.wooodenleg

import clapCommand
import com.ktmi.tmi.dsl.builder.TwitchScope
import com.ktmi.tmi.dsl.builder.scopes.commands
import commandMark
import goodnightCommand
import hugCommand
import sadCommand

fun TwitchScope.wooodenlegCommands() {
    commands(commandMark) {
        hugCommand()
        clapCommand()
        sadCommand()
        goodnightCommand()
    }
}