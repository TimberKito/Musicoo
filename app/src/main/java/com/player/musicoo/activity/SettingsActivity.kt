package com.player.musicoo.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.R
import com.player.musicoo.databinding.ActivitySettingsBinding
import com.player.musicoo.util.PRIVACY_POLICY_URL
import com.player.musicoo.util.TERMS_OF_SERVICE_URL
import com.player.musicoo.util.openPrivacyPolicy
import com.player.musicoo.util.openTermsOfService
import com.player.musicoo.util.sendFeedback
import com.player.musicoo.util.shareApp

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initImmersionBar()
        initView()
    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(true)
//            statusBarView(binding.view)
        }
    }

    private fun initView() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.aboutBtn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.feedbackBtn.setOnClickListener {
            sendFeedback(this, "motaleb3024@gmail.com", getString(R.string.app_name))
        }
        binding.shareBtn.setOnClickListener {
            shareApp(this)
        }
        binding.ppBtn.setOnClickListener {
            openPrivacyPolicy(this, PRIVACY_POLICY_URL)
        }
        binding.tosBtn.setOnClickListener {
            openTermsOfService(this, TERMS_OF_SERVICE_URL)
        }
    }

}