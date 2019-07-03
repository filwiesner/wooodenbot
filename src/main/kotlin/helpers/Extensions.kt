package helpers

import com.ktmi.tmi.messages.TextMessage

val TextMessage.isSubscriber get() = badges?.containsKey("subscriber") == true