package com.player.musicoo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.activity.PlayDetailsActivity
import com.player.musicoo.bean.Audio
import com.player.musicoo.databinding.RealHumanVoiceLayoutBinding
import com.player.musicoo.util.convertMillisToMinutesAndSecondsString
import com.player.musicoo.util.getAudioDurationFromAssets

class RealHumanVoiceAdapter(
    private val context: Context,
    private val pdfList: List<Audio>,
) :
    RecyclerView.Adapter<RealHumanVoiceAdapter.PDFViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val binding =
            RealHumanVoiceLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return PDFViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val audio = pdfList[position]
        holder.bind(audio)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayDetailsActivity::class.java);
            intent.putExtra(PlayDetailsActivity.KEY_DETAILS_AUDIO, audio)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pdfList.size

    inner class PDFViewHolder(private val binding: RealHumanVoiceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(audio: Audio) {
            binding.apply {
                Glide.with(context)
                    .load("file:///android_asset/${audio.image}")
                    .into(realImg)
                name.text = audio.name
                val s = getAudioDurationFromAssets(context, audio.file)
                desc.text = convertMillisToMinutesAndSecondsString(s)

                if (App.currentPlayingAudio != null) {
                    if (App.currentPlayingAudio?.file == audio.file) {
                        stateImg.setImageResource(R.drawable.playing_white_icon)
                        name.setTextColor(context.getColor(R.color.green))
                        desc.setTextColor(context.getColor(R.color.green))
                    } else {
                        stateImg.setImageResource(R.drawable.play_white_icon)
                        name.setTextColor(context.getColor(R.color.white))
                        desc.setTextColor(context.getColor(R.color.white))
                    }
                }
            }
        }
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}