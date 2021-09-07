package org.jam.jmessenger.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import org.jam.jmessenger.R


class ProfileRoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs) {

    private val borderWidth = 8f
    private val borderColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)
    private val backColor = ResourcesCompat.getColor(resources, R.color.primaryDarkColor, null)

    private var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.ic_user_default_profile)
    private var bitmapDefaultMask = ResourcesCompat.getDrawable(resources, R.drawable.uprofile_mask, null)!!.toBitmap()

    private val bitmapShader = BitmapShader(bitmapDefaultMask, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private val paint = Paint().apply { isAntiAlias = true }
    private val paintBorder = Paint().apply { isAntiAlias = true; color = borderColor}
    private val paintBackground = Paint().apply { isAntiAlias = true; color = backColor }
    private val paintBackgroundMask = Paint().apply { isAntiAlias = true; shader = bitmapShader; style = Paint.Style.FILL}


    override fun getScaleType(): ScaleType =
        super.getScaleType() ?: ScaleType.CENTER_CROP

    override fun setScaleType(scaleType: ScaleType) {
        require(
            listOf(
                ScaleType.CENTER_CROP,
                ScaleType.CENTER_INSIDE,
                ScaleType.FIT_CENTER
            ).contains(scaleType)
        ) {
            "ScaleType $scaleType not supported. Just ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE & ScaleType.FIT_CENTER are available for this library."
        }
        super.setScaleType(scaleType)
    }

    private fun makeCircle(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val bitmapHeight = bitmap.height
        val radius = ((bitmapHeight - 14) / 2).toFloat()
        val center = ((bitmapHeight) / 2).toFloat()
        val baseBitmap = Bitmap.createBitmap(bitmapHeight, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(baseBitmap)
        val basePaint = Paint().apply { isAntiAlias = true; color = Color.WHITE; xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)}
        canvas.drawCircle(center, center, radius, paint)
        canvas.drawBitmap(bitmap, 0f, 0f, basePaint)
        return baseBitmap
    }

    override fun onDraw(canvas: Canvas) {
        // super.onDraw(canvas)
        val baseRadius = this.height.toFloat() / 2

        // Draw Border
        canvas.drawCircle(baseRadius, baseRadius, baseRadius, paintBorder)
        // Draw Circle background
        canvas.drawCircle(baseRadius, baseRadius, baseRadius - borderWidth, paintBackground)
        // Draws Pixmap
        canvas.drawBitmap(makeCircle(bitmapDefault.scale((baseRadius * 2).toInt(), (baseRadius * 2).toInt()))!!, 0f, 0f, paintBackgroundMask)

    }

    override fun setImageBitmap(bm: Bitmap?) {
        bitmapDefault = bm
        super.setImageBitmap(bm)
    }
}