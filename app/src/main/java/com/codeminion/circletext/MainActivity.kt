package com.codeminion.circletext

import android.bluetooth.BluetoothAdapter
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.brother.ptouch.sdk.LabelInfo
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo


class MainActivity : AppCompatActivity() {

    private var mGeneratedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generateButton: View = findViewById(R.id.generate_text)
        val topText: EditText = findViewById(R.id.top_text)
        val bottomText: EditText = findViewById(R.id.bottom_text)
        val printButton: View = findViewById(R.id.print_image)

        generateButton.setOnClickListener {
            generateBitmap(
                topText = topText.text.toString(),
                bottomText = bottomText.text.toString()
            )
        }

        printButton.setOnClickListener {
            mGeneratedBitmap?.let { image ->
                print(image)
            }
        }

        // IMPORTANT - This is for sample purposes only.
        // The work in this sample if being done on the main thread to not
        // distract the readers from the main subject.
        // If doing something similar in your app make sure to take advantage
        // of Coroutines to run most of the work on a background thread.


    }

    fun generateBitmap(topText: String = "Brother Hackathon", bottomText: String = "2021") {

        mGeneratedBitmap?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }

        val imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo_brother)

        val imageView: ImageView = findViewById(R.id.image)
        val totalWidth = resources.getDimensionPixelSize(R.dimen.image_size);
        val conf = Bitmap.Config.ARGB_8888
        val circleImage = Bitmap.createBitmap(totalWidth, totalWidth, conf)
        val canvas = Canvas(circleImage)

        val defTextSize = totalWidth / 10f
        // Create a background paint, we will use this for painting the bitmap on the background.
        val backgroundPaint = Paint().apply {
            color = Color.WHITE
            textSize = defTextSize
            isAntiAlias = true
        }

        val imagePaint = Paint().apply {
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
        val imageBounds = RectF(0f, 0f, totalWidth.toFloat(), totalWidth.toFloat())


        // Background - Draw circle Bitmap
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(totalWidth / 2f, totalWidth / 2f, totalWidth / 2f, imagePaint)
        imagePaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        val imageSize = imageBitmap.width;
        val srcRect = Rect(0, 0, imageSize, imageSize)
        val backgroundSize = (totalWidth * 0.8).toInt()
        val padding = (totalWidth - backgroundSize) / 2
        val destRect =
            Rect(padding, padding, padding + backgroundSize, padding + backgroundSize)
        canvas.drawBitmap(imageBitmap, srcRect, destRect, imagePaint)

        // Background Draw a Rectangle for printer support.
        backgroundPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_OVER))
        canvas.drawRect(imageBounds, backgroundPaint)

        // Top Text - Create the Arc for the top text to follow.
        val outerTextBounds =
            RectF(defTextSize, defTextSize, totalWidth - defTextSize, totalWidth - defTextSize)
        val outerTextPath = Path()
        outerTextPath.addArc(outerTextBounds, 180f, 180f)
        canvas.drawTextOnPath(topText, outerTextPath, 0f, 0f, textPaint)

        // Bottom Text - Create the Arc for the bottom text to follow.
        val innerTextBounds =
            RectF(defTextSize, defTextSize, totalWidth - defTextSize, totalWidth - defTextSize)
        val innerTextPath = Path()
        innerTextPath.addArc(innerTextBounds, 180f, -180f)
        canvas.drawTextOnPath(bottomText, innerTextPath, 0f, 0f, textPaint)

        // Save the image
        canvas.save()

        imageView.setImageBitmap(circleImage)

        mGeneratedBitmap = circleImage

        // Save to gallery
        MediaStore.Images.Media.insertImage(
            getContentResolver(),
            circleImage,
            "Circle-Text",
            "Sample Circle Text"
        )


    }

    // IMPORTANT: This is done on the main thread for demo purposes only.
    // Move this to a background thread.
    fun print(bitmap: Bitmap) {
        val printer = Printer()
        val printInfo = PrinterInfo()
        printInfo.printerModel = PrinterInfo.Model.QL_1110NWB
        printInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE
        printInfo.isAutoCut = true
        printInfo.port = PrinterInfo.Port.BLUETOOTH;
        printInfo.macAddress = "58:93:D8:BD:69:95" // Printer BLuetooth Mac
        printInfo.workPath = filesDir.absolutePath + "/";
        printInfo.labelNameIndex = LabelInfo.QL1100.W103.ordinal;

        printer.printerInfo = printInfo
        printer.setBluetooth(BluetoothAdapter.getDefaultAdapter())

        printer.startCommunication()
        val result = printer.printImage(bitmap)
        if (result.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
            println("Error: " + result.errorCode)
        }
        printer.endCommunication()

    }
}
