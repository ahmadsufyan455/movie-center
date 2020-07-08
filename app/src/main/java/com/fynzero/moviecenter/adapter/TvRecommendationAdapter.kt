package com.fynzero.moviecenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.TvModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tv_item_recommendation.view.*

class TvRecommendationAdapter(private val tvRecommendationList: ArrayList<TvModel>) :
    RecyclerView.Adapter<TvRecommendationAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tv_item_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = tvRecommendationList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tvRecommendationList[position])

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(tvRecommendationList[holder.adapterPosition])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val posterURL = "https://image.tmdb.org/t/p/w185"
        fun bind(tvModel: TvModel) {
            with(itemView) {
                tv_name_rec.text = tvModel.name
                tv_date_rec.text = tvModel.date
                tv_rating_rec.text = tvModel.rating.toString()
                Picasso.get().load(posterURL + tvModel.poster).into(img_poster_rec)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(tvModel: TvModel)
    }
}