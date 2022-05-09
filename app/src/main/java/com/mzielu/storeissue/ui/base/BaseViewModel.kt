package com.mzielu.storeissue.ui.base

import android.app.Application
import androidx.annotation.CallSuper
import androidx.lifecycle.AndroidViewModel

abstract class BaseAndroidViewModel constructor(app: Application) : AndroidViewModel(app) {
    @CallSuper
    open fun start() {
    }

    @CallSuper
    open fun stop() {
    }
}
