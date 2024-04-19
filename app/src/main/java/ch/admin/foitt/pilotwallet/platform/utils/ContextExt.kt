package ch.admin.foitt.pilotwallet.platform.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.onFailure
import timber.log.Timber

fun Context.openSecuritySettings() {
    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    if (intent.resolveActivity(packageManager) == null) {
        // Some phones do not have direct jump to security settings thus jump to settings
        intent.action = Settings.ACTION_SETTINGS
    }
    startActivity(intent)
}

fun Context.openAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        data = Uri.fromParts("package", this@openAppDetailsSettings.packageName, null)
    }
    if (intent.resolveActivity(this.packageManager) == null) {
        // Some phones do not have direct jump to app settings thus jump to settings
        intent.action = Settings.ACTION_SETTINGS
    }
    startActivity(intent)
}

fun Context.openLink(@StringRes uriResource: Int) {
    val link = getString(uriResource)
    openLink(link)
}

fun Context.openLink(uri: String) {
    runSuspendCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }.onFailure {
        Timber.w(it, "Could not open uri: $uri")
    }
}
