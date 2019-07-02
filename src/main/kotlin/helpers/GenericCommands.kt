package helpers

//class RequestContext(
//    private val client: TmiClient,
//    val channel: String,
//    val context: UserState,
//    val message: String
//) {
//    fun say(msg: String) = client.say(channel, msg)
//    fun badSyntax(msg: String) = client.badSyntax(channel, msg)
//}
//
//interface CommandReceiver {
//    val commandName: String
//    fun RequestContext.onCommand()
//}
//
//class CommandObservable(vararg commands: CommandReceiver) {
//    val commandRecievers = mutableMapOf<String, CommandReceiver>().apply {
//        putAll(commands.map { it.commandName to it })
//    }
//
//    fun TmiClient.onCommand(channel: String, context: UserState, cmd: String) {
//        val command = cmd.trim().split(' ')
//        val message =
//            if (command.size == 1) ""
//            else cmd.substring(command[0].length).trim()
//
//        commandRecievers[command[0]]?.let {
//            RequestContext(this, channel, context,
//                message
//            ).apply{ it.apply { onCommand() } }
//        }
//    }
//}
//
//class TargetCommand(
//    override val commandName: String,
//    private val syntaxMessage: String = "$commandName <target>",
//    private val optionalTarget: Boolean = false,
//    private val overrideBadSyntax: Boolean = false,
//    val onTargetCommand: RequestContext.(String?) -> Unit
//) : CommandReceiver {
//
//    override fun RequestContext.onCommand() {
//        if (message.isBlank() && optionalTarget)
//            onTargetCommand(null)
//        else {
//            val parts = message.split(' ')
//
//            if (parts.size == 1 && message.isNotBlank())
//                onTargetCommand(parts[0])
//            else {
//                if (overrideBadSyntax) say(syntaxMessage)
//                else badSyntax(syntaxMessage)
//            }
//        }
//    }
//}