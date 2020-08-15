package com.fynzero.moviecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.MovieModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_trailer.*
import kotlinx.android.synthetic.main.custom_actionbar_title.*
import org.json.JSONObject
import java.lang.Exception

class TrailerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY = "key"
        const val apiKey = BuildConfig.API_KEY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer)

        val titles = intent.getParcelableExtra<MovieModel>(EXTRA_KEY)
        val title = titles?.title
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_actionbar_title)
        action_bar_title.text = title

        getVideo()
    }

    private fun getVideo() {
        val listTitle = ArrayList<String>()
        val listKey = ArrayList<String>()
        val movies = intent.getParcelableExtra<MovieModel>(EXTRA_KEY)
        val movieId = movies?.id
        val url =
            "https://api.themoviedb.org/3/movie/$movieId/videos?api_key=$apiKey&language=en-US"
        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val result = String(responseBody!!)

                try {

                    val responseObject = JSONObject(result)
                    val jsonArray = responseObject.getJSONArray("results")

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val keys = obj.getString("key")
                        val names = obj.getString("name")
                        listTitle.add(names)
                        listKey.add(keys)
                    }

                    // default trailer [0]
                    if (jsonArray.length() != 0) {
                        youtube_player_view.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.loadVideo(listKey[0], 0F)
                            }
                        })
                    }

                    // custom trailer
                    val adapter = ArrayAdapter(
                        this@TrailerActivity,
                        R.layout.simple_list,
                        R.id.txt_trailer,
                        listTitle
                    )
                    lv_list.adapter = adapter

                    youtube_player_view.addYouTubePlayerListener(object :
                        AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            lv_list.setOnItemClickListener { _, _, position, _ ->
                                Toast.makeText(
                                    this@TrailerActivity,
                                    listTitle[position],
                                    Toast.LENGTH_SHORT
                                ).show()
                                youTubePlayer.loadVideo(listKey[position], 0F)
                            }
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