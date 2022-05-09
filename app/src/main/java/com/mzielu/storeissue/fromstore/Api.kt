package com.mzielu.storeissue.fromstore

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//file copied from Store Dropbox library example
interface Api {

    @GET("r/{subredditName}/new/.json")
    suspend fun fetchSubreddit(
        @Path("subredditName") subredditName: String,
        @Query("limit") limit: Int
    ): RedditData

    @GET("r/{subredditName}/new/.json")
    suspend fun fetchSubredditForPersister(
        @Path("subredditName") subredditName: String,
        @Query("limit") limit: Int
    ): ResponseBody
}
