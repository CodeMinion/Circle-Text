package com.codeminion.circletext

import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // IMPORTANT - This is for sample purposes only.
        // The work in this sample if being done on the main thread to not
        // distract the readers from the main subject.
        // If doing something similar in your app make sure to take advantage
        // of Coroutines to run most of the work on a background thread.

        val imageView:ImageView = findViewById(R.id.image)
        val totalWidth = resources.getDimensionPixelSize(R.dimen.image_size);
        val conf = Bitmap.Config.ARGB_8888
        val circleImage = Bitmap.createBitmap(totalWidth, totalWidth, conf)
        val canvas = Canvas(circleImage)


        val defTextSize = totalWidth / 10f
        // Create a background paint, we will use this for painting the bitmap on the background.
        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#0d2ea0")
            textSize = defTextSize
            isAntiAlias = true
        }

        // Create a text paint that will be used to draw the text on the image.
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = defTextSize
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val imageBounds = RectF(0f,0f, totalWidth.toFloat(), totalWidth.toFloat())

        // Background - Draw circle Bitmap
        val imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_brother)
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(totalWidth/2f, totalWidth/2f, totalWidth/2f, backgroundPaint)
        backgroundPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        val imageSize = imageBitmap.width;
        val srcRect = Rect(0, 0, imageSize, imageSize)
        val backgroundSize = (totalWidth *0.8).toInt()
        val padding = (totalWidth - backgroundSize)/2
        val destRect = Rect(padding,padding, padding + backgroundSize, padding+backgroundSize)
        canvas.drawBitmap(imageBitmap, srcRect, destRect, backgroundPaint)

        // Top Text - Create the Arc for the top text to follow.
        val outerTextBounds = RectF(defTextSize, defTextSize, totalWidth - defTextSize, totalWidth-defTextSize)
        val outerTextPath = Path()
        outerTextPath.addArc(outerTextBounds, 180f, 180f)
        canvas.drawTextOnPath("Brother Hackathon", outerTextPath, 0f,0f, textPaint)

        // Bottom Text - Create the Arc for the bottom text to follow.
        val innerTextBounds = RectF(defTextSize, defTextSize, totalWidth - defTextSize, totalWidth-defTextSize)
        val innerTextPath = Path()
        innerTextPath.addArc(innerTextBounds, 180f, -180f)
        canvas.drawTextOnPath("2021", innerTextPath, 0f,0f, textPaint)

        // Save the image
        canvas.save()

        imageView.setImageBitmap(circleImage)

    }



}
