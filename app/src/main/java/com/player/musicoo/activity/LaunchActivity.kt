package com.player.musicoo.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.databinding.ActivityLaunchBinding

class LaunchActivity : BaseActivity() {
    private lateinit var binding: ActivityLaunchBinding
    private val totalTime = 3000 // 5秒
    private val interval = 50 // 更新间隔，毫秒
    private val steps = totalTime / interval
    private val progressPerStep = 100 / steps
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTimer()

        immersionBar {
            fullScreen(true)
            statusBarDarkFont(false)
        }
    }

    private fun initTimer() {
        val progressBar = binding.customProgressBar
        val timer = object : CountDownTimer(totalTime.toLong(), interval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                progressBar.setProgress(progressBar.getProgress() + progressPerStep)
            }

            override fun onFinish() {
                progressBar.setProgress(100)
                toMainActivity()
            }
        }

        timer.start()
    }

    private fun toMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}