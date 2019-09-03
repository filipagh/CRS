package com.example.crs

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer


class MediaProjectionUtil {
    companion object {
        var mediaProjection: MediaProjection? = null
        var instance: MediaProjectionUtil? = null
        var mImageReader: ImageReader? = null
        var windowSize: Point? = null
        var appContect: Context? = null
        var textRecognizer: TextRecognizer? = null
    }

    fun MediaProjectionUtil(): MediaProjectionUtil? {
        if (instance == null) {
            instance = this
        }
        return instance
    }

    fun setupMediaProjection(intentMain: Intent, context: Context) {
        if (mediaProjection == null) {
            appContect = context.applicationContext
            val resultCode = intentMain.getIntExtra("resultCode", 0)
            val data = intentMain.getParcelableExtra<Intent>("mediaProjectionData")
            val mediaProjectManag =
                context.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

            mediaProjection = mediaProjectManag.getMediaProjection(resultCode, data)

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowSize = Point()
            windowManager.defaultDisplay.getSize(windowSize)

            mImageReader = ImageReader.newInstance(windowSize!!.y, windowSize!!.y, PixelFormat.RGBA_8888, 20)
            context.applicationContext
            mediaProjection!!.createVirtualDisplay(
                "ScreenCapture",
                windowSize!!.x, windowSize!!.y, 1000,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader!!.surface, null, null
            )

            setupTextParser()
        }
    }

    fun makeScrShot(boxPositon: Point): Bitmap {

        var i = 0
        while (i != 100000) {
            i++
            //todo add callback func
        }
        val image = mImageReader!!.acquireNextImage()
        val planes = image!!.planes

        val buffer = planes[0].buffer.rewind()
        val bitmap = Bitmap.createBitmap(windowSize!!.y, windowSize!!.y, Bitmap.Config.ARGB_8888)
        bitmap!!.copyPixelsFromBuffer(buffer)

//        return Bitmap.createBitmap(bitmap, 0, 0, windowSize!!.x, windowSize!!.y)
        val a = Bitmap.createBitmap(bitmap, 0, 0, windowSize!!.x, windowSize!!.y)
        val b = Bitmap.createBitmap(a, 0, boxPositon.y +10, 500, 130)

        return Bitmap.createScaledBitmap(b, 500 * 3, 130 * 3, false)
    }

    fun parseTextFromBitmap(bitmap: Bitmap): ArrayList<String> {

        val fb = Frame.Builder()
        fb.setBitmap(bitmap)
        val frame = fb.build()
        val parsedTextBlocks = textRecognizer!!.detect(frame)
        val result = arrayListOf<String>()
        for (i in 0 until parsedTextBlocks.size()) {
            for (ii in 0 until parsedTextBlocks[i].components.size) {
                try {
                    result.add(parsedTextBlocks[i].components[ii].value)
                    Log.d("Parser", parsedTextBlocks[i].components[ii].value)
                } catch (e: Exception) {
                }
            }
        }
        return result

    }

    private fun setupTextParser() {
        textRecognizer = TextRecognizer.Builder(appContect!!).build()
        textRecognizer!!.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems
                val stringBuilder = StringBuffer("")


                if (items.size() != 0) {

                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }

                }
            }
        })
    }

}