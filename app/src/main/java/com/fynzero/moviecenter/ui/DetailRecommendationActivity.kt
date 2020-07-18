package com.fynzero.moviecenter.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.MovieAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_recommendation.*
import kotlinx.android.synthetic.main.activity_detail_recommendation.btn_tryAgain
import kotlinx.android.synthetic.main.activity_detail_recommendation.con_failed
import kotlinx.android.synthetic.main.activity_detail_recommendation.textView4
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder

class DetailRecommendationActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        const val url_poster = "https://image.tmdb.org/t/p/w185"
        const val url_backdrop = "https://image.tmdb.org/t/p/w400"
        const val apiKey = BuildConfig.API_KEY
        private val TAG = DetailRecommendationActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_recommendation)

        supportActionBar?.hide()

        getDetail()
        getCast()
        getRecommendation()

        ic_back_rec.setOnClickListener {
            finish()
        }
    }

    private fun getDetail() {
        val movie = intent.getParcelableExtra<MovieModel>(EXTRA_DETAIL)
        val genres = ArrayList<String>()
        val movieId = movie?.id
        val url = "https://api.themoviedb.org/3/movie/${movieId}?api_key=${apiKey}&language=en-US"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                progressBar_rec.visibility = View.GONE
                view_recommendation.visibility = View.GONE
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val jsonObject = JSONObject(result)
                    movie?.id = jsonObject.getInt("id")
                    movie?.title = jsonObject.getString("title")
                    movie?.poster = jsonObject.getString("poster_path")
                    movie?.backdrop = jsonObject.getString("backdrop_path")
                    movie?.date = jsonObject.getString("release_date")
                    movie?.rating = jsonObject.getDouble("vote_average")
                    movie?.overview = jsonObject.getString("overview")
                    val jsonArray = jsonObject.getJSONArray("genres")
                    for (i in 0 until jsonArray.length()) {
                        val genreObject = jsonArray.getJSONObject(i)
                        val genre = genreObject.getString("name")
                        genres.add(genre)
                    }

                    tv_title_rec.text = movie?.title
                    tv_date_rec.text = movie?.date

                    val builder = StringBuilder()
                    var prefix = ""
                    for (i in genres) {
                        builder.append(prefix)
                        prefix = ", "
                        builder.append(i)
                    }

                    tv_genre_rec.text = builder

                    tv_rating_rec.text = movie?.rating.toString()

                    tv_trailer_rec.setOnClickListener {
                        val trailer =
                            Intent(this@DetailRecommendationActivity, TrailerActivity::class.java)
                        trailer.putExtra(TrailerActivity.EXTRA_KEY, movie)
                        startActivity(trailer)
                    }

                    tv_overview_rec.text = movie?.overview
                    Picasso.get().load(url_poster + movie?.poster).into(img_poster_rec)
                    Picasso.get().load(url_backdrop + movie?.backdrop).into(img_backdrop_rec)

                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                progressBar_rec.visibility = View.GONE
                con_failed.visibility = View.VISIBLE
                btn_tryAgain.setOnClickListener {
                    finish()
                    startActivity(intent)
                }
            }

        })
    }

    private fun getCast() {
        val movie = intent.getParcelableExtra<MovieModel>(EXTRA_DETAIL)
        val movieId = movie?.id
        val url =
            "https://api.themoviedb.org/3/movie/${movieId}/credits?api_key=$apiKey"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val cast = ArrayList<String>()
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("cast")

                    for (i in 0 until 7) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("name")
                        cast.add(name)
                    }

                    val builder = StringBuilder()
                    var prefix = ""
                    for (i in cast) {
                        builder.append(prefix)
                        prefix = ", "
                        builder.append(i)
                    }

                    tv_cast_rec.text = builder

                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("onFailure", error?.message.toString())
            }

        })
    }

    private fun getRecommendation() {
        val recommendations = ArrayList<MovieModel>()
        val movie = intent.getParcelableExtra<MovieModel>(EXTRA_DETAIL)
        val movieId = movie?.id
        val url =
            "https://api.themoviedb.org/3/movie/$movieId/recommendations?api_key=$apiKey&language=en-US&page=1"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val recommendationMovie = MovieModel()
                        recommendationMovie.id = jsonObject.getInt("id")
                        recommendationMovie.title = jsonObject.getString("title")
                        recommendationMovie.poster = jsonObject.getString("poster_path")
                        recommendationMovie.date = jsonObject.getString("release_date")
                        recommendationMovie.rating = jsonObject.getDouble("vote_average")
                        recommendations.add(recommendationMovie)
                    }

                    if (jsonArray.length() == 0) {
                        textView4.visibility = View.INVISIBLE
                    }

                    val recommendationAdapter = MovieAdapter(recommendations)

                    rv_recommendation_rec.layoutManager = LinearLayoutManager(
                        this@DetailRecommendationActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    rv_recommendation_rec.adapter = recommendationAdapter
                    rv_recommendation_rec.setHasFixedSize(true)
                    recommendationAdapter.notifyDataSetChanged()

                    recommendationAdapter.setOnItemClickCallback(object :
                        MovieAdapter.OnItemClickCallBack {
                        override fun onItemClicked(movie: MovieModel) {
                            val toDetail = Intent(
                                this@DetailRecommendationActivity,
                                DetailMovieActivity::class.java
                            )
                            toDetail.putExtra(DetailMovieActivity.EXTRA_DETAIL, movie)
                            startActivity(toDetail)
                        }
                    })

                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                Log.d("onFailure", error?.message.toString())
            }

        })
    }
}