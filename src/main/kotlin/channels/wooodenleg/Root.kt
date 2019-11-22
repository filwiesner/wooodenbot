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
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

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
                val origName = it.getValue("original").asChannelName
                val newName = it.getValue("new").asChannelName

                if (Database.Channels.get().contains(origName)) {
                    sendMessage("Sure thing but it may take a while")
                    val length = measureTimeMillis {
                        Database.Channels.renameChannel(origName, newName)
                        Database.LastSeen.renameChannel(origName, newName)
                        Database.Quotes.renameChannel(origName, newName)
                        Database.Message.renameChannel(origName, newName)

                        leave(origName)
                        join(newName)
                    }

                    sendMessage("Done CorgiDerp It took only ${length / 1000} seconds")
                } else sendMessage("No channel with name ${origName.channelAsUsername}")
            }
        }
    }
}