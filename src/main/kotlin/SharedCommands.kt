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
