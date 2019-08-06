import com.ktmi.tmi.dsl.plugins.Container
import com.ktmi.tmi.dsl.plugins.TwitchPlugin
import org.joda.time.DateTime

fun Container.ThrottleOut(interval: Long = 5_000) = object : TwitchPlugin {
    override val name = "throttle_out"

    private var lastSent = 0L

    override fun filterOutgoing(message: String): Boolean {
        val now = DateTime.now().millis

        return (now - lastSent > interval)
            .also { if (it) lastSent = now }
    }
}