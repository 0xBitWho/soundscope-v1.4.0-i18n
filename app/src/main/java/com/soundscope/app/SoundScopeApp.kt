package com.soundscope.app

import android.app.Application
import com.soundscope.app.data.AppDatabase

class SoundScopeApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
