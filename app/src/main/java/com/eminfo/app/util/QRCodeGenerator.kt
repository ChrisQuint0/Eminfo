package com.eminfo.app.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

data class EmergencyQRData(
    val name: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val medications: String = "",
    val primaryContact: QRContact? = null,
    val physicianName: String = "",
    val physicianPhone: String = ""
)

data class QRContact(
    val name: String,
    val phone: String,
    val relationship: String
)

object QRCodeGenerator {

    fun generateQRCode(data: EmergencyQRData, size: Int): Bitmap? {
        try {
            val formattedText = formatEmergencyData(data)

            if (formattedText.isBlank()) {
                return null
            }

            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1)
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(formattedText, BarcodeFormat.QR_CODE, size, size, hints)

            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun generateDownloadableQRCard(
        context: android.content.Context,
        qrBitmap: Bitmap
    ): Bitmap? {
        try {
            // Card dimensions
            val cardWidth = 1080
            val cardHeight = 1400
            val padding = 80

            // Create the card bitmap
            val cardBitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(cardBitmap)

            // Draw white background
            val paint = android.graphics.Paint().apply {
                color = Color.WHITE
                style = android.graphics.Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), paint)

            // Load and draw logo
            try {
                val logoDrawable = context.resources.getIdentifier(
                    "eminfo_logo",
                    "drawable",
                    context.packageName
                )

                if (logoDrawable != 0) {
                    val logoBitmap = android.graphics.BitmapFactory.decodeResource(
                        context.resources,
                        logoDrawable
                    )
                    val logoSize = 120
                    val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoSize, logoSize, true)

                    val logoX = (cardWidth - logoSize) / 2f
                    val logoY = padding.toFloat()
                    canvas.drawBitmap(scaledLogo, logoX, logoY, null)

                    logoBitmap.recycle()
                    scaledLogo.recycle()
                } else {
                    // Fallback: Draw app icon or text if logo not found
                    val fallbackPaint = android.graphics.Paint().apply {
                        color = Color.parseColor("#00b761")
                        textSize = 48f
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText("EmInfo", cardWidth / 2f, padding + 60f, fallbackPaint)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback: Draw text if logo loading fails
                val fallbackPaint = android.graphics.Paint().apply {
                    color = Color.parseColor("#00b761")
                    textSize = 48f
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.drawText("EmInfo", cardWidth / 2f, padding + 60f, fallbackPaint)
            }

            // Draw "Emergency Information" text
            val textPaint = android.graphics.Paint().apply {
                color = Color.parseColor("#1C1C1E")
                textSize = 56f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            val textY = padding + 140f + 50f
            canvas.drawText("Emergency Information", cardWidth / 2f, textY, textPaint)

            // Draw QR code
            val qrSize = cardWidth - (padding * 2)
            val scaledQR = Bitmap.createScaledBitmap(qrBitmap, qrSize, qrSize, true)
            val qrY = textY + 60f
            canvas.drawBitmap(scaledQR, padding.toFloat(), qrY, null)
            scaledQR.recycle()

            // Draw bottom text
            val bottomTextPaint = android.graphics.Paint().apply {
                color = Color.parseColor("#8E8E93")
                textSize = 32f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL)
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            val bottomY = cardHeight - padding + 20f
            canvas.drawText("Scan for emergency medical information", cardWidth / 2f, bottomY, bottomTextPaint)

            return cardBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun formatEmergencyData(data: EmergencyQRData): String {
        val sections = mutableListOf<String>()

        // Header
        sections.add("═══ EMERGENCY MEDICAL INFO ═══")
        sections.add("")

        // Patient Information
        if (data.name.isNotBlank()) {
            sections.add("👤 PATIENT NAME:")
            sections.add("   ${data.name}")
            sections.add("")
        }

        if (data.bloodType.isNotBlank()) {
            sections.add("🩸 BLOOD TYPE:")
            sections.add("   ${data.bloodType}")
            sections.add("")
        }

        // Critical Medical Information
        if (data.allergies.isNotBlank()) {
            sections.add("⚠️  ALLERGIES:")
            sections.add("   ${data.allergies}")
            sections.add("")
        }

        if (data.medicalConditions.isNotBlank()) {
            sections.add("🏥 MEDICAL CONDITIONS:")
            sections.add("   ${data.medicalConditions}")
            sections.add("")
        }

        if (data.medications.isNotBlank()) {
            sections.add("💊 CURRENT MEDICATIONS:")
            sections.add("   ${data.medications}")
            sections.add("")
        }

        // Emergency Contact
        if (data.primaryContact != null) {
            sections.add("📞 EMERGENCY CONTACT:")
            sections.add("   ${data.primaryContact.name}")
            sections.add("   ${data.primaryContact.relationship}")
            sections.add("   Tel: ${data.primaryContact.phone}")
            sections.add("")
        }

        // Physician Information
        if (data.physicianName.isNotBlank() || data.physicianPhone.isNotBlank()) {
            sections.add("👨‍⚕️ PHYSICIAN:")
            if (data.physicianName.isNotBlank()) {
                sections.add("   ${data.physicianName}")
            }
            if (data.physicianPhone.isNotBlank()) {
                sections.add("   Tel: ${data.physicianPhone}")
            }
            sections.add("")
        }

        sections.add("══════════════════════════")
        sections.add("Generated: ${getCurrentDateTime()}")

        return sections.joinToString("\n")
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.US)
        return dateFormat.format(java.util.Date())
    }
}