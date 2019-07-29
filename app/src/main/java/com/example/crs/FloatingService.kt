package com.example.crs

import android.content.Intent
import android.graphics.Color
import android.media.ImageReader
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bsk.floatingbubblelib.FloatingBubbleConfig
import com.bsk.floatingbubblelib.FloatingBubbleService

class FloatingService : FloatingBubbleService() {

    lateinit var intentMain: Intent
//    var mediaProjection: MediaProjection? = null
    var mediaProjectionUtil: MediaProjectionUtil? = null
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
        if (mediaProjectionUtil == null) {
            mediaProjectionUtil = MediaProjectionUtil()
            mediaProjectionUtil!!.setupMediaProjection(intentMain,this)
        }
        val btn = root.findViewById(R.id.scrShot) as Button
        btn.isEnabled = true
    }

    fun scrShot(view: View) {
        val btn = root.findViewById(R.id.scrShot) as Button
        btn.isEnabled = false
        setState(false)

        val scrShot = mediaProjectionUtil!!.makeScrShot()
        mediaProjectionUtil!!.parseTextFromBitmap(scrShot)
        setState(true)
        val surf = root.findViewById(R.id.imageView) as ImageView
        surf.setImageBitmap(scrShot)

        val btnClear = root.findViewById(R.id.button2) as Button
        btnClear.isEnabled = true
    }

    fun clear(view: View) {
        val btnClear = root.findViewById(R.id.button2) as Button
        btnClear.isEnabled = false
        val surf = root.findViewById(R.id.imageView) as ImageView
        surf.setImageResource(android.R.color.transparent)
        val btn = root.findViewById(R.id.scrShot) as Button
        btn.isEnabled = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intentMain = intent!!
        return super.onStartCommand(intent, flags, startId)
    }


}

