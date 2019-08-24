package com.example.crs

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

class SetPositionService : Service() {

    lateinit var windowManager: WindowManager
    lateinit var floatingView: View
    lateinit var collapsedView: View
    lateinit var params: WindowManager.LayoutParams

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        floatingView = LayoutInflater.from(this).inflate(R.layout.set_position_layout, null)
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.x = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE).getInt(X, 0)
        params.y = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE).getInt(Y, 0)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)
        collapsedView = floatingView.findViewById(R.id.Layout_Collapsed)


        floatingView.findViewById<ImageView>(R.id.cancel)
            .setOnClickListener { stopService(Intent(applicationContext, SetPositionService::class.java)) }
        floatingView.findViewById<ImageView>(R.id.accept)
            .setOnClickListener { savePosition() }
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
                        return false
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = X_Axis + (event.rawX - TouchX).toInt()
                        params.y = Y_Axis + (event.rawY - TouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return false
                    }
                }
                return false
            }
        })
    }

    fun savePosition() {
        getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE).edit().putInt(X, params.x).apply()
        getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE).edit().putInt(Y, params.y).apply()
        val text = "Ulozene."
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
        stopService(Intent(applicationContext, SetPositionService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }

    companion object {
        const val PREFERENCES = "preferences"
        const val X = "x"
        const val Y = "y"
    }
}