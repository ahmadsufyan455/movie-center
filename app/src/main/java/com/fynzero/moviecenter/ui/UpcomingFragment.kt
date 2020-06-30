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
import androidx.recyclerview.widget.GridLayoutManager
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.MovieUpcomingAdapter
import com.fynzero.moviecenter.model.MovieModel
import com.fynzero.moviecenter.viewmodel.MovieUpcomingViewModel
import kotlinx.android.synthetic.main.fragment_upcoming.*

class UpcomingFragment : Fragment() {

    private lateinit var movieUpcomingViewModel: MovieUpcomingViewModel
    private val upcomingList = ArrayList<MovieModel>()
    private val upcomingAdapter = MovieUpcomingAdapter(upcomingList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup recyclerview with grid manager
        rv_movie_upcoming.layoutManager =
            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        rv_movie_upcoming.adapter = upcomingAdapter
        rv_movie_upcoming.setHasFixedSize(true)
        upcomingAdapter.notifyDataSetChanged()

        getMovieUpcomingViewModel()

        upcomingAdapter.setOnItemClickCallBack(object : MovieUpcomingAdapter.OnItemClickCallback {
            override fun onItemClicked(upcoming: MovieModel) {
                val toDetail = Intent(context, DetailMovieActivity::class.java)
                toDetail.putExtra(DetailMovieActivity.EXTRA_DETAIL, upcoming)
                startActivity(toDetail)
            }
        })
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getMovieUpcomingViewModel() {
        movieUpcomingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(MovieUpcomingViewModel::class.java)

        movieUpcomingViewModel.setUpcoming()

        movieUpcomingViewModel.getUpcoming().observe(this, Observer { upcomingList ->
            if (upcomingList != null) {
                upcomingAdapter.seData(upcomingList)
                progressBar.visibility = View.GONE
            }
        })
    }
}