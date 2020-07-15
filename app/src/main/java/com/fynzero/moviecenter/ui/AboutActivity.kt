package com.fynzero.moviecenter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fynzero.moviecenter.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        supportActionBar?.title = "About App"
    }
}