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
        date: Long,
        status: String = "PAID"
    ): Uri? {
        val width = 600
        val height = 900
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()

        // Background (Premium Dark)
        paint.color = 0xFF0F172A.toInt() // BackgroundDark
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // Accent Gradient Header
        val gradient = LinearGradient(0f, 0f, width.toFloat(), 200f, 0xFF00D09C.toInt(), 0xFF00AEEF.toInt(), Shader.TileMode.CLAMP)
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), 200f, paint)
        paint.shader = null

        // Header Text
        paint.color = Color.WHITE
        paint.textSize = 48f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("LAN", width / 2f, 100f, paint)
        
        paint.textSize = 20f
        paint.isFakeBoldText = false
        canvas.drawText("Lightning Agent Network", width / 2f, 140f, paint)

        // Body
        paint.textAlign = Paint.Align.LEFT
        var y = 300f
        val step = 80f
        val margin = 60f

        // Status Badge
        val statusColor = when(status.uppercase()) {
            "PAID", "SUCCESS" -> 0xFF00C853.toInt()
            "PENDING" -> 0xFFFFB300.toInt()
            "EXPIRED" -> 0xFF94A3B8.toInt()
            else -> 0xFFFF5252.toInt()
        }
        
        paint.color = statusColor
        val rect = RectF(margin, y - 40f, margin + 200f, y + 20f)
        canvas.drawRoundRect(rect, 20f, 20f, paint)
        
        paint.color = Color.WHITE
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText(status.uppercase(), margin + 40f, y - 5f, paint)
        
        y += 120f

        // Amount
        paint.color = 0xFF00D09C.toInt()
        paint.textSize = 64f
        canvas.drawText("$amount sats", margin, y, paint)
        y += 100f

        // Divider
        paint.color = 0xFF334155.toInt()
        paint.strokeWidth = 2f
        canvas.drawLine(margin, y, width - margin, y, paint)
        y += 60f

        // Details
        paint.color = 0xFFCBD5E1.toInt() // TextSecondaryDark
        paint.textSize = 22f
        paint.isFakeBoldText = false
        
        canvas.drawText("Reference", margin, y, paint)
        paint.color = Color.WHITE
        paint.isFakeBoldText = true
        canvas.drawText(reference.take(20), width - margin - 250f, y, paint)
        y += step

        paint.color = 0xFFCBD5E1.toInt()
        paint.isFakeBoldText = false
        canvas.drawText("Date", margin, y, paint)
        paint.color = Color.WHITE
        paint.isFakeBoldText = true
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        canvas.drawText(dateFormat.format(Date(date)), width - margin - 250f, y, paint)
        y += step

        paint.color = 0xFFCBD5E1.toInt()
        paint.isFakeBoldText = false
        canvas.drawText("Transaction ID", margin, y, paint)
        paint.color = Color.WHITE
        paint.isFakeBoldText = true
        canvas.drawText(txId.take(12) + "...", width - margin - 250f, y, paint)

        // Footer
        y = height - 60f
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 18f
        paint.color = 0xFF475569.toInt()
        canvas.drawText("Secure • Instant • Borderless", width / 2f, y, paint)

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
