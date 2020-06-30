package com.fynzero.moviecenter.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.MovieAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_movie.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder

class DetailMovieActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        const val url_poster = "https://image.tmdb.org/t/p/w185"
        const val url_backdrop = "https://image.tmdb.org/t/p/w400"
        private val TAG = DetailMovieActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_movie)

        supportActionBar?.hide()

        getDetail()
        getCast()
        getRecommendation()
        getVideo()

        ic_back.setOnClickListener {
            finish()
        }
    }

    private fun getDetail() {
        val movie = intent.getParcelableExtra<MovieModel>(EXTRA_DETAIL)
        val genres = ArrayList<String>()
        val movie_id = movie?.id
        val api_key = "e40c34a2a097d56ae9509a5ab8c47d44"
        val url = "https://api.themoviedb.org/3/movie/${movie_id}?api_key=${api_key}&language=en-US"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                progressBar.visibility = View.GONE
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val jsonObject = JSONObject(result)
                    val movieDetail = MovieModel()
                    movieDetail.title = jsonObject.getString("title")
                    movieDetail.poster = jsonObject.getString("poster_path")
                    movieDetail.backdrop = jsonObject.getString("backdrop_path")
                    movieDetail.date = jsonObject.getString("release_date")
                    movieDetail.rating = jsonObject.getDouble("vote_average")
                    movieDetail.overview = jsonObject.getString("overview")
                    val jsonArray = jsonObject.getJSONArray("genres")
                    for (i in 0 until jsonArray.length()) {
                        val genreObject = jsonArray.getJSONObject(i)
                        val genre = genreObject.getString("name")
                        genres.add(genre)
                    }

                    tv_title.text = movieDetail.title
                    tv_date.text = movieDetail.date

                    val builder = StringBuilder()
                    for (i in genres) {
                        builder.append(i)
                        builder.append(", ")
                    }

                    tv_genre.text = builder

                    tv_rating.text = movieDetail.rating.toString()
                    tv_overview.text = movieDetail.overview
                    Picasso.get().load(url_poster + movieDetail.poster).into(img_poster)
                    Picasso.get().load(url_backdrop + movieDetail.backdrop).into(img_backdrop)

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
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@DetailMovieActivity,
                    "your connection failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun getCast() {
        val movie = intent.getParcelableExtra<MovieModel>(EXTRA_DETAIL)
        val movie_id = movie?.id
        val url =
            "https://api.themoviedb.org/3/movie/${movie_id}/credits?api_key=e40c34a2a097d56ae9509a5ab8c47d44"
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
                    for (i in cast) {
                        builder.append(i)
                        builder.append(", ")
                    }

                    tv_cast.text = builder

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
        val movie_id = movie?.id
        val url =
            "https://api.themoviedb.org/3/movie/$movie_id/recommendations?api_key=e40c34a2a097d56ae9509a5ab8c47d44&language=en-US&page=1"
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

                    val recommendationAdapter = MovieAdapter(recommendations)

                    rv_recommendation.layoutManager = LinearLayoutManager(
                        this@DetailMovieActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    rv_recommendation.adapter = recommendationAdapter
                    rv_recommendation.setHasFixedSize(true)
                    recommendationAdapter.notifyDataSetChanged()

                    recommendationAdapter.setOnItemClickCallback(object :
                        MovieAdapter.OnItemClickCallBack {
                        override fun onItemClicked(movie: MovieModel) {
                            val toDetail = Intent(
                                this@DetailMovieActivity,
                                DetailRecommendationActivity::class.java
                            )
                            toDetail.putExtra(DetailRecommendationActivity.EXTRA_DETAIL, movie)
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

    private fun getVideo() {
        val movies = intent.getParcelableExtra<MovieModel>(EXTRA_DETAIL)
        val movie_id = movies?.id
        val prefix = "https://www.youtube.com/watch?v="
        val url =
            "https://api.themoviedb.org/3/movie/$movie_id/videos?api_key=e40c34a2a097d56ae9509a5ab8c47d44&language=en-US"
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

                    val jsonObject = jsonArray.getJSONObject(0)
                    val key = jsonObject.getString("key")

                    tv_trailer.setOnClickListener {
                        val toTrailer = Intent(Intent.ACTION_VIEW, Uri.parse(prefix + key))
                        startActivity(toTrailer)
                    }

                } catch (e: Exception) {}
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                TODO("Not yet implemented")
            }

        })
    }
}