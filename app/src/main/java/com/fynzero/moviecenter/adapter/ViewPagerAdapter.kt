package com.fynzero.moviecenter.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fynzero.moviecenter.ui.NowPlayingFragment
import com.fynzero.moviecenter.ui.PopularFragment
import com.fynzero.moviecenter.ui.UpcomingFragment

class ViewPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {

        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = NowPlayingFragment()
            1 -> fragment = PopularFragment()
            2 -> fragment = UpcomingFragment()
        }
        return fragment as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "NOW PLAYING"
            1 -> "POPULAR"
            else -> "UPCOMING"
        }
    }

    override fun getCount(): Int {
        return 3
    }

}