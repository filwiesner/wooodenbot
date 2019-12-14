package channels.wooodenleg

import com.ktmi.tmi.dsl.builder.scopes.ChannelScope

fun ChannelScope.wooodenleg() {
    wooodenlegAdministration()
    wooodenlegCommands()
    wooodenlegSocial()
}