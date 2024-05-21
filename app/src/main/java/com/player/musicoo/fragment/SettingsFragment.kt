package com.player.musicoo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.R
import com.player.musicoo.activity.AboutActivity
import com.player.musicoo.databinding.ActivitySettingsBinding
import com.player.musicoo.databinding.FragmentSettingBinding
import com.player.musicoo.util.PRIVACY_POLICY_URL
import com.player.musicoo.util.TERMS_OF_SERVICE_URL
import com.player.musicoo.util.openPrivacyPolicy
import com.player.musicoo.util.openTermsOfService
import com.player.musicoo.util.sendFeedback
import com.player.musicoo.util.shareApp

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersionBar()
        initView()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initImmersionBar()
        }
    }

    override fun onResume() {
        super.onResume()
        initImmersionBar()
    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(true)
//            statusBarView(binding.view)
        }
    }

    private fun initView() {
//        binding.backBtn.setOnClickListener {
//            finish()
//        }
        binding.aboutBtn.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
        binding.feedbackBtn.setOnClickListener {
            sendFeedback(requireContext(), "motaleb3024@gmail.com", getString(R.string.app_name))
        }
        binding.shareBtn.setOnClickListener {
            shareApp(requireContext())
        }
        binding.ppBtn.setOnClickListener {
            openPrivacyPolicy(requireContext(), PRIVACY_POLICY_URL)
        }
        binding.tosBtn.setOnClickListener {
            openTermsOfService(requireContext(), TERMS_OF_SERVICE_URL)
        }
    }
}