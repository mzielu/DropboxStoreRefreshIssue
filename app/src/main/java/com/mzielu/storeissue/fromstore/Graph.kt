package com.mzielu.storeissue.fromstore

import android.text.Html
import androidx.room.Room
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mzielu.storeissue.MyTestApp
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

//file copied from Store Dropbox library example
object Graph {
    private val serializer = Json { ignoreUnknownKeys = true }

    private fun provideRetrofit(): Api {
        return Retrofit.Builder()
            .baseUrl("https://reddit.com/")
            .addConverterFactory(serializer.asConverterFactory(contentType = "application/json".toMediaType()))
            .build()
            .create(Api::class.java)
    }

    private fun provideRoom(context: MyTestApp): RedditDb {
        return Room.inMemoryDatabaseBuilder(context, RedditDb::class.java)
            .build()
    }

    fun provideStore(context: MyTestApp): Store<String, List<Post>> {
        val db = provideRoom(context)
        return StoreBuilder
            .from(
                Fetcher.of {
                    provideRetrofit().fetchSubreddit("anything", 10).data.children.map(::toPosts)
                },
                sourceOfTruth = SourceOfTruth.of(
                    reader = db.postDao()::loadPosts,
                    writer = db.postDao()::insertPosts,
                    delete = db.postDao()::clearFeedBySubredditName,
                    deleteAll = db.postDao()::clearAllFeeds
                )
            )
            .build()
    }

    private fun toPosts(it: Children): Post {
        return it.data.copy(
            preview = it.data.preview?.let {
                it.copy(
                    images = it.images.map { image ->
                        @Suppress("DEPRECATION")
                        image.copy(
                            source = image.source.copy(
                                url = Html.fromHtml(image.source.url).toString()
                            )
                        )
                    }
                )
            }
        )
    }
}