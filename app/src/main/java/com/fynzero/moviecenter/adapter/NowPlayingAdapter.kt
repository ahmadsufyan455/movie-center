package com.fynzero.moviecenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.MovieModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item_now_playing.view.*

class NowPlayingAdapter(private val nowPlayingList: ArrayList<MovieModel>) :
    RecyclerView.Adapter<NowPlayingAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(movies: ArrayList<MovieModel>) {
        nowPlayingList.clear()
        nowPlayingList.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item_now_playing, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = nowPlayingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(nowPlayingList[position])

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(nowPlayingList[holder.adapterPosition])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val backdropURL = "https://image.tmdb.org/t/p/w400"
        fun bind(movieModel: MovieModel) {
            with(itemView) {
                tv_title_now_playing.text = movieModel.title
                Picasso.get().load(backdropURL + movieModel.backdrop).into(img_poster_now_playing)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(movieModel: MovieModel)
    }
}