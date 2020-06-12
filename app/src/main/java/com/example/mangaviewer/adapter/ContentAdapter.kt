package com.example.mangaviewer.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.mangaviewer.R
import com.example.mangaviewer.data.Episode
import com.example.mangaviewer.fragment.ContentFragment
import java.lang.RuntimeException

class ContentAdapter(val list : ArrayList<String>, val dialog : AlertDialog, val episodes : ArrayList<Episode>, val previousIndex : Int, val nextIndex : Int, val frag : ContentFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var pa : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        pa = parent
        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_content, parent, false)
                BottomViewHoolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_content, parent, false)
                MyViewHolder(view)
            }
            else -> throw RuntimeException("viewtype error")
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position < list.size) {
            val h = holder as MyViewHolder
            val item = list[position]
            val url = pa.context.resources.getString(R.string.url)
            val imageLink = item.replace("http://localhost:3000", url)
            val task = ImageTask(imageLink, pa.context, h.image, position)
            task.execute()
        }
        else {

            val h = holder as BottomViewHoolder
            if(nextIndex == episodes.size) {
                h.nextEp.visibility = View.INVISIBLE
            }
            else {
                h.nextEp.setOnClickListener {
                    val ele = episodes[nextIndex]
                    frag.setContentFragment(ele.eptitle, ele.link)
                }
            }
            if(previousIndex == -1) {
                h.previousEp.visibility = View.INVISIBLE
            }
            else {
                h.previousEp.setOnClickListener {
                    val ele = episodes[previousIndex]
                    frag.setContentFragment(ele.eptitle, ele.link)
                }
            }
        }
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.content_image)
    }

    inner class BottomViewHoolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val previousEp : LinearLayout = itemView.findViewById(R.id.previousEp)
        val nextEp : LinearLayout = itemView.findViewById(R.id.nextEp)
    }

    inner class ImageTask(val link : String, val context : Context, val image : ImageView, val index : Int) : AsyncTask<Void, Void, Drawable>() {

        override fun doInBackground(vararg params: Void?): Drawable {
            val s : FutureTarget<Drawable> = Glide.with(context).load(link).submit()
            val tmp : Drawable = s.get()
            return tmp
        }

        override fun onPostExecute(result: Drawable?) {
            image.setImageDrawable(result)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            list.size -> 0
            else -> 1
        }
    }


}