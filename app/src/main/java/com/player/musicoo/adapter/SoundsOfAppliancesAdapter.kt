package com.player.musicoo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.activity.PlayDetailsActivity
import com.player.musicoo.bean.Audio
import com.player.musicoo.databinding.SoundsOfAppliancesLayoutBinding
import com.player.musicoo.util.convertMillisToMinutesAndSecondsString
import com.player.musicoo.util.getAudioDurationFromAssets

class SoundsOfAppliancesAdapter(
    private val context: Context,
    private val pdfList: List<Audio>,
) :
    RecyclerView.Adapter<SoundsOfAppliancesAdapter.PDFViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val binding =
            SoundsOfAppliancesLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return PDFViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val audio = pdfList[position]
        holder.bind(audio)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayDetailsActivity::class.java);
            intent.putExtra(PlayDetailsActivity.KEY_DETAILS_AUDIO, audio)
            context.startActivity(intent)
//            mediaPlayer.setDataSource(this, Uri.parse("file:///android_asset/${audio.file}"))
        }
    }

    override fun getItemCount(): Int = pdfList.size

    inner class PDFViewHolder(private val binding: SoundsOfAppliancesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(audio: Audio) {
            binding.apply {
                Glide.with(context)
                    .load("file:///android_asset/${audio.image}")
                    .into(image)
                name.text = audio.name
                val s = getAudioDurationFromAssets(context, audio.file)
                desc.text = convertMillisToMinutesAndSecondsString(s)

                if (App.currentPlayingAudio != null) {
                    if (App.currentPlayingAudio?.file == audio.file) {
                        playingLayout.visibility = View.VISIBLE
                        name.setTextColor(context.getColor(R.color.blue))
                        desc.setTextColor(context.getColor(R.color.blue))
                    } else {
                        playingLayout.visibility = View.GONE
                        name.setTextColor(context.getColor(R.color.black))
                        desc.setTextColor(context.getColor(R.color.black))
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