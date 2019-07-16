package common

import com.ktmi.tmi.client.commands.action
import com.ktmi.tmi.dsl.builder.MainScope
import commandMark
import helpers.commands

fun MainScope.commonCommands() {
    commands(commandMark) {
        "hello" receive {
            action(channel, "MrDestructoid Beep Boop")
        }

        "whoareyou" receive {
            sendMessage("I am bot created by wooodenleg TakeNRG To see available commands write '${commandMark}commands'. " +
                    "For more info write '${commandMark}details'")
        }

        "details" receive {
            sendMessage("I am written in Kotlin/JVM using TmiK library and ~700 lines of code long CoolCat." +
                    " I run 24/7 on Heroku server and save my data to MongoDB")
        }


    }
}