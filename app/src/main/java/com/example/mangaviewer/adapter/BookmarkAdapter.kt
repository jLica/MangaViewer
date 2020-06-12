package com.example.mangaviewer.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.example.mangaviewer.R
import com.example.mangaviewer.data.Searched
import com.example.mangaviewer.fragment.BookmarkFragment
import java.lang.RuntimeException

class BookmarkAdapter(val list : ArrayList<Searched>, val dialog : AlertDialog, val frag : BookmarkFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var pa : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        pa = parent

        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.title_bookmark, parent, false)
                EmptyViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_main, parent, false)
                MyViewHolder(view)
            }
            else -> throw RuntimeException("viewtype error")
        }
    }

    override fun getItemCount(): Int {
        return when(list.size % 3) {
            0 -> list.size / 3 + 1
            1 -> (list.size + 2) / 3 + 1
            2 -> (list.size + 1) / 3 + 1
            else -> 1
        }
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, p : Int) {
        if(p > 0) {
            val position : Int = p - 1

            if(list[position].title.isNotEmpty()) {
                val holder = h as MyViewHolder
                when(list.size % 3) {
                    0 -> {
                        for (i in 0..2) {
                            setItem(position * 3 + i, holder)
                        }
                    }
                    1-> {
                        if(position < (list.size - 1) / 3) {
                            for (i in 0..2) {
                                setItem(position * 3 + i, holder)
                            }
                        }
                        else {
                            setItem(position * 3, holder)
                            holder.card2.visibility = View.INVISIBLE
                            holder.card3.visibility = View.INVISIBLE
                        }
                    }
                    2 -> {
                        if(position < (list.size - 2) / 3) {
                            for (i in 0..2) {
                                setItem(position * 3 + i, holder)
                            }
                        }
                        else {
                            for(i in 0..1) {
                                setItem(position * 3 + i, holder)
                            }
                            holder.card3.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imageView1 : ImageView = itemView.findViewById(R.id.cardMain_image1)
        val imageView2 : ImageView = itemView.findViewById(R.id.cardMain_image2)
        val imageView3 : ImageView = itemView.findViewById(R.id.cardMain_image3)
        val title1 : TextView = itemView.findViewById(R.id.cardMain_title1)
        val title2 : TextView = itemView.findViewById(R.id.cardMain_title2)
        val title3 : TextView = itemView.findViewById(R.id.cardMain_title3)
        val card1 : LinearLayout = itemView.findViewById(R.id.card1)
        val card2 : LinearLayout = itemView.findViewById(R.id.card2)
        val card3 : LinearLayout = itemView.findViewById(R.id.card3)

    }

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    inner class ImageTask(val link : String, val context : Context, val image : ImageView) : AsyncTask<Void, Void, Drawable>() {

        override fun doInBackground(vararg params: Void?): Drawable {
            val s : FutureTarget<Drawable> = Glide.with(context).load(link).submit()
            val tmp : Drawable = s.get()
            return tmp
        }

        override fun onPostExecute(result: Drawable?) {
            super.onPostExecute(result)
            image.setImageDrawable(result)
            //dialog.dismiss()
        }
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

    fun setItem(position : Int, holder : MyViewHolder) {
        val item = list[position]

        val url = pa.context.resources.getString(R.string.url)

        val titleImage = item.imageLink.replace("http://localhost:3000", url)



        when(position % 3) {
            0 -> {
                val task = ImageTask(titleImage, pa.context, holder.imageView1)
                task.execute()
                val tmp = changeTitle(item.title)
                holder.title1.text = reduceText(tmp)

                holder.card1.setOnClickListener {
                    frag.setEpFragment(item.title, item.link, item.artist)
                }
            }
            1 -> {
                val task = ImageTask(titleImage, pa.context, holder.imageView2)
                task.execute()
                val tmp = changeTitle(item.title)
                holder.title2.text = reduceText(tmp)

                holder.card2.setOnClickListener {
                    frag.setEpFragment(item.title, item.link, item.artist)
                }
            }
            2 -> {
                val task = ImageTask(titleImage, pa.context, holder.imageView3)
                task.execute()
                val tmp = changeTitle(item.title)
                holder.title3.text = reduceText(tmp)

                holder.card3.setOnClickListener {
                    frag.setEpFragment(item.title, item.link, item.artist)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> 0
            else -> 1
        }
    }
}