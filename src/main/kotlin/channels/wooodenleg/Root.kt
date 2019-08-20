package channels.wooodenleg

import com.ktmi.tmi.commands.join
import com.ktmi.tmi.commands.leave
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.builder.scopes.broadcaster
import com.ktmi.tmi.messages.asChannelName
import com.ktmi.tmi.messages.channelAsUsername
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
                sendMessage("Joined channel ${channel.channelAsUsername}")
            }

            "leave {channel}" receive {
                val channel = it.getValue("channel").asChannelName
                Database.Channels.remove(channel)
                leave(channel)
                sendMessage("Left channel ${channel.channelAsUsername}")
            }
        }
    }
}