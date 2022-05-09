package com.mzielu.storeissue.fromstore

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.TypeConverter
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Dao
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//file copied from Store Dropbox library example
@Entity(
    primaryKeys = ["subredditName", "postOrder", "postId"],
    indices = [Index("postId", unique = false)]
)
data class FeedEntity(
    val subredditName: String,
    val postOrder: Int,
    val postId: String
)

// this wrapper usually doesn't make sense but we do here to avoid leaking room into the Model file
@Entity(
    primaryKeys = ["id"]
)
data class PostEntity(
    @Embedded
    val post: Post
)

class RedditTypeConverters {
    @TypeConverter
    fun previewToString(preview: Preview?) = preview?.let {
        Json.encodeToString(preview)
    }

    @TypeConverter
    fun stringToPreview(preview: String?) = preview?.let {
        Json.decodeFromString<Preview>(preview)
    }
}

@Dao
abstract class PostDao {
    @Transaction
    open suspend fun insertPosts(subredditName: String, posts: List<Post>) {
        // first clear the feed
        clearFeedBySubredditName(subredditName)
        // convert them into database models
        val feedEntities = posts.mapIndexed { index: Int, post: Post ->
            FeedEntity(
                subredditName = subredditName,
                postOrder = index,
                postId = post.id
            )
        }
        val postEntities = posts.map {
            PostEntity(it)
        }
        // save them into the database
        insertPosts(feedEntities, postEntities)
        // delete posts that are not part of any feed
        clearObseletePosts()
    }

    @Query("DELETE FROM FeedEntity WHERE subredditName = :subredditName")
    abstract suspend fun clearFeedBySubredditName(subredditName: String)

    @Query("DELETE FROM FeedEntity")
    abstract suspend fun clearAllFeeds()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPosts(
        feedEntries: List<FeedEntity>,
        posts: List<PostEntity>
    )

    @Query("DELETE FROM PostEntity WHERE id NOT IN (SELECT DISTINCT(postId) FROM FeedEntity)")
    protected abstract suspend fun clearObseletePosts()

    @Query(
        """
        SELECT PostEntity.* FROM FeedEntity
            LEFT JOIN PostEntity ON FeedEntity.postId = PostEntity.id
            WHERE subredditName = :subredditName
            ORDER BY FeedEntity.postOrder ASC
        """
    )
    abstract fun loadPosts(subredditName: String): Flow<List<Post>>
}

@Database(
    version = 1,
    exportSchema = false,
    entities = [PostEntity::class, FeedEntity::class]
)
@TypeConverters(RedditTypeConverters::class)
abstract class RedditDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}
