package com.mzielu.storeissue.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.*
import com.mzielu.storeissue.MyTestApp
import com.mzielu.storeissue.ui.base.BaseAndroidViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(app: Application) : BaseAndroidViewModel(app) {

    private val store = (app as MyTestApp).roomStore

    val refreshCounter = MutableStateFlow(0)
    val collectedEventsCounter = MutableStateFlow(0)
    val state = MutableStateFlow("Init")

    init {
        Timber.d("Start observing for events")
        viewModelScope.launch {
            store.stream(StoreRequest.cached(key = TEST_KEY, refresh = true)).collect { storeResponse ->
                Timber.d("New event from stream: $storeResponse")
                collectedEventsCounter.update { it + 1 }
                state.value = when (storeResponse) {
                    is StoreResponse.Data -> "Data loaded successfully"
                    is StoreResponse.Loading -> "Loading..."
                    is StoreResponse.NoNewData -> "No new data loaded"
                    is StoreResponse.Error.Exception -> "Error with exception"
                    is StoreResponse.Error.Message -> "Error with message"
                }
            }
        }
    }

    fun refresh() {
        Timber.d("Refresh button clicked")
        viewModelScope.launch {
            refreshCounter.update { it + 1 }
            state.value = "Refreshing..."
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