package com.ubaya.dailymemedigest_kelompok29.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.ubaya.dailymemedigest_kelompok29.Data.Leaderboard
import com.ubaya.dailymemedigest_kelompok29.R

class LeaderboardAdapter(private var context: Context, private var items: List<Leaderboard>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val leade = items[position]

        holder.tvName.text = if(leade.fullName != "null") leade.fullName else "No Name"
        holder.tvLike.text = leade.like.toString()

        leade.urlImage.let {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgProfile)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leader_board, parent, false)

        return ViewHolder(itemView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var tvLike: TextView
        var imgProfile: ImageView

        init {
            tvName = view.findViewById(R.id.tv_name)
            tvLike = view.findViewById(R.id.tv_likes)
            imgProfile = view.findViewById(R.id.img_profile)
        }
    }
}