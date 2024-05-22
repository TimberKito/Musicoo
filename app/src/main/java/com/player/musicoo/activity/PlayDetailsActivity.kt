package com.player.musicoo.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.slider.Slider.OnChangeListener
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.bean.Audio
import com.player.musicoo.database.AppDatabase
import com.player.musicoo.databinding.ActivityPlayDetails2Binding
import com.player.musicoo.media.MediaControllerManager
import com.player.musicoo.util.containsContent
import com.player.musicoo.util.convertMillisToMinutesAndSecondsString
import com.player.musicoo.util.getAudioDurationFromAssets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

class PlayDetailsActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val KEY_DETAILS_AUDIO = "key_details_audio"
    }

    private lateinit var binding: ActivityPlayDetails2Binding
    private var audio: Audio? = null
    private var rotationAnimator: ValueAnimator? = null


    private val database = Room.databaseBuilder(
        App.appContext, AppDatabase::class.java, "local_audio_viewer_database"
    ).build()

    private val audioFileDao = database.localAudioDao()

    @SuppressLint("ForegroundServiceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayDetails2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initImmersionBar()
        audio = intent.getSerializableExtra(KEY_DETAILS_AUDIO) as Audio?
        if (audio == null) {
            onBackPressed()
            Toast.makeText(this, getString(R.string.data_error), Toast.LENGTH_SHORT).show()
        }
        binding.playLike.isSelected = audio?.collect!!
        Log.d("ocean", "PlayDetailsActivity audio->$audio")
        initView()

    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(true)
//            statusBarView(binding.view)
        }
    }

    @OptIn(UnstableApi::class)
    private fun initView() {

        if (audio?.image?.isNotEmpty() == true) {
            Glide.with(this)
                .load("file:///android_asset/${audio?.image}")
                .into(binding.image)
            val bitmap = loadBitmapFromAsset(this, audio?.image!!)
            val blurredBitmap = applyGaussianBlur(bitmap, 25f, this)
//            binding.imageView.setImageBitmap(blurredBitmap)

        } else {
            binding.image.setImageResource(R.mipmap.musicoo_logo_img)
            val bitmap = loadBitmapFromAsset(R.mipmap.musicoo_logo_img)
            val blurredBitmap = applyGaussianBlur(bitmap, 25f, this)
//            binding.imageView.setImageBitmap(blurredBitmap)
        }
        binding.seekBar.value = 0f
        binding.title.text = audio?.name
        binding.nameTv.text = audio?.name
        binding.descTv.text = audio?.name

        if (containsContent(audio?.file!!)) {
            binding.totalDurationTv.text = convertMillisToMinutesAndSecondsString(audio?.duration!!)
            binding.seekBar.valueTo = audio?.duration!!.toFloat()
        } else {
            val s = getAudioDurationFromAssets(this, audio?.file!!)
            binding.totalDurationTv.text = convertMillisToMinutesAndSecondsString(s)

            binding.seekBar.valueTo = s.toFloat()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }



        binding.playLike.setOnClickListener(this)

        val currentPlayer = MediaControllerManager.getController()
        currentPlayer?.addListener(object : Player.Listener {
            override fun onPlayWhenReadyChanged(
                playWhenReady: Boolean,
                reason: Int
            ) {
                Log.d("ocean", "details onPlayWhenReadyChanged")
                updatePlayState(playWhenReady, "playWhenReady")
                updateProgressState()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d("ocean", "details onPlaybackStateChanged")
                updateProgressState()
            }
        })
        binding.playImg.setOnClickListener {
            if (currentPlayer != null) {
                if (currentPlayer.isPlaying) {
                    currentPlayer.pause()
                    updatePlayState(false, "click")
                } else {
                    currentPlayer.play()
                    updatePlayState(true, "click")
                }
                updateProgressState()
            }
        }
        MediaControllerManager.setupMedia(this,
            audio!!,
            object : Player.Listener {
                override fun onPlayWhenReadyChanged(
                    playWhenReady: Boolean,
                    reason: Int
                ) {
                    updatePlayState(playWhenReady, "playWhenReady")
                    updateProgressState()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateProgressState()
                }
            })

        binding.seekBar.addOnChangeListener(OnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                if (currentPlayer != null) {
                    currentPlayer.seekTo(value.toLong())
                    val ss = currentPlayer.isPlaying
                    if (!ss) {
                        currentPlayer.play()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val currentPlayer = MediaControllerManager.getController()
        if (currentPlayer != null && currentPlayer.playbackState == Player.STATE_READY) {
            val isPlaying = currentPlayer.isPlaying
            updatePlayState(isPlaying, "onResume")
            if (isPlaying) {
                updateProgressState()
            } else {
                val currentPosition = currentPlayer.currentPosition
                val currentString = convertMillisToMinutesAndSecondsString(currentPosition)
                binding.progressDurationTv.text = currentString
                binding.seekBar.value = currentPosition.toFloat()
            }
        }
    }


    private fun updatePlayState(b: Boolean, string: String) {
        if (b) {
            binding.playImg.setImageResource(R.drawable.playing_green_icon)
        } else {
            binding.playImg.setImageResource(R.drawable.play_green_icon)
        }
    }

    private fun loadBitmapFromAsset(context: Context, filePath: String): Bitmap {
        return try {
            val inputStream = context.assets.open(filePath)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Could not load bitmap from asset")
        }
    }

    private fun loadBitmapFromAsset(id: Int): Bitmap {
        return try {
            val inputStream: InputStream = resources.openRawResource(id)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Could not load bitmap from asset")
        }
    }

    private fun applyGaussianBlur(inputBitmap: Bitmap, radius: Float, context: Context): Bitmap {
        val rsContext = RenderScript.create(context)
        val outputBitmap =
            Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
        val blurScript = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))
        val tmpIn = Allocation.createFromBitmap(rsContext, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rsContext, outputBitmap)
        blurScript.setRadius(radius)
        blurScript.setInput(tmpIn)
        blurScript.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        rsContext.finish()
        return outputBitmap
    }

    override fun onPause() {
        super.onPause()
        finishWithAnimation()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun finishWithAnimation() {
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressHandler.removeCallbacksAndMessages(null)
        rotationAnimator?.cancel()
    }

    private fun updateProgressState() {
        val currentPlayer = MediaControllerManager.getController()
        if (currentPlayer != null && currentPlayer.playbackState == Player.STATE_READY && currentPlayer.isPlaying) {
            updatePlayState(currentPlayer.isPlaying, "playWhenReady")
            progressHandler.removeCallbacksAndMessages(null)
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
                val currentString = convertMillisToMinutesAndSecondsString(currentPosition)
                binding.progressDurationTv.text = currentString

                binding.seekBar.value = currentPosition.toFloat()

                sendEmptyMessageDelayed(1, 1000)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.playLike -> {
                if (!binding.playLike.isSelected) {
                    binding.playLike.isSelected = !binding.playLike.isSelected
                    Toast.makeText(
                        this@PlayDetailsActivity, "You have collected this sound.", Toast.LENGTH_SHORT
                    ).show()
                    CoroutineScope(Dispatchers.IO).launch {

                        audio?.apply {
                            collect = binding.playLike.isSelected
                        }?.let { it1 -> audioFileDao.insertAudioFile(it1) }
                    }

                } else {
                    binding.playLike.isSelected = !binding.playLike.isSelected
                    Toast.makeText(
                        this@PlayDetailsActivity, "You have unfavorite this sound.", Toast.LENGTH_SHORT
                    ).show()
                    CoroutineScope(Dispatchers.IO).launch {

                        audio?.apply {
                            collect = binding.playLike.isSelected
                        }?.let { it1 -> audioFileDao.deleteAudioFile(it1) }
                    }
                }
            }
        }
    }

}