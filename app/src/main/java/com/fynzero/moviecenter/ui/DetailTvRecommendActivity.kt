package com.fynzero.moviecenter.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.TvRecommendationAdapter
import com.fynzero.moviecenter.model.TvModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.*
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.ic_back
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.img_backdrop
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.img_poster
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.progressBar
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.textView4
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_cast_tvShow
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_date
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_genre
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_name
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_overview
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_rating
import kotlinx.android.synthetic.main.activity_detail_tv_recommend.tv_trailer
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder

class DetailTvRecommendActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        const val url_poster = "https://image.tmdb.org/t/p/w185"
        const val url_backdrop = "https://image.tmdb.org/t/p/w400"
        const val apiKey = BuildConfig.API_KEY
        private val TAG = DetailTvRecommendActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tv_recommend)

        supportActionBar?.hide()

        getTv()
        getCast()
        getRecommendation()

        ic_back.setOnClickListener {
            finish()
        }
    }

    private fun getTv() {
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_DETAIL)
        val genres = ArrayList<String>()
        val tvId = tvShow?.id
        val url = "https://api.themoviedb.org/3/tv/${tvId}?api_key=${apiKey}&language=en-US"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                progressBar.visibility = View.GONE
                view_recommendation_tv.visibility = View.GONE
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val jsonObject = JSONObject(result)
                    val tvDetail = TvModel()
                    tvDetail.id = jsonObject.getInt("id")
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
                    var prefix = ""
                    for (i in genres) {
                        builder.append(prefix)
                        prefix = ", "
                        builder.append(i)
                    }

                    tv_genre.text = builder

                    tv_rating.text = tvDetail.rating.toString()

                    tv_trailer.setOnClickListener {
                        val trailer = Intent(this@DetailTvRecommendActivity, TrailerTvActivity::class.java)
                        trailer.putExtra(TrailerTvActivity.EXTRA_KEY, tvDetail)
                        startActivity(trailer)
                    }

                    tv_overview.text = tvDetail.overview
                    Picasso.get().load(url_poster + tvDetail.poster)
                        .into(img_poster)
                    Picasso.get().load(url_backdrop + tvDetail.backdrop)
                        .into(img_backdrop)

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
                con_failed.visibility = View.VISIBLE
                btn_tryAgain.setOnClickListener {
                    finish()
                    startActivity(intent)
                }
            }
        })
    }

    private fun getCast() {
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_DETAIL)
        val tvId = tvShow?.id
        val url =
            "https://api.themoviedb.org/3/tv/$tvId/credits?api_key=$apiKey&language=en-US"
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
                    var prefix = ""
                    for (i in crews) {
                        builder.append(prefix)
                        prefix = ", "
                        builder.append(i)
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
        val tvId = tvShow?.id
        val url =
            "https://api.themoviedb.org/3/tv/$tvId/recommendations?api_key=$apiKey&language=en-US&page=1"
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
                        textView4.visibility = View.INVISIBLE
                    }

                    val tvRecommendationAdapter = TvRecommendationAdapter(tvRecommendations)

                    rv_tvRecommendation_rec.layoutManager = LinearLayoutManager(
                        this@DetailTvRecommendActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    rv_tvRecommendation_rec.adapter = tvRecommendationAdapter
                    rv_tvRecommendation_rec.setHasFixedSize(true)
                    tvRecommendationAdapter.notifyDataSetChanged()

                    tvRecommendationAdapter.setOnItemClickCallback(object :
                        TvRecommendationAdapter.OnItemClickCallback {
                        override fun onItemClicked(tvModel: TvModel) {
                            val toDetail =
                                Intent(this@DetailTvRecommendActivity, DetailTvActivity::class.java)
                            toDetail.putExtra(DetailTvActivity.EXTRA_DETAIL, tvModel)
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