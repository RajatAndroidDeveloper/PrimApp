package com.primapp.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.primapp.R
import java.util.*

class LetterTileProvider {
    companion object {
        const val CIRCLE = 1
        const val RECTANGLE = 0
        const val ROUNDED_RECTANGLE = 2
        const val COLOR900 = 900
        const val COLOR400 = 400
        const val COLOR700 = 700

        private lateinit var uiContext: Context
        private var texSize = 0F

        fun avatarImage(context: Context, size: Int, shape: Int, name: String?): BitmapDrawable {
            return avatarImageGenerate(context, size, shape, name, COLOR700)
        }


        fun avatarImage(
            context: Context,
            size: Int,
            shape: Int,
            name: String,
            colorModel: Int
        ): BitmapDrawable {
            return avatarImageGenerate(context, size, shape, name, colorModel)
        }

        private fun avatarImageGenerate(
            context: Context,
            size: Int,
            shape: Int,
            name: String?,
            colorModel: Int
        ): BitmapDrawable {
            uiContext = context

            texSize = calTextSize(size)
            val label = getNameInitials(name)
            val textPaint = textPainter()
            val painter = painter()
            painter.isAntiAlias = true
            val areaRect = Rect(0, 0, size, size)

            if (shape == RECTANGLE || shape == ROUNDED_RECTANGLE) {
                painter.color = RandomColors(colorModel).getColor()
            } else {
                painter.color = Color.TRANSPARENT
            }

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            if (shape == ROUNDED_RECTANGLE) {
                //Used in Skills on Portfolio
                canvas.drawRoundRect(RectF(areaRect), 16f, 16f, painter)
            } else {
                canvas.drawRect(areaRect, painter)
            }

            //reset painter
            if (shape == RECTANGLE || shape == ROUNDED_RECTANGLE) {
                painter.color = Color.TRANSPARENT
            } else {
                painter.color = RandomColors(colorModel).getColor()
            }

            val bounds = RectF(areaRect)
            bounds.right = textPaint.measureText(label, 0, label.length)
            bounds.bottom = textPaint.descent() - textPaint.ascent()

            bounds.left += (areaRect.width() - bounds.right) / 2.0f
            bounds.top += (areaRect.height() - bounds.bottom) / 2.0f

            canvas.drawCircle(size.toFloat() / 2, size.toFloat() / 2, size.toFloat() / 2, painter)
            canvas.drawText(label, bounds.left, bounds.top - textPaint.ascent(), textPaint)
            return BitmapDrawable(uiContext.resources, bitmap)

        }


        private fun getNameInitials(name: String?): String {
            var alteredName:String = name ?: "?"
            if(alteredName.trim().isEmpty()) {
                alteredName = "?"
            }
            return alteredName
                .split(' ')
                .mapNotNull { it.firstOrNull()?.toString() }
                .reduce { acc, s -> acc + s }

            //return name.first().toString().toUpperCase(Locale.ROOT)
        }

        private fun textPainter(): TextPaint {

            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.textSize = texSize * uiContext.resources.displayMetrics.scaledDensity
            textPaint.color = Color.WHITE
            textPaint.typeface = ResourcesCompat.getFont(uiContext, R.font.poppins_regular)
            return textPaint
        }

        private fun painter(): Paint {
            return Paint()
        }

        private fun calTextSize(size: Int): Float {
            return (size / 6).toFloat()
        }
    }


    internal class RandomColors(colorModel: Int = 700) {
        private val recycle: Stack<Int> = Stack()
        private val colors: Stack<Int> = Stack()

        fun getColor(): Int {
            if (colors.size == 0) {
                while (!recycle.isEmpty()) colors.push(recycle.pop())
                Collections.shuffle(colors)
            }
            val c: Int = colors.pop()
            recycle.push(c)
            return c
        }

        init {
            if (colorModel == 700) {
                recycle.addAll(
                    //A 700
                    listOf(
                        -0xd32f2f, -0xC2185B, -0x7B1FA2, -0x512DA8,
                        -0x303F9F, -0x1976D2, -0x0288D1, -0x0097A7,
                        -0x00796B, -0x388E3C, -0x689F38, -0xAFB42B,
                        -0xFBC02D, -0xFFA000, -0xF57C00, -0xE64A19,
                        -0x5D4037, -0x616161, -0x455A64
                    )
                )
            }

            //A400
            if (colorModel == 400) {
                recycle.addAll(
                    listOf(
                        -0xef5350, -0xEC407A, -0xAB47BC, -0x7E57C2,
                        -0x5C6BC0, -0x42A5F5, -0x29B6F6, -0x26C6DA,
                        -0x26A69A, -0x66BB6A, -0x9CCC65, -0xD4E157,
                        -0xFFEE58, -0xFFCA28, -0xFFA726, -0xFF7043,
                        -0x8D6E63, -0xBDBDBD, -0x78909C
                    )
                )
            }

            //A900
            if (colorModel == 900) {
                recycle.addAll(
                    listOf(
                        -0xb71c1c, -0x880E4F, -0x4A148C, -0x311B92,
                        -0x1A237E, -0x0D47A1, -0x01579B, -0x006064,
                        -0x004D40, -0x1B5E20, -0x33691E, -0x827717,
                        -0xF57F17, -0xFF6F00, -0xE65100, -0xBF360C,
                        -0x3E2723, -0x212121, -0x263238
                    )
                )
            }

        }
    }
}