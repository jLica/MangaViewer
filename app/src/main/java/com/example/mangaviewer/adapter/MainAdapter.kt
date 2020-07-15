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
import com.example.mangaviewer.data.Updates
import com.example.mangaviewer.fragment.MainFragment

class MainAdapter(val list : ArrayList<Updates>, val dialog : AlertDialog, val frag : MainFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var pa : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        pa = parent
        val view : View
        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.title_main, parent, false)
                EmptyViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_main, parent, false)
                MyViewHolder(view)
            }
            else -> throw RuntimeException("viewType error")
        }
    }

    override fun getItemCount(): Int {
        return (list.size / 3) + 1
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, p: Int) {
        if(p > 0) {
            val position = p - 1;
            val holder = h as MyViewHolder

            val item1 = list[position * 3]
            val item2 = list[position * 3 + 1]
            val item3 = list[position * 3 + 2]

            val url = pa.context.resources.getString(R.string.url)

            val titleImage1 = item1.episode.updateimageLink.replace("http://localhost:3000", url)
            val titleImage2 = item2.episode.updateimageLink.replace("http://localhost:3000", url)
            val titleImage3 = item3.episode.updateimageLink.replace("http://localhost:3000", url)

            val tmp1 = item1.episode.eptitle.replace(item1.title, "")
            val tmp2 = item2.episode.eptitle.replace(item2.title, "")
            val tmp3 = item3.episode.eptitle.replace(item3.title, "")

            holder.card1.setOnClickListener {
                frag.setContentFragment(item1)
            }
            holder.card2.setOnClickListener {
                frag.setContentFragment(item2)
            }
            holder.card3.setOnClickListener {
                frag.setContentFragment(item3)
            }

            val arrayList = ArrayList<String>()
            arrayList.add(titleImage1)
            arrayList.add(titleImage2)
            arrayList.add(titleImage3)

            val arrayImage = ArrayList<ImageView>()
            arrayImage.add(holder.imageView1)
            arrayImage.add(holder.imageView2)
            arrayImage.add(holder.imageView3)

            val task = ImageTask(arrayList, pa.context, 0, arrayImage, position)
            task.execute()

            val tmp_1 = changeTitle(item1.title)
            val tmp_2 = changeTitle(item2.title)
            val tmp_3 = changeTitle(item3.title)

            holder.title1.text = reduceText(tmp_1, tmp1)
            holder.title2.text = reduceText(tmp_2, tmp2)
            holder.title3.text = reduceText(tmp_3, tmp3)
        }
    }

    inner class EmptyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

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

    inner class ImageTask(val arrayLink : ArrayList<String>, val context : Context, val code : Int, val arrayImage : ArrayList<ImageView>, val index : Int) : AsyncTask<Void, Void, Drawable>() {

        override fun doInBackground(vararg params: Void?): Drawable {
            val s : FutureTarget<Drawable> = Glide.with(context).load(arrayLink[code]).override(250).submit()
            val tmp : Drawable = s.get()
            return tmp
        }

        override fun onPostExecute(result: Drawable?) {
            super.onPostExecute(result)
            arrayImage[code].setImageDrawable(result)
            if(code < 2) {
                val c = code + 1
                val task = ImageTask(arrayLink, context, c, arrayImage, index)
                task.execute()
            }
            else {
                dialog.dismiss()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 0
            else -> 1
        }
    }

    fun refreshItem(list_ : ArrayList<Updates>) {
        if(list_ != list) {
            list.clear()
            list.addAll(list_)
            notifyDataSetChanged()
        }
    }

    private fun reduceText(title : String, episode : String) : String {
        return if(title.length > 18) {
            title.substring(0, 18) + "..." + episode
        } else {
            title + episode
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
}