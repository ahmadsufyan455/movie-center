package com.fynzero.moviecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fynzero.moviecenter.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_trailer.*

class TrailerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY = "key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trailer)

        supportActionBar?.hide()

        youtube_player_view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val bundle = intent.extras
                val key = bundle?.getString(EXTRA_KEY)
                youTubePlayer.loadVideo(key.toString(), 0F)
            }
        })
    }
}