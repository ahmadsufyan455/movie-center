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
import com.fynzero.moviecenter.adapter.MovieAdapter
import com.fynzero.moviecenter.adapter.TvAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.fynzero.moviecenter.model.TvModel
import com.fynzero.moviecenter.viewmodel.MoviePopularViewModel
import com.fynzero.moviecenter.viewmodel.TvPopularViewModel
import kotlinx.android.synthetic.main.fragment_popular.*
import kotlinx.android.synthetic.main.fragment_popular.progressBar

class PopularFragment : Fragment() {

    private lateinit var movieViewModel: MoviePopularViewModel
    private val movieList = ArrayList<MovieModel>()
    private val movieAdapter = MovieAdapter(movieList)

    private lateinit var tvViewModel: TvPopularViewModel
    private val tvList = ArrayList<TvModel>()
    private val tvAdapter = TvAdapter(tvList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup recyclerview movie
        rv_movie.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_movie.adapter = movieAdapter
        rv_movie.setHasFixedSize(true)
        movieAdapter.notifyDataSetChanged()

        // setup recyclerview Tv
        rv_tv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_tv.adapter = tvAdapter
        rv_tv.setHasFixedSize(true)
        tvAdapter.notifyDataSetChanged()

        getMovieViewModel()
        getTvViewModel()

        movieAdapter.setOnItemClickCallback(object : MovieAdapter.OnItemClickCallBack {
            override fun onItemClicked(movie: MovieModel) {
                val toDetail = Intent(context, DetailMovieActivity::class.java)
                toDetail.putExtra(DetailMovieActivity.EXTRA_DETAIL, movie)
                startActivity(toDetail)
            }
        })

        tvAdapter.setOnItemClickCallback(object : TvAdapter.OnItemClickCallback {
            override fun onItemClicked(tvShow: TvModel) {
                val toDetail = Intent(context, DetailTvActivity::class.java)
                toDetail.putExtra(DetailTvActivity.EXTRA_DETAIL, tvShow)
                startActivity(toDetail)
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getMovieViewModel() {
        movieViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(MoviePopularViewModel::class.java)

        movieViewModel.setMovie()

        movieViewModel.getMovie().observe(this, Observer { movieList ->
            if (movieList != null) {
                movieAdapter.setData(movieList)
                progressBar.visibility = View.GONE
                view_popular.visibility = View.GONE
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getTvViewModel() {
        tvViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(TvPopularViewModel::class.java)

        tvViewModel.setTv()

        tvViewModel.getTv().observe(this, Observer { tvList ->
            if (tvList != null) {
                tvAdapter.setData(tvList)
            }
        })
    }
}