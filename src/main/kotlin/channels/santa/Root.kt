package channels.santa

import com.ktmi.tmi.dsl.builder.GlobalContextScope
import com.ktmi.tmi.dsl.builder.container
import com.ktmi.tmi.dsl.builder.scopes.ChannelScope
import com.ktmi.tmi.dsl.builder.scopes.channel
import com.ktmi.tmi.events.displayName
import helpers.Greet

fun GlobalContextScope.santa() = channel("sgtsantatv") {
    container {
        +Greet { lastSeenHours ->
            if (lastSeenHours == null)
                sendMessage("I've never seen you before $displayName TakeNRG Welcome!")
        }

        santaCommands()
    }
}