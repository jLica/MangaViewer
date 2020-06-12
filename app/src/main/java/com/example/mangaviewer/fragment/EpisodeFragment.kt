package com.example.mangaviewer.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangaviewer.R
import com.example.mangaviewer.activity.MainActivity
import com.example.mangaviewer.adapter.EpisodeAdapter
import com.example.mangaviewer.data.Episode
import com.example.mangaviewer.retrofit.RetrofitConnection
import com.example.mangaviewer.data.Episodes
import com.example.mangaviewer.data.ResultBookmark
import com.example.mangaviewer.data.Updates
import com.example.mangaviewer.json.JsonforBookmark
import com.example.mangaviewer.json.JsonforEpisodes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodeFragment(val title : String, val titleLink : String, val artist : String, val fromContent : Boolean) : Fragment() {

    private lateinit var dialog : AlertDialog
    private lateinit var episodes : ArrayList<Episode>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_episode, container, false)
        val recycler : RecyclerView = view.findViewById(R.id.episode_recycler)
        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        dialog = alertBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val tmp = reduceText(changeTitle(title))
        val ac = (requireActivity() as MainActivity)
        ac.setBarforEp(tmp)
        ac.getEpisodeBack().setOnClickListener {
            /*if(fromContent) {
                val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
                val transaction : FragmentTransaction = fragmentManager.beginTransaction()
                transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                transaction.remove(fragmentManager.findFragmentByTag("episode")!!).commit()
                ac.setBarforContent(title)
            }
            else { //만화 타이틀에서 온 경우

            }*/
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val transaction : FragmentTransaction = fragmentManager.beginTransaction()
            //transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
            transaction.remove(fragmentManager.findFragmentByTag("episode")!!).commit()
            ac.clearBar()
        }

        //val imageView : ImageView = view.findViewById(R.id.episode_image)
        //val artistText : TextView = view.findViewById(R.id.episode_artist)
        //val periodText : TextView = view.findViewById(R.id.episode_period)
        val retrofitConnection =
            RetrofitConnection(requireContext())
        val json = JsonforEpisodes(title, titleLink, artist)
        val call : Call<Episodes> = retrofitConnection.server.getEpData(json)
        call.enqueue(object : Callback<Episodes> {
            override fun onFailure(call: Call<Episodes>, t: Throwable) {
                Log.i("어우", t.toString())
            }

            override fun onResponse(
                call: Call<Episodes>,
                response: Response<Episodes>
            ) {
                val epItem = response.body()!!
                episodes = epItem.epList
                val adapter = EpisodeAdapter(epItem.epList, this@EpisodeFragment, epItem.info, title)
                val ac = (activity!! as MainActivity)
                var bookmark = epItem.info.bookmark
                val bookmarkBtn = ac.getBookmarkBtn()
                if(bookmark) {
                    bookmarkBtn.setImageResource(R.drawable.star_yellow)
                }
                bookmarkBtn.setOnClickListener {
                    when(bookmark) {
                        true -> {
                            dialog.show()
                            bookmark = false
                            val tmp = JsonforBookmark(title, artist, bookmark)
                            val call2 : Call<ResultBookmark> = retrofitConnection.server.setBookmark(tmp)
                            call2.enqueue(object : Callback<ResultBookmark> {
                                override fun onFailure(call: Call<ResultBookmark>, t: Throwable) {
                                    Log.i("에피소드에서 북마크 false로 하기 실패", t.toString())
                                }
                                override fun onResponse(
                                    call: Call<ResultBookmark>,
                                    response: Response<ResultBookmark>
                                ) {
                                    dialog.dismiss()
                                    if(response.body()!!.isSuccessful) {
                                        bookmarkBtn.setImageResource(R.drawable.star_white)
                                        Toast.makeText(activity!!.applicationContext, "북마크 해제되었습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Toast.makeText(activity!!.applicationContext, "북마크 해제에 실패했습니다", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                        false -> {
                            dialog.show()
                            bookmark = true
                            val tmp = JsonforBookmark(title, artist, bookmark)
                            Log.i("어허...", title)
                            val call2 : Call<ResultBookmark> = retrofitConnection.server.setBookmark(tmp)
                            call2.enqueue(object : Callback<ResultBookmark> {
                                override fun onFailure(call: Call<ResultBookmark>, t: Throwable) {
                                    Log.i("에피소드에서 북마크 true로 하기 실패", t.toString())
                                }
                                override fun onResponse(
                                    call: Call<ResultBookmark>,
                                    response: Response<ResultBookmark>
                                ) {
                                    dialog.dismiss()
                                    if(response.body()!!.isSuccessful) {
                                        bookmarkBtn.setImageResource(R.drawable.star_yellow)
                                        Toast.makeText(activity!!.applicationContext, "북마크 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Toast.makeText(activity!!.applicationContext, "북마크 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                    }
                }
                val layoutManager = LinearLayoutManager(activity!!.applicationContext)
                recycler.layoutManager = layoutManager
                recycler.adapter = adapter
                //recycler.scrollToPosition(epItem.epList.size)
                dialog.dismiss()
            }

        })
        return view
    }

    private fun reduceText(title : String) : String {
        return if(title.length > 21) {
            title.substring(0, 21) + "..."
        } else {
            title
        }
    }

    private fun changeTitle(t : String) : String {
        var tmp = t
        while(tmp.contains("쀍")) {
            tmp = tmp.replace("쀍", "/")
        }
        while(tmp.contains("뿕")) {
            tmp = tmp.replace("뿕", "?")
        }
        while(tmp.contains("뽉")) {
            tmp = tmp.replace("뽉", "*")
        }
        while(tmp.contains("뻙")) {
            tmp = tmp.replace("뻙", "<")
        }
        while(tmp.contains("빩")) {
            tmp = tmp.replace("빩", ">")
        }
        while(tmp.contains("뽥")) {
            tmp = tmp.replace("뽥", ":")
        }
        while(tmp.contains("쀩")) {
            tmp = tmp.replace("쀩", "|")
        }
        while(tmp.contains("꽑")) {
            tmp = tmp.replace("꽑", "\"")
        }
        while(tmp.contains("꿝")) {
            tmp = tmp.replace("꿝", "\\")
        }
        while(tmp.contains("꿹")) {
            tmp = tmp.replace("꿹", "%")
        }
        if(tmp.contains("낅")) {
            tmp = tmp.replace("낅", ".")
        }
        return tmp
    }

    fun setContentFragment(eptitle : String, eplink : String) {
        val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        //transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)

        transaction.add(R.id.frameMain, ContentFragment(title, eptitle, eplink, artist, titleLink, episodes, true), "content").commit()
        //(activity as MainActivity).setBarforContent(title)
    }


}