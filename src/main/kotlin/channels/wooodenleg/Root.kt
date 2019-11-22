package channels.wooodenleg

import com.ktmi.tmi.commands.join
import com.ktmi.tmi.commands.leave
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.builder.scopes.broadcaster
import com.ktmi.tmi.dsl.builder.scopes.commands
import com.ktmi.tmi.messages.asChannelName
import com.ktmi.tmi.messages.channelAsUsername
import commandMark
import database.Database

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

            "changename {original} {new}" receive {
                val origName = it.getValue("original")
                val newName = it.getValue("new")

                if (Database.Channels.get().contains("#$origName")) {
                    Database.Channels.renameChannel(origName, newName)
                    Database.LastSeen.renameChannel(origName, newName)
                    Database.Quotes.renameChannel(origName, newName)
                    Database.Message.renameChannel(origName, newName)

                    leave(origName)
                    join(newName)
                    sendMessage("Done CorgiDerp")
                } else sendMessage("No channel with name $origName")
            }
        }
    }
}