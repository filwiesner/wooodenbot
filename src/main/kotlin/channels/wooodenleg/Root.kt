package channels.wooodenleg

import com.ktmi.tmi.client.commands.join
import com.ktmi.tmi.client.commands.leave
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.builder.scopes.broadcaster
import com.ktmi.tmi.messages.asChannelName
import commandMark
import database.Database
import helpers.commands

fun ChannelScope.wooodenleg() {
    broadcaster {
        commands(commandMark) {

            "join {channel}" receive {
                val channel = it.getValue("channel").asChannelName
                Database.Channels.add(channel)
                join(channel)
            }

            "leave {channel}" receive {
                val channel = it.getValue("channel").asChannelName
                Database.Channels.remove(channel)
                leave(channel)
            }

        }
    }
}