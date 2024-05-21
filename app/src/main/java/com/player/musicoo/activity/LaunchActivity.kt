package com.player.musicoo.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.databinding.ActivityLaunchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchActivity : BaseActivity() {
    private lateinit var binding: ActivityLaunchBinding
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val countTime: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initImmersionBar()
        initTimer()
    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(true)
//            statusBarView(binding.view)
        }
    }
    private fun initTimer() {
        coroutineScope.launch {
            delay(countTime)
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}