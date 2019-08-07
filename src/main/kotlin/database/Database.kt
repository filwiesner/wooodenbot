package database

import org.joda.time.DateTime
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

data class LastSeenEntry(val username: String, val channel: String, val timestamp: Long)

data class PollEntry(val name: String, val author: String, val options: List<String>, val votes: MutableList<PollVoteEntry>)
data class PollVoteEntry(val author: String, val option: String)
data class ChannelEntry(val channelName: String)

object Database {
    private val dbUri = System.getenv("MONGODB_URI")
    private val client = KMongo.createClient(dbUri).coroutine
    private val database = client.getDatabase("heroku_b4wj1mdn")

    object Channels {
        private val collection = database.getCollection<ChannelEntry>()

        suspend fun get(): List<String> = collection
            .find().toList()
            .map(ChannelEntry::channelName)

        suspend fun add(name: String) {
            collection.insertOne(ChannelEntry(name))
        }

        suspend fun remove(name: String): Boolean {
            val res = collection.deleteOne(ChannelEntry::channelName eq name)
            return res.deletedCount > 0
        }
    }

    object LastSeen {
        private val collection = database.getCollection<LastSeenEntry>()

        suspend fun get(channel: String, username: String) =
            collection.findOne(
                LastSeenEntry::channel eq channel,
                LastSeenEntry::username eq username
            )

        suspend fun set(channel: String, username: String) {
            collection.insertOne(LastSeenEntry(username, channel, now))
        }

        suspend fun update(channel: String, username: String) {
            collection.updateOne(
                and(LastSeenEntry::channel eq channel, LastSeenEntry::username eq username),
                setValue(LastSeenEntry::timestamp, now)
            )
        }
    }

    object Poll {
        private val collection = database.getCollection<PollEntry>()

        suspend fun create(name: String, author: String, options: List<String>): PollEntry? {
            collection.insertOne(PollEntry(name, author, options, arrayListOf()))
            return collection.findOne(PollEntry::name eq name)
        }

        suspend fun stop(name: String): PollEntry? =
            collection.findOneAndDelete(PollEntry::name eq name)

        suspend fun vote(pollName: String, author: String, option: String): Boolean {
            val new = collection.findOne(PollEntry::name eq pollName)?.let { poll ->
                if (poll.votes.any {it.author == author})
                    poll.votes.removeIf { it.author == author }

                if (poll.options.contains(option))
                    poll.votes.add(PollVoteEntry(author, option))
                else return false

                poll.votes
            }

            if (new != null)
                collection.updateOne(
                    PollEntry::name eq pollName,
                    setValue(PollEntry::votes, new)
                )
            else return false

            return true
        }

        suspend fun get(name: String): PollEntry? =
            collection.findOne(PollEntry::name eq name)

    }
}


val now get() = DateTime.now().millis