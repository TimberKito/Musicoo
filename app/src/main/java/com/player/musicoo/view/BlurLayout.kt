package com.player.musicoo.view

import android.content.Context
import android.graphics.*
import android.renderscript.*
import android.util.AttributeSet
import android.widget.FrameLayout

class BlurLayout : FrameLayout {
    private var blurRadius = 25f // 默认模糊半径
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

        // 创建一个 Bitmap 用于绘制模糊效果
        val blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 使用 RenderScript 处理模糊效果
        val rsContext = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rsContext, originalBitmap)
        val output = Allocation.createTyped(
            rsContext,
            Type.createXY(
                rsContext,
                Element.RGBA_8888(rsContext),
                originalBitmap.width,
                originalBitmap.height
            )
        )
        val script = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
        script.setInput(input)
        script.setRadius(blurRadius)
        script.forEach(output)
        output.copyTo(blurredBitmap)
        // 绘制原始内容
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        // 创建一个画笔，用于绘制模糊区域
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        // 定义模糊区域，即底部的 50dp
        val blurHeight = dpToPx(50).toInt()
        val blurRect = Rect(0, height - blurHeight, width, height)

        // 绘制模糊效果
        canvas.drawBitmap(blurredBitmap, blurRect, blurRect, paint)

        // 绘制一个具有指定颜色的矩形作为背景
        paint.color = Color.parseColor("#99000000") // 半透明黑色
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER) // 使用 SRC_OVER 模式覆盖绘制
        paint.isAntiAlias = true
        val rectF = RectF(blurRect)
        canvas.drawRect(blurRect,paint)
//        canvas.drawRoundRect(rectF,cornerRadius, cornerRadius, paint)

        // 定义整个布局的矩形区域
        val layoutRect = Rect(0, 0, width, height)
        val rectFAll = RectF(layoutRect)

        paint.color = Color.parseColor("#00000000") // 半透明黑色
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER) // 使用 SRC_OVER 模式覆盖绘制
        paint.isAntiAlias = true
        canvas.drawRoundRect(rectFAll,cornerRadius, cornerRadius, paint)

        input.destroy()
        output.destroy()
        script.destroy()
        rsContext.destroy()
    }

    private fun dpToPx(dp: Int): Float {
        val density = resources.displayMetrics.density
        return (dp * density)
    }
}
