package com.mzielu.storeissue

import android.app.Application
import com.dropbox.android.external.store4.Store
import com.mzielu.storeissue.fromstore.Graph
import com.mzielu.storeissue.fromstore.Post
import timber.log.Timber

class MyTestApp : Application() {
    lateinit var roomStore: Store<String, List<Post>>

    override fun onCreate() {
        super.onCreate()
        roomStore = Graph.provideStore(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}