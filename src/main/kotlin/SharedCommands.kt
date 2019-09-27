import helpers.CommandScope
import helpers.displayName

fun CommandScope.hugCommand() {
    "hug [target]" receive { parameters ->
        val target = parameters["target"] ?: displayName
        sendMessage("GivePLZ $target TakeNRG")
    }
}

fun CommandScope.clapCommand() {
    "clap [target]" receive { parameters ->
        val target = parameters["target"]
        sendMessage("${target ?: "This"} deserves my clapping. \uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F\uD83D\uDC4F ")
    }
}

fun CommandScope.unoCommand() {
    "uno [target]" receive { parameters ->
        val target = parameters["target"]
        if (target == null)
            sendMessage("$displayName uses reverse UNO card")
        else
            sendMessage("@$target $displayName uses reverse UNO card on you")
    }
}

fun CommandScope.slapCommand() {
    "slap [target]" receive { parameters ->
        val target = parameters["target"]
        sendMessage("${target ?: "Hey"}, stop being toxic! \uD83D\uDC4A KAPOW")
    }
}

fun CommandScope.sadCommand() {
    "sad [target]" receive { parameters ->
        val target = parameters["target"] ?: displayName
        sendMessage("DepressoEspresso${target?.capitalize()}")
    }
}

fun CommandScope.goodnightCommand() {
    "gn [target]" receive { parameters ->
        val target = parameters["target"] ?: displayName
        sendMessage("Good night $target GivePLZ \uD83D\uDC9B")
    }
}

fun CommandScope.cookieCommand() {
    "cookie" receive { sendMessage("GivePLZ \uD83C\uDF6A") }
}
