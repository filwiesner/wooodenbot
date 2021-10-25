package channels.wooodenleg

import com.ktmi.tmi.dsl.builder.GlobalContextScope
import com.ktmi.tmi.dsl.builder.container
import com.ktmi.tmi.dsl.builder.scopes.channel
import helpers.Greet
import knownUsers

fun GlobalContextScope.wooodenleg() = channel("wooodenleg") {
    container {
        + Greet(customMessages = knownUsers)

        wooodenlegAdministration()
        wooodenlegCommands()
        wooodenlegSocial()
    }
}
