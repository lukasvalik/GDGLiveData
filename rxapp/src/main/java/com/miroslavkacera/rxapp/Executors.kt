/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.miroslavkacera.rxapp

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */

lateinit var instance: Executors

open class Executors private constructor(
        private val diskIO: Executor,
        private val networkIO: Executor,
        private val mainThread: Executor) {

    companion object {
        fun getInstance(diskIO: Executor = java.util.concurrent.Executors.newSingleThreadExecutor(),
                        networkIO: Executor = java.util.concurrent.Executors.newFixedThreadPool(3),
                        mainThread: Executor = MainThreadExecutor()): Executors {
            if (!::instance.isInitialized ||
                    instance.diskIO != diskIO ||
                    instance.networkIO != networkIO ||
                    instance.mainThread != mainThread) {
                instance = Executors(diskIO, networkIO, mainThread)
            }

            return instance
        }
    }

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO(): Executor {
        return networkIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}