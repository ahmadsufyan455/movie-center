package com.fynzero.moviecenter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.model.MovieModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception

class MovieUpcomingViewModel : ViewModel() {
    companion object {
        private val TAG = MovieUpcomingViewModel::class.java.simpleName
        const val apiKey = BuildConfig.API_KEY
    }

    private val movieUpcomingList = MutableLiveData<ArrayList<MovieModel>>()

    fun setUpcoming() {
        val upcomingList = ArrayList<MovieModel>()
        val url =
            "https://api.themoviedb.org/3/movie/upcoming?api_key=${apiKey}&language=en-US&page=1"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // parsing json
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val movieUpcoming = MovieModel()
                        movieUpcoming.id = jsonObject.getInt("id")
                        movieUpcoming.title = jsonObject.getString("title")
                        movieUpcoming.poster = jsonObject.getString("poster_path")
                        movieUpcoming.backdrop = jsonObject.getString("backdrop_path")
                        movieUpcoming.rating = jsonObject.getDouble("vote_average")
                        movieUpcoming.date = jsonObject.getString("release_date")
                        movieUpcoming.overview = jsonObject.getString("overview")
                        upcomingList.add(movieUpcoming)
                    }

                    movieUpcomingList.postValue(upcomingList)

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

    fun getUpcoming(): LiveData<ArrayList<MovieModel>> {
        return movieUpcomingList
    }
}