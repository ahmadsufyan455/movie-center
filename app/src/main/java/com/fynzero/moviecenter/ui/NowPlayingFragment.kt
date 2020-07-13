package com.fynzero.moviecenter.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.NowPlayingAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.fynzero.moviecenter.viewmodel.NowPlayingViewModel
import kotlinx.android.synthetic.main.fragment_now_playing.*

class NowPlayingFragment : Fragment() {

    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private val nowPlyingList = ArrayList<MovieModel>()
    private val nowPlayingAdapter = NowPlayingAdapter(nowPlyingList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup recyclerview
        rv_now_playing.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_now_playing.adapter = nowPlayingAdapter
        rv_now_playing.setHasFixedSize(true)
        nowPlayingAdapter.notifyDataSetChanged()

        getNowPlayingViewModel()

        nowPlayingAdapter.setOnItemClickCallback(object : NowPlayingAdapter.OnItemClickCallback {
            override fun onItemClicked(movieModel: MovieModel) {
                val toDetail = Intent(context, DetailMovieActivity::class.java)
                toDetail.putExtra(DetailMovieActivity.EXTRA_DETAIL, movieModel)
                startActivity(toDetail)
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getNowPlayingViewModel() {
        nowPlayingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(NowPlayingViewModel::class.java)

        nowPlayingViewModel.setMovie()

        nowPlayingViewModel.getMovie().observe(this, Observer { nowPlayingList ->
            if (nowPlayingList != null) {
                nowPlayingAdapter.setData(nowPlayingList)
                progressBar_now_playing.visibility = View.GONE
            }
        })
    }
}