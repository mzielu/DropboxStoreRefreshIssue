package com.mzielu.storeissue.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.fresh
import com.mzielu.storeissue.MyTestApp
import com.mzielu.storeissue.ui.base.BaseAndroidViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(app: Application) : BaseAndroidViewModel(app) {

    private val store = (app as MyTestApp).roomStore

    init {
        Timber.d("Start observing for events")
        viewModelScope.launch {
            store.stream(StoreRequest.cached(key = TEST_KEY, refresh = true)).collect {
                Timber.d("New event from stream: $it")
            }
        }
    }

    fun refresh() {
        Timber.d("Refresh button clicked")
        viewModelScope.launch {
            try {
                store.fresh(TEST_KEY)
            } catch (e: Throwable) {
                //Logging message is not crucial for this test example
            }
        }
    }

    companion object {
        private const val TEST_KEY = "key_test"
    }
}