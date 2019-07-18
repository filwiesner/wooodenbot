package database

import org.joda.time.DateTime
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

data class LastSeen(val username: String, val channel: String, val timestamp: Long)
data class Poll(val name: String, val author: String, val options: List<String>, val votes: MutableList<PollVote>)
data class PollVote(val author: String, val option: String)

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

    object Poll {
        private val collection = database.getCollection<database.Poll>()

        suspend fun create(name: String, author: String, options: List<String>): database.Poll? {
            collection.insertOne(Poll(name, author, options, arrayListOf()))
            return collection.findOne(database.Poll::name eq name)
        }

        suspend fun stop(name: String): database.Poll? =
            collection.findOneAndDelete(database.Poll::name eq name)

        suspend fun vote(pollName: String, author: String, option: String) {
            val new = collection.findOne(database.Poll::name eq pollName)?.let { poll ->
                if (poll.votes.any {it.author == author})
                    poll.votes.removeIf { it.author == author }

                if (poll.options.contains(option))
                    poll.votes.add(PollVote(author, option))

                poll.votes
            }
            if (new != null)
                collection.updateOne(
                    database.Poll::name eq pollName,
                    setValue(database.Poll::votes, new)
                )
        }
    }
}


val now get() = DateTime.now().millis