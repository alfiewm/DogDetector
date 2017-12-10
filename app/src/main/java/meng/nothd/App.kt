package meng.nothd

import android.app.Application
import timber.log.Timber

/**
 * Created by meng on 2017/12/5.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        val PREFS_NAME = "NotHotDogPrefs"
        val AUTH_TOKEN_KEY = "AuthToken"
    }
}