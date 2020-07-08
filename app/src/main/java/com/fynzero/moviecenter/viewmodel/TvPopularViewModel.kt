package com.fynzero.moviecenter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.model.TvModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception

class TvPopularViewModel : ViewModel() {
    companion object {
        private val TAG = TvPopularViewModel::class.java.simpleName
        const val apiKey = BuildConfig.API_KEY
    }

    private val tvList = MutableLiveData<ArrayList<TvModel>>()

    fun setTv() {
        val tvShows = ArrayList<TvModel>()
        val url = "https://api.themoviedb.org/3/tv/popular?api_key=${apiKey}&language=en-US&page=1"
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
                        val tvModel = TvModel()
                        tvModel.id = jsonObject.getInt("id")
                        tvModel.name = jsonObject.getString("name")
                        tvModel.poster = jsonObject.getString("poster_path")
                        tvModel.backdrop = jsonObject.getString("backdrop_path")
                        tvModel.date = jsonObject.getString("first_air_date")
                        tvModel.rating = jsonObject.getDouble("vote_average")
                        tvModel.overview = jsonObject.getString("overview")
                        tvShows.add(tvModel)
                    }

                    tvList.postValue(tvShows)

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

    fun getTv(): LiveData<ArrayList<TvModel>> {
        return tvList
    }
}