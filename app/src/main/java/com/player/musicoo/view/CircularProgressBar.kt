package com.player.musicoo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = dpToPx(3) // 设置圆环的宽度为20像素
        color = 0xFFFFFFFF.toInt() // 设置圆环的颜色为白色
    }

    private var backgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = dpToPx(3)
        color = 0x00000000
    }

    private var progress: Long = 0 // 进度值，默认为0
    private var maxProgress: Long = 100 // 最大进度值，默认为100

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (min(width, height) - progressPaint.strokeWidth) / 2f

        // 绘制圆环背景
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // 绘制圆环进度
        val startAngle = -90f
        val sweepAngle = -360f * progress / maxProgress
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            startAngle,
            sweepAngle,
            false,
            progressPaint
        )
    }

    fun setProgress(progress: Long) {
        // 确保进度值在有效范围内
        this.progress = progress.coerceIn(0, maxProgress)
        invalidate() // 重新绘制视图
    }

    fun setMaxProgress(maxProgress: Long) {
        // 设置最大进度值
        this.maxProgress = maxProgress
        // 更新当前进度，确保当前进度在有效范围内
        this.progress = this.progress.coerceIn(0, maxProgress)
        invalidate() // 重新绘制视图
    }

    private fun dpToPx(dp: Int): Float {
        val density = resources.displayMetrics.density
        return (dp * density)
    }
}

