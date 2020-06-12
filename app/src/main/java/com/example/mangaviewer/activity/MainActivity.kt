package com.example.mangaviewer.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.example.mangaviewer.R
import com.example.mangaviewer.fragment.BookmarkFragment
import com.example.mangaviewer.fragment.MainFragment
import com.example.mangaviewer.fragment.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.screen_main.*
import kotlinx.android.synthetic.main.screen_main.bookmark_star
import kotlinx.android.synthetic.main.screen_main.content_back
import kotlinx.android.synthetic.main.screen_main.content_listBtn
import kotlinx.android.synthetic.main.screen_main.episode_back
import kotlinx.android.synthetic.main.screen_main.main_title

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    private val mainFragment = MainFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager.beginTransaction().add(R.id.frameMain, mainFragment, "main").commit()

        bottomnav.selectedItemId = R.id.home
        bottomnav.setOnNavigationItemSelectedListener(ItemSelectedListener())
        setSupportActionBar(toolbar)

    }

    override fun onBackPressed() {
        when {
            episode_back.visibility == View.VISIBLE -> {
                episode_back.callOnClick()
            }
            content_back.visibility == View.VISIBLE -> {
                content_back.callOnClick()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    inner class ItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            val transaction : FragmentTransaction = fragmentManager.beginTransaction()
            val fragMain = fragmentManager.findFragmentByTag("main")
            val fragBM = fragmentManager.findFragmentByTag("bookmark")
            val fragSearch = fragmentManager.findFragmentByTag("search")
            val fragEp = fragmentManager.findFragmentByTag("episode")
            val fragContent = fragmentManager.findFragmentByTag("content")
            when(item.itemId) {
                R.id.bookmark -> {
                    //transaction.replace(R.id.frameMain, bookmarkFragment).commit()

                    if(fragEp != null)
                        transaction.remove(fragEp)
                    if(fragContent != null)
                        transaction.remove(fragContent)

                    if(fragBM == null) {
                        if(fragMain != null)
                            transaction.hide(fragMain)
                        if(fragSearch != null)
                            transaction.hide(fragSearch)
                        transaction.add(R.id.frameMain, BookmarkFragment(), "bookmark").commit()
                    }
                    else {
                        if(fragMain != null)
                            transaction.hide(fragMain)
                        if(fragSearch != null)
                            transaction.hide(fragSearch)
                        transaction.remove(fragBM)
                        transaction.add(R.id.frameMain, BookmarkFragment(), "bookmark").commit()
                    }
                }
                R.id.search -> {
                    //transaction.replace(R.id.frameMain, searchFragment).commit()
                    if(fragEp != null)
                        transaction.remove(fragEp)
                    if(fragContent != null)
                        transaction.remove(fragContent)

                    if(fragSearch == null) {
                        if(fragMain != null)
                            transaction.hide(fragMain)
                        if(fragBM != null)
                            transaction.hide(fragBM)
                        transaction.add(R.id.frameMain, SearchFragment(), "search").commit()

                    }
                    else {
                        if(fragMain != null)
                            transaction.hide(fragMain)
                        if(fragBM != null)
                            transaction.hide(fragBM)
                        transaction.show(fragSearch).commit()
                    }
                }
                R.id.home -> {
                    //transaction.replace(R.id.frameMain, mainFragment).commit()
                    if(fragMain == null) {
                        transaction.add(R.id.frameMain, mainFragment, "main").commit()
                    }
                    else {
                        if(fragBM != null)
                            transaction.hide(fragBM)
                        if(fragSearch != null)
                            transaction.hide(fragSearch)
                        val transaction_ = fragmentManager.beginTransaction()
                        //transaction_.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                        if(fragEp != null)
                            transaction_.remove(fragEp)
                        if(fragContent != null)
                            transaction_.remove(fragContent)
                        transaction_.commit()
                        transaction.show(fragMain).commit()
                        clearBar()
                    }
                }
                else -> {}
            }
            return true
        }

    }
    fun setBarforEp(title : String) {
        //navMenu.visibility = View.INVISIBLE
        episode_back.visibility = View.VISIBLE
        content_back.visibility = View.INVISIBLE
        main_title.text = title
        content_listBtn.visibility = View.INVISIBLE
        bookmark_star.visibility = View.VISIBLE
    }

    fun setBarforContent(title : String) {
        //navMenu.visibility = View.INVISIBLE
        episode_back.visibility = View.INVISIBLE
        content_back.visibility = View.VISIBLE
        main_title.text = title
        content_listBtn.visibility = View.VISIBLE
        bookmark_star.visibility = View.INVISIBLE
    }

    fun getListBtn() : ImageView {
        return content_listBtn
    }

    fun clearBar() {
        //navMenu.visibility = View.VISIBLE
        episode_back.visibility = View.GONE
        content_back.visibility = View.INVISIBLE
        bookmark_star.visibility = View.INVISIBLE
        main_title.text = "만화를 보자"
        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("main")!!)
    }

    fun getContentBack() : ImageView {
        return content_back
    }

    fun getEpisodeBack() : ImageView {
        return episode_back
    }

    fun getBookmarkBtn() : ImageView {
        return bookmark_star
    }


}
