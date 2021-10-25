package channels.kaito

import com.ktmi.tmi.dsl.builder.GlobalContextScope
import com.ktmi.tmi.dsl.builder.container
import com.ktmi.tmi.dsl.builder.scopes.channel
import helpers.Greet
import knownUsers

fun GlobalContextScope.kaito() = channel("pkmntrainerkaito") {
    container {
        + Greet(customMessages = knownUsers)

        kaitoCommands()
    }
}