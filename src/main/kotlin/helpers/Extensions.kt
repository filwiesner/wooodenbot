package helpers

import com.ktmi.tmi.client.events.UserContext
import com.ktmi.tmi.messages.TextMessage

val UserContext<TextMessage>.isSubscriber get() = message.badges?.containsKey("subscriber") == true
val UserContext<TextMessage>.displayName get() = message.displayName
val UserContext<TextMessage>.isMod get() = message.isMod