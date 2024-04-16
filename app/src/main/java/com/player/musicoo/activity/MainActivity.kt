package com.player.musicoo.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.bean.Audio
import com.player.musicoo.databinding.ActivityMainBinding
import com.player.musicoo.fragment.HomeFragment
import com.player.musicoo.fragment.ImportFragment
import com.player.musicoo.media.MediaControllerManager
import com.player.musicoo.util.convertMillisToMinutesAndSecondsString
import com.player.musicoo.util.getAudioDurationFromAssets


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mFragments: MutableList<Fragment> = ArrayList()
    private var currentIndex: Int = 0
    private var mCurrentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        initImmersionBar()
        initView()
    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(false)
            statusBarView(binding.view)
        }
    }

    private fun initView() {
        initClick()
        initFragment()
    }

    override fun onResume() {
        super.onResume()
        val currentPlayer = MediaControllerManager.getController()

        if (App.currentPlayingAudio == null) {
            binding.playingStatusLayout.visibility = View.GONE
        } else {
            binding.playingStatusLayout.visibility = View.VISIBLE
            val currentAudio = App.currentPlayingAudio

            Log.d("ocean","main currentAudio->$currentAudio")
            val maxProgress = try {
                getAudioDurationFromAssets(this, currentAudio?.file!!)
            } catch (e: Exception) {
                currentAudio?.duration
            }
            if (maxProgress != null) {
                binding.progressBar.setMaxProgress(maxProgress)
            }
            if (currentAudio?.image?.isNotEmpty() == true) {
                Glide.with(this)
                    .load("file:///android_asset/${currentAudio?.image}")
                    .into(binding.audioImg)
            } else {
                binding.audioImg.setImageResource(R.mipmap.musicoo_logo_img)
            }

            binding.name.text = currentAudio?.name
            binding.desc.text = currentAudio?.name
        }

        if (currentPlayer != null && currentPlayer.playbackState == Player.STATE_READY) {
            val isPlaying = currentPlayer.isPlaying
            updatePlayState(isPlaying)
            if (isPlaying) {
                updateProgressState()
            }
        }
    }

    private fun initClick() {
        binding.homeBtn.setOnClickListener {
            changeFragment(0)
            updateBtnState(0)
        }
        binding.importBtn.setOnClickListener {
            changeFragment(1)
            updateBtnState(1)
        }
        binding.playingStatusLayout.setOnClickListener {
            val currentAudio = App.currentPlayingAudio
            val duration = try {
                getAudioDurationFromAssets(
                    this, currentAudio?.file!!
                )
            } catch (e: Exception) {
                currentAudio?.duration!!
            }

            val audio = Audio(
                currentAudio?.name!!,
                currentAudio.file,
                currentAudio.image,
                duration,
                false
            )
            val intent = Intent(this, PlayDetailsActivity::class.java);
            intent.putExtra(PlayDetailsActivity.KEY_DETAILS_AUDIO, audio)
            startActivity(intent)
        }
        binding.alarmClockBtn.setOnClickListener {

        }

        binding.playBlackBtn.setOnClickListener {
            val currentPlayer = MediaControllerManager.getController()
            if (currentPlayer != null) {
                Log.d("ocean", "currentPlayer.playbackState->${currentPlayer.playbackState}")
                if (currentPlayer.playbackState == Player.STATE_READY) {
                    if (currentPlayer.isPlaying) {
                        currentPlayer.pause()
                        updatePlayState(false)
                    } else {
                        currentPlayer.play()
                        updatePlayState(true)
                    }
                    updateProgressState()
                } else {
                    MediaControllerManager.setupMedia(this@MainActivity, App.currentPlayingAudio!!,
                        object : Player.Listener {
                            override fun onPlayWhenReadyChanged(
                                playWhenReady: Boolean,
                                reason: Int
                            ) {
                                Log.d("ocean", "main2 onPlayWhenReadyChanged")
                                updatePlayState(playWhenReady)
                                updateProgressState()
                            }

                            override fun onPlaybackStateChanged(playbackState: Int) {
                                Log.d("ocean", "main2 onPlaybackStateChanged")
                                updateProgressState()
                            }
                        })
                }
            } else {
                Log.d("ocean", "main currentPlayer == null")
            }
        }
    }

    private fun updatePlayState(b: Boolean) {
        if (b) {
            binding.playStatusImg.setImageResource(R.drawable.playing_black_icon)
        } else {
            binding.playStatusImg.setImageResource(R.drawable.play_black_icon)
        }
    }

    private fun initFragment() {
        mFragments.clear()
        mFragments.add(HomeFragment())
        mFragments.add(ImportFragment())
        changeFragment(0)
        updateBtnState(0)
    }

    private fun changeFragment(index: Int) {
        currentIndex = index
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (null != mCurrentFragment) {
            ft.hide(mCurrentFragment!!)
        }
        var fragment = supportFragmentManager.findFragmentByTag(
            mFragments[currentIndex].javaClass.name
        )
        if (null == fragment) {
            fragment = mFragments[index]
        }
        mCurrentFragment = fragment

        if (!fragment.isAdded) {
            ft.add(R.id.frame_layout, fragment, fragment.javaClass.name)
        } else {
            ft.show(fragment)
        }
        ft.commit()
    }

    private fun updateBtnState(index: Int) {
        binding.apply {
            homeImg.setImageResource(
                when (index) {
                    0 -> R.drawable.home_select_icon
                    else -> R.drawable.home_unselect_icon
                }
            )
            importImg.setImageResource(
                when (index) {
                    1 -> R.drawable.import_select_icon
                    else -> R.drawable.import_unselect_icon
                }
            )
        }
    }

    private fun updateProgressState() {
        val currentPlayer = MediaControllerManager.getController()
        if (currentPlayer != null && currentPlayer.playbackState == Player.STATE_READY && currentPlayer.isPlaying) {
            progressHandler.removeCallbacksAndMessages(null)
            updatePlayState(currentPlayer.isPlaying)
            progressHandler.sendEmptyMessage(1)
        } else {
            progressHandler.removeCallbacksAndMessages(null)
        }
    }

    private val progressHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            val currentPlayer = MediaControllerManager.getController()
            if (currentPlayer != null && currentPlayer.playbackState == Player.STATE_READY && currentPlayer.isPlaying) {
                val currentPosition = currentPlayer.currentPosition
                binding.progressBar.setProgress(currentPosition)
                sendEmptyMessageDelayed(1, 1000)
            }
        }
    }

    private var backPressedTime: Long = 0
    private val backToast: Toast by lazy {
        Toast.makeText(baseContext, "Press again to exit", Toast.LENGTH_SHORT)
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            backToast.cancel()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}