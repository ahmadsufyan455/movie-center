package com.fynzero.moviecenter.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.MovieUpcomingAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_search_list.*
import org.json.JSONObject
import java.lang.Exception

class SearchListActivity : AppCompatActivity() {

    companion object {
        private val TAG = SearchListActivity::class.java.simpleName
        const val apiKey = BuildConfig.API_KEY
        const val EXTRA_SEARCH = "extra_search"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_list)

        getSearch()
    }

    private fun getSearch() {
        val bundle = intent.extras!!
        val query = bundle.getString(EXTRA_SEARCH)
        val movieList = ArrayList<MovieModel>()
        val url =
            "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&language=en-US&query=$query&page=1&include_adult=false"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                search_progress.visibility = View.GONE
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val movieModel = MovieModel()
                        movieModel.id = jsonObject.getInt("id")
                        movieModel.title = jsonObject.getString("title")
                        movieModel.poster = jsonObject.getString("poster_path")
                        movieList.add(movieModel)
                    }

                    if (jsonArray.length() == 0) {
                        tv_noResult.visibility = View.VISIBLE
                    }

                    rv_search.layoutManager = GridLayoutManager(
                        this@SearchListActivity,
                        3,
                        GridLayoutManager.VERTICAL,
                        false
                    )
                    val movieAdapter = MovieUpcomingAdapter(movieList)
                    rv_search.adapter = movieAdapter
                    rv_search.setHasFixedSize(true)
                    movieAdapter.notifyDataSetChanged()

                    movieAdapter.setOnItemClickCallBack(object :
                        MovieUpcomingAdapter.OnItemClickCallback {
                        override fun onItemClicked(upcoming: MovieModel) {
                            val toDetail =
                                Intent(this@SearchListActivity, DetailMovieActivity::class.java)
                            toDetail.putExtra(DetailMovieActivity.EXTRA_DETAIL, upcoming)
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
                search_progress.visibility = View.GONE
                Log.d("onFailure", error?.message.toString())
                Toast.makeText(
                    this@SearchListActivity,
                    "your connection failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}