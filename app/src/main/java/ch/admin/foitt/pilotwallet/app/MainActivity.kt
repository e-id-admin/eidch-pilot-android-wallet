package ch.admin.foitt.pilotwallet.app

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ch.admin.foitt.pilotwallet.app.presentation.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val timeStamp = SystemClock.elapsedRealtime()
        splashScreen.setKeepOnScreenCondition {
            SystemClock.elapsedRealtime() - timeStamp < SPLASH_SCREEN_DURATION
        }
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.setRecentsScreenshotEnabled(false)
        }
        setContent {
            MainScreen(this)
        }
    }

    companion object {
        private const val SPLASH_SCREEN_DURATION = 2000
    }
}
