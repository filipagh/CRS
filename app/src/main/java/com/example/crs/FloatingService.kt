package com.example.crs

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.provider.Settings
import android.service.media.MediaBrowserService
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.bsk.floatingbubblelib.FloatingBubbleService
import com.bsk.floatingbubblelib.FloatingBubbleConfig
import kotlinx.android.synthetic.main.floating_service_layout.view.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import android.widget.Button
import android.widget.ImageView
import com.bsk.floatingbubblelib.FloatingBubbleTouchListener
import java.io.FileOutputStream
import java.io.IOException

class FloatingService : FloatingBubbleService() {

    lateinit var intentMain: Intent
    var mediaProjection: MediaProjection? = null
    lateinit var mImageReader: ImageReader
    lateinit var root: View

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun getConfig(): FloatingBubbleConfig {
        root = getInflater().inflate(R.layout.floating_service_layout, null)
        return FloatingBubbleConfig.Builder()

            // Set the size of the bubble in dp
            .bubbleIconDp(64)

            // Set the size of the remove bubble in dp
            .removeBubbleIconDp(64)

            // Set the padding of the view from the boundary
            .paddingDp(4)

            // Set the radius of the border of the expandable view
            .borderRadiusDp(4)

            // Does the bubble attract towards the walls
            .physicsEnabled(true)

            // The color of background of the layout
            .expandableColor(Color.WHITE)

            // The color of the triangular layout
            .triangleColor(Color.WHITE)

            // Horizontal gravity of the bubble when expanded
            .gravity(Gravity.END)

            // The view which is visible in the expanded view
            .expandableView(root)

            // Set the alpha value for the remove bubble icon
            .removeBubbleAlpha(0.75f)

            // Building
            .build()
    }


    fun setup(vstup: View) {

        if (mediaProjection == null) {
            val resultCode = intentMain!!.getIntExtra("resultCode", 0)
            val data = intentMain.getParcelableExtra<Intent>("mediaProjectionData")
            val mediaProjectManag =
                getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mediaProjectManag.getMediaProjection(resultCode, data)
        }

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var windowSize = Point()
        windowManager.defaultDisplay.getSize(windowSize)

        mImageReader = ImageReader.newInstance(windowSize.y, windowSize.y, PixelFormat.RGBA_8888, 20)

        val display = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            windowSize.x, windowSize.y, 1000,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader.surface, null, null
        )

        var btn = root.findViewById(R.id.scrShot) as Button
        btn.isEnabled = true


    }

    fun scrShot(view: View) {

        var btn = root.findViewById(R.id.scrShot) as Button
        btn.isEnabled = false
        setState(false)
        var image: Image? = null
        var bitmap: Bitmap? = null
        var i = 0
        while (i != 100000) {
            i++
        }
        val surf = root.findViewById(R.id.imageView) as ImageView
        image = mImageReader.acquireNextImage()
        val planes = image!!.planes

        val buffer = planes[0].buffer.rewind()
        bitmap = Bitmap.createBitmap(windowSize.y, windowSize.y, Bitmap.Config.ARGB_8888)
        bitmap!!.copyPixelsFromBuffer(buffer)

        val a = Bitmap.createBitmap(bitmap, 0, 0, windowSize.x, windowSize.y)


        val c = Canvas(a)
        setState(true)
        surf.setImageBitmap(a)
        var x = 4

        var btnClear = root.findViewById(R.id.button2) as Button
        btnClear.isEnabled = true
    }

    fun clear(view: View) {

        var btnClear = root.findViewById(R.id.button2) as Button
        btnClear.isEnabled = false
        val surf = root.findViewById(R.id.imageView) as ImageView
        surf.setImageResource(android.R.color.transparent)
        var btn = root.findViewById(R.id.scrShot) as Button
        btn.isEnabled = true

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intentMain = intent!!


        return super.onStartCommand(intent, flags, startId)
    }


}

