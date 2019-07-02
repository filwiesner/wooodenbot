package common

import com.ktmi.tmi.client.commands.sendMessage
import com.ktmi.tmi.client.events.onMessage
import com.ktmi.tmi.dsl.builder.MainScope

fun MainScope.commonLogic() {

    onMessage { message ->
        if (message.message.let { it.contains("hello") && it.contains(username) })
            sendMessage(message.channel, "Hello ${message.username}")
    }
}