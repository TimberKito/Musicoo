package com.player.musicoo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.player.musicoo.R
import com.player.musicoo.activity.PlayDetailsActivity
import com.player.musicoo.bean.Audio

class CollectAdapter(private val context: Context) : RecyclerView.Adapter<CollectAdapter.ItemViewHolder>() {

    private var collectDataList: List<Audio> = emptyList()

    fun updateData(dataList: List<Audio>) {
        collectDataList = dataList
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgItemPer = itemView.findViewById<ImageView>(R.id.image)
        val textItemName = itemView.findViewById<TextView>(R.id.tv_like_name)
        val rootCard = itemView.findViewById<LinearLayout>(R.id.like_layout_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_collect, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return collectDataList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        collectDataList[position].run {
            holder.textItemName.text = name

            Glide.with(context).load("file:///android_asset/${image}")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imgItemPer)

            holder.rootCard.setOnClickListener() {
                val intent = Intent(context, PlayDetailsActivity::class.java);
                intent.putExtra(PlayDetailsActivity.KEY_DETAILS_AUDIO, this)
                context.startActivity(intent)
            }
        }
    }
}