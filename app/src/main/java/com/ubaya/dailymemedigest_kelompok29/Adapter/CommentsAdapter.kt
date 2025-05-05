package com.ubaya.dailymemedigest_kelompok29.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.ubaya.dailymemedigest_kelompok29.Data.Comment
import com.ubaya.dailymemedigest_kelompok29.MemeDetailActivity
import com.ubaya.dailymemedigest_kelompok29.R

class CommentsAdapter (private var context: Context, private var items: List<Comment>) :
RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = items[position]

        holder.tvName.text = comment.name
        holder.tvDate.text = comment.date
        holder.tvComment.text = comment.comment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)

        return ViewHolder(itemView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var tvDate: TextView
        var tvComment: TextView

        init {
            tvName = view.findViewById(R.id.tv_name)
            tvDate = view.findViewById(R.id.tv_date)
            tvComment = view.findViewById(R.id.tv_comment)
        }
    }
}