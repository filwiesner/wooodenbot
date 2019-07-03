package common

import ThrottleOut
import com.ktmi.tmi.dsl.builder.MainScope
import com.ktmi.tmi.dsl.plugins.container
import commandMark
import helpers.textMessage

fun MainScope.commonLogic() {

    commonSocial()
    commonCommands()
}