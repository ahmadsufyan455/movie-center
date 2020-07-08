package com.fynzero.moviecenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.MovieModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item_popular.view.*

class MovieAdapter(private val movieList: ArrayList<MovieModel>) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private lateinit var onItemClickCallBack: OnItemClickCallBack

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    fun setData(movies: ArrayList<MovieModel>) {
        movieList.clear()
        movieList.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item_popular, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movieList[position])

        holder.itemView.setOnClickListener {
            onItemClickCallBack.onItemClicked(movieList[holder.adapterPosition])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterURL = "https://image.tmdb.org/t/p/w185"
        fun bind(movieModel: MovieModel) {
            with(itemView) {
                tv_title.text = movieModel.title
                tv_date.text = movieModel.date
                tv_rating.text = movieModel.rating.toString()
                Picasso.get().load(posterURL + movieModel.poster).into(img_poster)
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(movie: MovieModel)
    }
}