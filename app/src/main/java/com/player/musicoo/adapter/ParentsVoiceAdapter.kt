package com.player.musicoo.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.activity.PlayDetailsActivity
import com.player.musicoo.bean.Audio
import com.player.musicoo.databinding.ParentsVoiceLayoutBinding
import com.player.musicoo.util.containsContent
import com.player.musicoo.util.convertMillisToMinutesAndSecondsString
import com.player.musicoo.util.getAudioDurationFromAssets

class ParentsVoiceAdapter(
    private val context: Context,
    private val pdfList: List<Audio>,
) :
    RecyclerView.Adapter<ParentsVoiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ParentsVoiceLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val audio = pdfList[position]
        holder.bind(audio)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayDetailsActivity::class.java);
            intent.putExtra(PlayDetailsActivity.KEY_DETAILS_AUDIO, audio)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pdfList.size

    inner class ViewHolder(private val binding: ParentsVoiceLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(audio: Audio) {
            binding.apply {
                image.setImageResource(R.mipmap.musicoo_logo_img)
                name.text = audio.name
                name.requestFocus()
                if (containsContent(audio.file)) {
                    desc.text = convertMillisToMinutesAndSecondsString(audio.duration)
                } else {
                    val s = getAudioDurationFromAssets(context, audio.file)
                    desc.text = convertMillisToMinutesAndSecondsString(s)
                }

                if (App.currentPlayingAudio != null) {
                    if (App.currentPlayingAudio?.file == audio.file) {
                        playingLayout.visibility = View.VISIBLE
                        name.setTextColor(context.getColor(R.color.blue))
                        desc.setTextColor(context.getColor(R.color.blue))
                    } else {
                        playingLayout.visibility = View.GONE
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