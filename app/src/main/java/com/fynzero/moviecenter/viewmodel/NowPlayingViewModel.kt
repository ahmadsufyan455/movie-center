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

class NowPlayingViewModel : ViewModel() {

    companion object {
        private val TAG = NowPlayingViewModel::class.java.simpleName
        private const val apiKey = BuildConfig.API_KEY
    }

    private val nowPlayingList = MutableLiveData<ArrayList<MovieModel>>()

    fun setMovie() {
        val movies = ArrayList<MovieModel>()
        val url =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=$apiKey&language=en-US&page=1"
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
                        val movieModel = MovieModel()
                        movieModel.id = jsonObject.getInt("id")
                        movieModel.backdrop = jsonObject.getString("backdrop_path")
                        movieModel.title = jsonObject.getString("title")
                        movies.add(movieModel)
                    }

                    nowPlayingList.postValue(movies)

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

    fun getMovie(): LiveData<ArrayList<MovieModel>> {
        return nowPlayingList
    }

}