package com.example.mangaviewer.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangaviewer.R
import com.example.mangaviewer.retrofit.RetrofitConnection
import com.example.mangaviewer.activity.MainActivity
import com.example.mangaviewer.adapter.ContentAdapter
import com.example.mangaviewer.data.Contents
import com.example.mangaviewer.data.Episode
import com.example.mangaviewer.data.Episodes
import com.example.mangaviewer.json.JsonforContents
import com.example.mangaviewer.data.Updates
import com.example.mangaviewer.json.JsonforEpisodes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentFragment(val title : String, val eptitle : String, val epLink : String, val artist : String, val titleLink : String, val episodes : ArrayList<Episode>?, val fromEpisode : Boolean) : Fragment() {

    private lateinit var dialog : AlertDialog
    private lateinit var ep : ArrayList<Episode>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_content, container, false)
        val recycler : RecyclerView = view.findViewById(R.id.content_recycler)
        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        dialog = alertBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog.setCancelable(false)
        dialog.show()
        val tmp = changeTitle(title) //쀍 -> 특수문자
        val tmp2 = eptitle.replace(title, "")
        val ac = (requireActivity() as MainActivity)
        ac.setBarforContent(reduceText(tmp, tmp2))
        val retrofitConnection =
            RetrofitConnection(requireContext())
        //val tmp = changeTitle(title)
        //val tmp2 = changeTitle(eptitle)
        ac.getContentBack().setOnClickListener {
            val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
            val transaction : FragmentTransaction = fragmentManager.beginTransaction()
            //transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
            transaction.remove(this).commit()
            if(episodes == null) { //업데이트 화면에서 온 경우
                ac.clearBar()
            }
            else {
                if(fromEpisode) //에피소드 화면에서 온 경우
                    ac.setBarforEp(title)
                else //컨텐트 화면에서 온 경우
                    ac.clearBar()
            }
        }
        val json = JsonforContents( //이미 특수문자 검열된 상태임
            title,
            epLink,
            artist,
            eptitle
        )
        val call : Call<Contents> = retrofitConnection.server.getContentsData(json)
        call.enqueue(object : Callback<Contents> {
            override fun onFailure(call: Call<Contents>, t: Throwable) {
                Log.i("contentFragment", t.toString())
            }

            override fun onResponse(call: Call<Contents>, response: Response<Contents>) {
                if(response.isSuccessful) {
                    val content = response.body()!!

                    (activity!! as MainActivity).getListBtn().setOnClickListener {
                        setEpFragment(title, titleLink, artist)
                    }
                    if(episodes == null) {
                        val json = JsonforEpisodes(title, titleLink, artist)
                        Log.i("컨텐츠 타이틀링크!", titleLink)
                        val epCall : Call<Episodes> = retrofitConnection.server.getEpData(json)
                        epCall.enqueue(object : Callback<Episodes> {

                            override fun onFailure(call: Call<Episodes>, t: Throwable) {
                                Log.i("컨텐츠에서 에피소드 얻기 실패", t.toString())
                            }

                            override fun onResponse(
                                call: Call<Episodes>,
                                response: Response<Episodes>
                            ) {
                                ep = response.body()!!.epList
                                ep.forEachIndexed { index, episode ->
                                    if(eptitle == episode.eptitle) {
                                        val adapter = ContentAdapter(content.contents, dialog, ep, index - 1, index + 1, this@ContentFragment)
                                        recycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
                                        recycler.adapter = adapter
                                        dialog.dismiss()
                                        recycler.setItemViewCacheSize(content.contents.size)
                                    }
                                }

                            }
                        })
                    }
                    else {
                        ep = episodes
                        episodes.forEachIndexed { index, episode ->
                            if(eptitle == episode.eptitle) {
                                val adapter = ContentAdapter(content.contents, dialog, episodes, index - 1, index + 1, this@ContentFragment)
                                recycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
                                recycler.adapter = adapter
                                dialog.dismiss()
                                recycler.setItemViewCacheSize(content.contents.size)
                            }
                        }
                    }
                }
            }
        })
        return view
    }

    private fun reduceText(title : String, episode : String) : String {
        val full = title + episode
        return if(full.length > 21) {
            title.substring(0, 21 - episode.length) + "..." + episode
        } else {
            title + episode
        }
    }

    /*private fun changeTitle(t : String) : String {
        var tmp = t
        while(tmp.contains("/")) {
            tmp = tmp.replace("/", "쀍")
        }
        while(tmp.contains("?")) {
            tmp = tmp.replace("?", "뿕")
        }
        while(tmp.contains("*")) {
            tmp = tmp.replace("*", "뽉")
        }
        while(tmp.contains("<")) {
            tmp = tmp.replace("<", "뻙")
        }
        while(tmp.contains(">")) {
            tmp = tmp.replace(">", "빩")
        }
        while(tmp.contains(":")) {
            tmp = tmp.replace(":", "뽥")
        }
        while(tmp.contains("|")) {
            tmp = tmp.replace("|", "쀩")
        }
        while(tmp.contains("\"")) {
            tmp = tmp.replace("\"", "꽑")
        }
        while(tmp.contains("\\")) {
            tmp = tmp.replace("\\", "꿝")
        }
        while(tmp.contains("%")) {
            tmp = tmp.replace("%", "꿹")
        }
        if(tmp[tmp.length - 1] == '.') {
            tmp = tmp.substring(0, tmp.length - 1) + "낅"
        }
        return tmp
    }*/
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

    private fun setEpFragment(title : String, titleLink : String, artist : String) {
        val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        transaction.remove(fragmentManager.findFragmentByTag("content")!!)
        transaction.add(R.id.frameMain, EpisodeFragment(title, titleLink, artist, true), "episode").commit()
    }

    fun setContentFragment(eptitle : String, eplink : String) {
        val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        transaction.remove(this)
        transaction.add(R.id.frameMain, ContentFragment(title, eptitle, eplink, artist, titleLink, ep, false), "content").commit()
        //(activity as MainActivity).setBarforContent(title)
    }
}