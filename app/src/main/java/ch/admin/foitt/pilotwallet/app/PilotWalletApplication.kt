package ch.admin.foitt.pilotwallet.app

import android.app.Application
import android.util.Log
import ch.admin.foitt.pilotwallet.BuildConfig
import ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase.ReportError
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class PilotWalletApplication : Application() {
    @Inject lateinit var reportError: ReportError

    override fun onCreate() {
        super.onCreate()
        setupLogging()
    }

    private fun setupLogging() {
        val trees = mutableListOf<Timber.Tree>(
            // Dynatrace tree
            object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    when (priority) {
                        Log.ERROR, Log.WARN -> reportError(message, t)
                    }
                }
            }
        )

        // debug log tree
        if (BuildConfig.DEBUG) {
            trees.add(
                object : Timber.DebugTree() {
                    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                        super.log(priority, "sw_$tag", message, t)
                    }
                }
            )
        }

        trees.forEach { tree ->
            tree
                .takeIf { it !in Timber.forest() }
                ?.apply { Timber.plant(this) }
        }
    }
}
