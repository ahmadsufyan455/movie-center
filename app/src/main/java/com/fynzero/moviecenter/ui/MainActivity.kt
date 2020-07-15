/*
* Created by: Ahmad Sufyan
*/

package com.fynzero.moviecenter.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.fynzero.moviecenter.R
import com.fynzero.moviecenter.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KEY = "extra_key"
    }

    private var mSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup view pager
        val viewPagerAdapter = ViewPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(viewPager)

        supportActionBar?.elevation = 0f

        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(EXTRA_KEY).toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView =
            menu.findItem(R.id.search).actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            // use this method when success search
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                val extra = Bundle()
                val toList = Intent(this@MainActivity, SearchListActivity::class.java)
                extra.putString(SearchListActivity.EXTRA_SEARCH, query)
                toList.putExtras(extra)
                startActivity(toList)
                return true
            }

            // use this method to response change text
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    mSearchQuery = newText
                }
                return false
            }

        })

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EXTRA_KEY, mSearchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about) {
            val toAbout = Intent(this, AboutActivity::class.java)
            startActivity(toAbout)
        }

        return true
    }
}