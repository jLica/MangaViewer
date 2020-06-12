package com.example.mangaviewer.fragment

import android.app.AlertDialog
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mangaviewer.adapter.MainAdapter
import com.example.mangaviewer.R
import com.example.mangaviewer.retrofit.RetrofitConnection
import com.example.mangaviewer.activity.MainActivity
import com.example.mangaviewer.data.Updates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var dialog : AlertDialog
    private lateinit var call : Call<ArrayList<Updates>>
    private lateinit var recycler : RecyclerView
    private lateinit var adapter : MainAdapter
    private lateinit var swipe : SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_main, container, false)
        recycler = view.findViewById(R.id.main_recycler)
        swipe = view.findViewById(R.id.main_refresh)
        swipe.setOnRefreshListener(this)

        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        dialog = alertBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val retrofitConnection =
            RetrofitConnection(requireContext())
        call = retrofitConnection.server.getUpdatedData()
        call.enqueue(object : Callback<ArrayList<Updates>> {
            override fun onFailure(call: Call<ArrayList<Updates>>, t: Throwable) {
                Log.i("허어", t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<Updates>>,
                response: Response<ArrayList<Updates>>
            ) {
                if(response.isSuccessful) {
                    val list = response.body()!!

                    adapter = MainAdapter(list, dialog, this@MainFragment)
                    recycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
                    recycler.adapter = adapter
                    recycler.setItemViewCacheSize(list.size)

                }
                else {
                    Log.i("허어", "GG")
                }
            }

        })

        return view
    }

    override fun onRefresh() {
        call.clone().enqueue(object : Callback<ArrayList<Updates>> {
            override fun onFailure(call: Call<ArrayList<Updates>>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<ArrayList<Updates>>, response: Response<ArrayList<Updates>>) {
                if(response.isSuccessful) {
                    adapter.refreshItem(response.body()!!)
                    swipe.isRefreshing = false
                }
                else {
                    Log.i("허어", "GG")
                }
            }

        })
    }

    fun setContentFragment(item : Updates) {
        val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        transaction.add(R.id.frameMain, ContentFragment(item.title, item.episode.eptitle, item.episode.link, item.artist, item.titleLink, null, false), "content").commit()
        (activity as MainActivity).setBarforContent(item.title)
    }

}