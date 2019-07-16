package database

import org.joda.time.DateTime
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

data class LastSeen(val username: String, val channel: String, val timestamp: Long)

object Database {
    private val dbUri = System.getenv("MONGODB_URI")
    private val client = KMongo.createClient(dbUri).coroutine
    private val database = client.getDatabase("heroku_b4wj1mdn")

    object LastSeen {
        private val collection = database.getCollection<database.LastSeen>()

        suspend fun get(channel: String, username: String) =
            collection.findOne(
                database.LastSeen::channel eq channel,
                database.LastSeen::username eq username
            )

        suspend fun set(channel: String, username: String) {
            collection.insertOne(LastSeen(username, channel, now))
        }

        suspend fun update(channel: String, username: String) {
            collection.updateOne(
                and(database.LastSeen::channel eq channel, database.LastSeen::username eq username),
                setValue(database.LastSeen::timestamp, now)
            )
        }
    }
}


val now get() = DateTime.now().millis