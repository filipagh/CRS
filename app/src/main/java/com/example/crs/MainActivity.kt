package com.example.crs

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    companion object {
        val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 8587
    }

    lateinit var floatingService: Intent
    lateinit var setPositionService: Intent
    lateinit var mediaProjectManag: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingService = Intent(this, FloatingWidgetShowService::class.java)
        setPositionService = Intent(this, SetPositionService::class.java)

        mediaProjectManag = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectManag.createScreenCaptureIntent(), 100)


    }

    private fun askForOverlayPermission() {
        startActivityForResult(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName)
            ), ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                askForOverlayPermission()
            }
        }
        if (requestCode == 100) {
            floatingService.putExtra("resultCode", resultCode)
            floatingService.putExtra("mediaProjectionData", data)
            setPositionService.putExtra("resultCode", resultCode)
            setPositionService.putExtra("mediaProjectionData", data)

            // permison na zobrazovanie nad aplikaciami
            if (!Settings.canDrawOverlays(this)) {
                askForOverlayPermission()
            }
        }

    }

    fun start(a: View) {
        startService(floatingService)
        moveTaskToBack(true)
    }

    fun startSetting(a: View) {
        startService(setPositionService)
        moveTaskToBack(true)
    }

    fun end(a: View) {
        stopService(floatingService)
        startService(setPositionService)
        moveTaskToBack(true)
    }

}
