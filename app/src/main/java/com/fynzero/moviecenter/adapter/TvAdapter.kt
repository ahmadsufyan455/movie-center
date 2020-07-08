package com.fynzero.moviecenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.TvModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_item_popular.view.*

class TvAdapter(private val tvList: ArrayList<TvModel>) :
    RecyclerView.Adapter<TvAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(tvShows: ArrayList<TvModel>) {
        tvList.clear()
        tvList.addAll(tvShows)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tv_item_popular, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = tvList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tvList[position])

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(tvList[holder.adapterPosition])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterURL = "https://image.tmdb.org/t/p/w185"
        fun bind(tvModel: TvModel) {
            with(itemView) {
                tv_title.text = tvModel.name
                tv_date.text = tvModel.date
                tv_rating.text = tvModel.rating.toString()
                Picasso.get().load(posterURL + tvModel.poster).into(img_poster)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(tvShow: TvModel)
    }
}