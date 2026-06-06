package bi.lan.lan.core.utils

import android.content.Context
import android.graphics.*
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ReceiptGenerator {
    fun generateReceipt(
        context: Context,
        amount: Long,
        reference: String,
        txId: String,
        date: Long
    ): Uri? {
        val width = 600
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Background
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Header (Greenish like LAN theme)
        paint.color = 0xFF00A36C.toInt() // Teal/Green
        canvas.drawRect(0f, 0f, width.toFloat(), 150f, paint)

        paint.color = Color.WHITE
        paint.textSize = 40f
        paint.isFakeBoldText = true
        canvas.drawText("LAN - Lightning Agent Network", 50f, 80f, paint)

        paint.textSize = 30f
        paint.isFakeBoldText = false
        canvas.drawText("PAYMENT RECEIVED", 50f, 125f, paint)

        // Body
        paint.color = Color.BLACK
        paint.textSize = 24f
        var y = 250f
        val step = 60f

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateStr = dateFormat.format(Date(date))

        canvas.drawText("Amount: $amount sats", 50f, y, paint)
        y += step
        canvas.drawText("Reference: $reference", 50f, y, paint)
        y += step
        canvas.drawText("Date: $dateStr", 50f, y, paint)
        y += step
        canvas.drawText("Status: SUCCESS", 50f, y, paint)
        y += step
        
        paint.textSize = 18f
        paint.color = Color.GRAY
        canvas.drawText("Transaction ID:", 50f, y, paint)
        y += 30f
        canvas.drawText(txId, 50f, y, paint)

        // Footer
        y = height - 50f
        paint.textSize = 20f
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Powered by LAN ⚡", width / 2f, y, paint)

        // Save to file
        return try {
            val cachePath = File(context.cacheDir, "receipts")
            cachePath.mkdirs()
            val file = File(cachePath, "receipt_$reference.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
