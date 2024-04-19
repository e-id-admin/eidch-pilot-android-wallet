package ch.admin.foitt.pilotwallet.feature.qrscan.presentation.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import ch.admin.foitt.pilotwallet.platform.scaffold.presentation.ScreenViewModel

private val ScreenViewModel.cameraPermission by lazy { Manifest.permission.CAMERA }

internal fun ScreenViewModel.hasCameraPermission(appContext: Context): Boolean =
    ActivityCompat.checkSelfPermission(appContext, cameraPermission) == PackageManager.PERMISSION_GRANTED

internal fun ScreenViewModel.shouldShowRationale(activity: FragmentActivity): Boolean =
    ActivityCompat.shouldShowRequestPermissionRationale(activity, cameraPermission)
