package com.fynzero.moviecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.fynzero.moviecenter.BuildConfig
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.model.TvModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_tv_trailer.*
import kotlinx.android.synthetic.main.custom_actionbar_title.*
import org.json.JSONObject
import java.lang.Exception

class TrailerTvActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY = "key"
        const val apiKey = BuildConfig.API_KEY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_trailer)

        val titles = intent.getParcelableExtra<TvModel>(EXTRA_KEY)
        val title = titles?.name
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.custom_actionbar_title)
        action_bar_title.text = title
        getVideo()
    }

    private fun getVideo() {
        val listTitle = ArrayList<String>()
        val listKey = ArrayList<String>()
        val tvShow = intent.getParcelableExtra<TvModel>(EXTRA_KEY)
        val tvId = tvShow?.id
        val url =
            "https://api.themoviedb.org/3/tv/$tvId/videos?api_key=$apiKey&language=en-US"
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
                        youtube_player_view_tv.addYouTubePlayerListener(object :
                            AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.loadVideo(listKey[0], 0F)
                            }
                        })
                    }

                    // custom trailer
                    val adapter = ArrayAdapter(
                        this@TrailerTvActivity,
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        listTitle
                    )
                    lv_tv.adapter = adapter

                    youtube_player_view_tv.addYouTubePlayerListener(object :
                        AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            lv_tv.setOnItemClickListener { _, _, position, _ ->
                                Toast.makeText(
                                    this@TrailerTvActivity,
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