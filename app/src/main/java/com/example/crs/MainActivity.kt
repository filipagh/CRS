package com.example.crs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.graphics.Bitmap
import android.R.attr.y
import android.R.attr.x
import android.graphics.Point
import android.view.Display
import android.graphics.drawable.Drawable
import android.support.v4.content.res.TypedArrayUtils.getResourceId
import android.content.res.TypedArray
import android.content.res.Resources.Theme
import android.app.Activity
import android.graphics.Canvas
import android.view.View
import android.R.layout
import android.hardware.display.DisplayManager
import android.media.projection.MediaProjectionManager
import android.view.View.MeasureSpec
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.view.SurfaceView
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    companion object {
        val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 8587
    }
   lateinit var floatingService: Intent
lateinit var mediaProjectManag: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingService = Intent(this, FloatingService::class.java)

        mediaProjectManag = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectManag.createScreenCaptureIntent(),100)


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
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                askForOverlayPermission()
            } else {
                startService(floatingService)

            }
        }
        if (requestCode == 100) {
            floatingService.putExtra("resultCode",resultCode)
            floatingService.putExtra("mediaProjectionData",data)

            // permison na zobrazovanie nad aplikaciami
            if (!Settings.canDrawOverlays(this)) {
                askForOverlayPermission()
            } else {
                startService(floatingService)
            }
        }

    }

}
