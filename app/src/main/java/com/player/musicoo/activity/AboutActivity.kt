package com.player.musicoo.activity

import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.databinding.ActivityAboutBinding
import com.player.musicoo.util.getAppVersion

class AboutActivity : BaseActivity() {

    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        immersionBar {
            statusBarDarkFont(false)
            statusBarView(binding.view)
        }
        binding.versionTv.text = "Version: " + getAppVersion(this)
        binding.backBtn.setOnClickListener {
            finish()
        }
    }


}