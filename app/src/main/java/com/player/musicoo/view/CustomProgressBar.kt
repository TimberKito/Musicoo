package com.player.musicoo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class CustomProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var progress = 0 // 当前进度
    private val maxProgress = 100 // 最大进度
    private val progressBarHeight = 20f // 进度条高度
    private val cornerRadius = 10f // 圆角半径
    private val backgroundColor = Color.parseColor("#26FFFFFF")
    private val startColor = Color.parseColor("#FF1CC8EE") // 起始颜色
    private val middleColor = Color.parseColor("#FF69FE73") // 中间颜色
    private val endColor = Color.parseColor("#FFCBD64B") // 结束颜色
    private val paint = Paint()
    private val paintTow = Paint()

    init {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paintTow.style = Paint.Style.FILL
        paintTow.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制底色矩形
        paint.shader = null // 重置着色器
        paint.color = backgroundColor
        val backgroundRect = RectF(0f, (height / 2 - progressBarHeight / 2), width.toFloat(), (height / 2 + progressBarHeight / 2))
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, paint)


        // 计算进度条的宽度
        val progressBarWidth = (width * progress.toFloat() / maxProgress).toInt()

        // 创建颜色渐变对象
        val gradient = LinearGradient(0f, 0f, width.toFloat(), 0f, intArrayOf(startColor, middleColor, endColor), null, Shader.TileMode.CLAMP)
        paintTow.shader = gradient

        // 绘制带圆角的进度条矩形
        val rect = RectF(0f, (height / 2 - progressBarHeight / 2), progressBarWidth.toFloat(), (height / 2 + progressBarHeight / 2))
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paintTow)
    }

    fun getProgress():Int{
        return progress
    }

    // 设置进度
    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate() // 请求重绘
    }
}
