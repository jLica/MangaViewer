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
import com.example.mangaviewer.activity.MainActivity
import com.example.mangaviewer.adapter.BookmarkAdapter
import com.example.mangaviewer.data.Searched
import com.example.mangaviewer.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookmarkFragment : Fragment() {

    private lateinit var dialog : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_bookmark, container, false)
        val recycler : RecyclerView = view.findViewById(R.id.bookmark_recycler)
        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        dialog = alertBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()
        val retrofitConnection = RetrofitConnection(requireContext())

        val call : Call<ArrayList<Searched>> = retrofitConnection.server.getBookmarks()
        call.enqueue(object : Callback<ArrayList<Searched>> {
            override fun onFailure(call: Call<ArrayList<Searched>>, t: Throwable) {
                Log.i("북마크 받아오기 실패", t.toString())
            }

            override fun onResponse(
                call: Call<ArrayList<Searched>>,
                response: Response<ArrayList<Searched>>
            ) {
                val list = response.body()!!
                val adapter = BookmarkAdapter(list, dialog, this@BookmarkFragment)
                recycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
                recycler.adapter = adapter
                dialog.dismiss()
            }
        })
        return view
    }

    fun setEpFragment(title : String, titleLink : String, artist : String) {
        val fragmentManager : FragmentManager = requireActivity().supportFragmentManager
        val transaction : FragmentTransaction = fragmentManager.beginTransaction()
        //transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom) //어째선지 애니메이션 적용 안 됨
        transaction.add(R.id.frameMain, EpisodeFragment(title, titleLink, artist, false), "episode").commit()
        (activity as MainActivity).setBarforEp(title)
    }
}