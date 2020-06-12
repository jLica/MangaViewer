package com.example.mangaviewer.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangaviewer.R
import com.example.mangaviewer.activity.MainActivity
import com.example.mangaviewer.adapter.SearchAdapter
import com.example.mangaviewer.data.Searched
import com.example.mangaviewer.json.JsonforSearch
import com.example.mangaviewer.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var dialog : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.frame_search, container, false)
        val editText : EditText = view.findViewById(R.id.search_editText)
        val recycler : RecyclerView = view.findViewById(R.id.search_recycler)
        val alertBuilder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        alertBuilder.setView(inflater.inflate(R.layout.dialog_loading, null))
        dialog = alertBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        editText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(v.text != null) {
                    val imm : InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    dialog.show()
                    val retrofitConnection =
                        RetrofitConnection(requireContext())
                    val json = JsonforSearch(v.text.toString())
                    val call: Call<ArrayList<Searched>> = retrofitConnection.server.getSearchedData(json)
                        call.enqueue(object : Callback<ArrayList<Searched>> {
                            override fun onFailure(call: Call<ArrayList<Searched>>, t: Throwable) {
                                Log.i("검색 실패", t.toString())
                            }

                            override fun onResponse(
                                call: Call<ArrayList<Searched>>,
                                response: Response<ArrayList<Searched>>
                            ) {
                                val item = response.body()
                                if(item != null) {
                                    val adapter = SearchAdapter(item, dialog, this@SearchFragment)
                                    recycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
                                    recycler.adapter = adapter
                                }
                                else {
                                    Toast.makeText(activity!!, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                }
                            }
                        })
                }
            }
            return@setOnEditorActionListener true
        }
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