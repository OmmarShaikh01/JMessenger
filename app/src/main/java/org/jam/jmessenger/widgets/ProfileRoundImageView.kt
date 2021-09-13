package org.jam.jmessenger.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import org.jam.jmessenger.R


/**
 * Profile round image view
 *
 * @constructor
 *
 * @param context
 * @param attrs
 */
class ProfileRoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs) {

    // START REGION: Declarations
    private val borderWidth = 6f
    private val borderColor = ResourcesCompat.getColor(resources, R.color.secondaryColor, null)
    private val backColor = ResourcesCompat.getColor(resources, R.color.primaryDarkColor, null)

    private var bitmapDefault = BitmapFactory.decodeResource(resources, R.drawable.ic_user_default_profile)
    private var bitmapDefaultMask = ResourcesCompat.getDrawable(resources, R.drawable.uprofile_mask, null)!!.toBitmap()

    private val bitmapShader = BitmapShader(bitmapDefaultMask, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    private val paint = Paint().apply { isAntiAlias = true }
    private val paintBorder = Paint().apply { isAntiAlias = true; color = borderColor}
    private val paintBackground = Paint().apply { isAntiAlias = true; color = backColor }
    private val paintBackgroundMask = Paint().apply { isAntiAlias = true; shader = bitmapShader; style = Paint.Style.FILL}
    // END REGION


    // START REGION: DrawMethods
    /**
     * Make Circle Bitmap
     *
     * @param bitmap: Bitmap
     * @return Bitmap?
     */
    private fun makeCircle(bitmap: Bitmap?): Bitmap? {
        if (bitmap != null) {
            val bitmapHeight = bitmap.height
            val radius = ((bitmapHeight - 14) / 2).toFloat()
            val center = ((bitmapHeight) / 2).toFloat()
            val basePaint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            }

            val baseBitmap = Bitmap.createBitmap(bitmapHeight, bitmapHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(baseBitmap)
            canvas.drawCircle(center, center, radius, paint) // Draws Base Circle
            canvas.drawBitmap(bitmap, 0f, 0f, basePaint) // Draws Profile Bitmap Over it
            return baseBitmap
        } else {
            return null
        }
    }
    // END REGION


    // START REGION: Overrides
    override fun getScaleType(): ScaleType = super.getScaleType() ?: ScaleType.CENTER_CROP

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

    override fun onDraw(canvas: Canvas) {
        val baseRadius = this.height.toFloat() / 2
        // Draw Border
        canvas.drawCircle(baseRadius, baseRadius, baseRadius, paintBorder)
        // Draw Circle background
        canvas.drawCircle(baseRadius, baseRadius, baseRadius - borderWidth, paintBackground)
        // Draws Pixmap
        canvas.drawBitmap(
            makeCircle(bitmapDefault.scale((baseRadius * 2).toInt(), (baseRadius * 2).toInt()))!!,
            0f,
            0f,
            paintBackgroundMask
        )
    }

    override fun setImageBitmap(bm: Bitmap?) {
        bitmapDefault = bm
        super.setImageBitmap(bm)
    }
    // END REGION
}