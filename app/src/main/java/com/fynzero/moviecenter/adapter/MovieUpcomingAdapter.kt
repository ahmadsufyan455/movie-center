package com.fynzero.moviecenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.MovieModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item_upcoming.view.*


class MovieUpcomingAdapter(private val movieUpcomingList: ArrayList<MovieModel>) :
    RecyclerView.Adapter<MovieUpcomingAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallBack(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun seData(upcomingList: ArrayList<MovieModel>) {
        movieUpcomingList.clear()
        movieUpcomingList.addAll(upcomingList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item_upcoming, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = movieUpcomingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movieUpcomingList[position])

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(movieUpcomingList[holder.adapterPosition])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterURL = "https://image.tmdb.org/t/p/w185"
        fun bind(movieUpcoming: MovieModel) {
            with(itemView) {
                Picasso.get().load(posterURL + movieUpcoming.poster).into(img_poster)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(upcoming: MovieModel)
    }
}