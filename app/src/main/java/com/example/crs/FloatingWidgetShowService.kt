package com.example.crs

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.example.crs.RoyalApi.PlayerModel

class FloatingWidgetShowService : Service() {

    lateinit var windowManager: WindowManager
    lateinit var floatingView: View
    lateinit var collapsedView: View
    lateinit var params: WindowManager.LayoutParams
    lateinit var image: ImageView

    var intentMain: Intent? = null
    var mediaProjectionUtil: MediaProjectionUtil? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intentMain = intent!!
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null)
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)
        collapsedView = floatingView.findViewById(R.id.Layout_Collapsed)
        image = floatingView.findViewById(R.id.Logo_Icon)

        floatingView.setOnClickListener { scrShot() }

        floatingView.setOnTouchListener(object : View.OnTouchListener {
            var X_Axis: Int = 0
            var Y_Axis: Int = 0
            var TouchX: Float = 0.toFloat()
            var TouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        X_Axis = params.x
                        Y_Axis = params.y
                        TouchX = event.rawX
                        TouchY = event.rawY
                        Log.d("aaaa","stlacil som")
                        return false
                    }

                    MotionEvent.ACTION_UP -> {
                        if (shouldClose(params.x, params.y)) {
                            stopService(Intent(applicationContext, FloatingWidgetShowService::class.java))
                        }
                        Log.d("aaaa","stlacil som up")
                        return false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = X_Axis + (event.rawX - TouchX).toInt()
                        params.y = Y_Axis + (event.rawY - TouchY).toInt()
                        if (shouldClose(params.x, params.y)) {
                            image.setColorFilter(Color.RED)
                        } else {
                            image.setColorFilter(Color.TRANSPARENT)
                        }
                        windowManager.updateViewLayout(floatingView, params)
                        return false
                    }
                }
                return false
            }
        })
    }

    fun scrShot() {

        Toast.makeText(this,"scrshot", Toast.LENGTH_LONG).show()
        if (mediaProjectionUtil == null) {
            mediaProjectionUtil = MediaProjectionUtil()
            mediaProjectionUtil!!.setupMediaProjection(intentMain!!, this)
        }
        var boxPosition = Point()
        boxPosition.x = getSharedPreferences(SetPositionService.PREFERENCES, Activity.MODE_PRIVATE).getInt(SetPositionService.X, 0)
        boxPosition.y = getSharedPreferences(SetPositionService.PREFERENCES, Activity.MODE_PRIVATE).getInt(SetPositionService.Y, 0)

        val scrShot = mediaProjectionUtil!!.makeScrShot(boxPosition)
        val player = PlayerModel(scrShot)

        Toast.makeText(this, player.playerData?.get("trophies")?.toString(), Toast.LENGTH_LONG).show()


//        val surf = root.findViewById(R.id.imageView) as ImageView
//        surf.setImageBitmap(scrShot)

//        val btnClear = root.findViewById(R.id.button2) as Button
//        btnClear.isEnabled = true
    }

    fun shouldClose(x: Int, y: Int): Boolean {
        return x > -200 && x < 200 && y > 500
    }


    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }
}


