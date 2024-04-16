package com.player.musicoo.view

import android.content.Context
import android.graphics.*
import android.renderscript.*
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.RelativeLayout

class RadiusLayout : RelativeLayout {
    private val cornerRadius = dpToPx(18) // 圆角半径
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchDraw(canvas: Canvas) {
        // 创建一个 Bitmap 用于绘制原始内容
        val originalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val originalCanvas = Canvas(originalBitmap)
        // 调用父类的 dispatchDraw() 方法，把所有的子 View 绘制到这个 Bitmap 上
        super.dispatchDraw(originalCanvas)
        // 绘制原始内容
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        // 创建一个画笔
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        // 定义整个布局的矩形区域
        val layoutRect = Rect(0, 0, width, height)
        val rectF = RectF(layoutRect)
        // 绘制具有圆角的矩形作为背景
        paint.color = Color.parseColor("#CCffffff") // 半透明黑色
        paint.isAntiAlias = true
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }

    private fun dpToPx(dp: Int): Float {
        val density = resources.displayMetrics.density
        return (dp * density)
    }
}
