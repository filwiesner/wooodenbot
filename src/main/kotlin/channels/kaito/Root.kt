package channels.kaito

import com.ktmi.tmi.dsl.builder.GlobalContextScope
import com.ktmi.tmi.dsl.builder.container
import com.ktmi.tmi.dsl.builder.scopes.channel
import helpers.Greet
import knownUsers

fun GlobalContextScope.kaito() = channel("sumkaito") {
    container {
        + Greet(customMessages = knownUsers)

        kaitoCommands()
    }
}