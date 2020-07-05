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
import com.fynzero.moviecenter.adapter.TvRecommendationAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.fynzero.moviecenter.model.TvModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_movie.*
import kotlinx.android.synthetic.main.activity_detail_tv.*
import kotlinx.android.synthetic.main.activity_detail_tv.ic_back
import kotlinx.android.synthetic.main.activity_detail_tv.img_backdrop
import kotlinx.android.synthetic.main.activity_detail_tv.img_poster
import kotlinx.android.synthetic.main.activity_detail_tv.progressBar
import kotlinx.android.synthetic.main.activity_detail_tv.tv_date
import kotlinx.android.synthetic.main.activity_detail_tv.tv_genre
import kotlinx.android.synthetic.main.activity_detail_tv.tv_overview
import kotlinx.android.synthetic.main.activity_detail_tv.tv_rating
import kotlinx.android.synthetic.main.activity_detail_tv.tv_trailer
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder

class DetailTvActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        const val url_poster = "https://image.tmdb.org/t/p/w185"
        const val url_backdrop = "https://image.tmdb.org/t/p/w400"
        private val TAG = DetailTvActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tv)

        supportActionBar?.hide()

        getTv()
        getCast()
        getRecommendation()
        getVideo()

        ic_back.setOnClickListener {
            finish()
        }
    }

    private fun getTv() {
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_DETAIL)
        val genres = ArrayList<String>()
        val tv_id = tvShow?.id
        val api_key = "e40c34a2a097d56ae9509a5ab8c47d44"
        val url = "https://api.themoviedb.org/3/tv/${tv_id}?api_key=${api_key}&language=en-US"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                progressBar.visibility = View.GONE
                view_detail_tv.visibility = View.GONE
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val jsonObject = JSONObject(result)
                    val tvDetail = TvModel()
                    tvDetail.name = jsonObject.getString("name")
                    tvDetail.poster = jsonObject.getString("poster_path")
                    tvDetail.backdrop = jsonObject.getString("backdrop_path")
                    tvDetail.date = jsonObject.getString("first_air_date")
                    tvDetail.rating = jsonObject.getDouble("vote_average")
                    tvDetail.overview = jsonObject.getString("overview")
                    val jsonArray = jsonObject.getJSONArray("genres")
                    for (i in 0 until jsonArray.length()) {
                        val genreObject = jsonArray.getJSONObject(i)
                        val genre = genreObject.getString("name")
                        genres.add(genre)
                    }


                    tv_name.text = tvDetail.name
                    tv_date.text = tvDetail.date

                    val builder = StringBuilder()
                    for (i in genres) {
                        builder.append(i)
                        builder.append(", ")
                    }

                    tv_genre.text = builder

                    tv_rating.text = tvDetail.rating.toString()
                    tv_overview.text = tvDetail.overview
                    Picasso.get().load(url_poster + tvDetail.poster).into(img_poster)
                    Picasso.get().load(url_backdrop + tvDetail.backdrop).into(img_backdrop)

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
                view_detail_tv.visibility = View.GONE
                Toast.makeText(this@DetailTvActivity, "your connection failed", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun getCast() {
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_DETAIL)
        val tv_id = tvShow?.id
        val url =
            "https://api.themoviedb.org/3/tv/$tv_id/credits?api_key=e40c34a2a097d56ae9509a5ab8c47d44&language=en-US"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val crews = ArrayList<String>()
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("cast")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("name")
                        crews.add(name)
                    }

                    val builder = StringBuilder()
                    for (i in crews) {
                        builder.append(i)
                        builder.append(", ")
                    }

                    tv_cast_tvShow.text = builder

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
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_DETAIL)
        val tv_id = tvShow?.id
        val api_key = "e40c34a2a097d56ae9509a5ab8c47d44"
        val url =
            "https://api.themoviedb.org/3/tv/$tv_id/recommendations?api_key=$api_key&language=en-US&page=1"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val tvRecommendations = ArrayList<TvModel>()
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val tvRecommendation = TvModel()
                        tvRecommendation.id = jsonObject.getInt("id")
                        tvRecommendation.name = jsonObject.getString("name")
                        tvRecommendation.poster = jsonObject.getString("poster_path")
                        tvRecommendation.date = jsonObject.getString("first_air_date")
                        tvRecommendation.rating = jsonObject.getDouble("vote_average")
                        tvRecommendations.add(tvRecommendation)
                    }

                    if (jsonArray.length() == 0) {
                        tv_label_rec.visibility = View.INVISIBLE
                    }

                    val tvRecommendationAdapter = TvRecommendationAdapter(tvRecommendations)

                    rv_tvRecommendation.layoutManager = LinearLayoutManager(
                        this@DetailTvActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    rv_tvRecommendation.adapter = tvRecommendationAdapter
                    rv_tvRecommendation.setHasFixedSize(true)
                    tvRecommendationAdapter.notifyDataSetChanged()

                    tvRecommendationAdapter.setOnItemClickCallback(object :
                        TvRecommendationAdapter.OnItemClickCallback {
                        override fun onItemClicked(tvModel: TvModel) {
                            val toDetail =
                                Intent(this@DetailTvActivity, DetailTvRecommendActivity::class.java)
                            toDetail.putExtra(DetailTvRecommendActivity.EXTRA_DETAIL, tvModel)
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
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_DETAIL)
        val tv_id = tvShow?.id
        val prefix = "https://www.youtube.com/watch?v="
        val url =
            "https://api.themoviedb.org/3/tv/$tv_id/videos?api_key=e40c34a2a097d56ae9509a5ab8c47d44&language=en-US"
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

                } catch (e: Exception) {
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