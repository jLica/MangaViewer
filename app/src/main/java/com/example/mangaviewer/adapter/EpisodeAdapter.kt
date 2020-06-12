package com.example.mangaviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mangaviewer.R
import com.example.mangaviewer.data.EpInfo
import com.example.mangaviewer.data.Episode
import com.example.mangaviewer.fragment.EpisodeFragment

class EpisodeAdapter(val list : ArrayList<Episode>, val frag : EpisodeFragment, val info : EpInfo, val title : String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var pa : ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View
        pa = parent
        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.title_episode, parent, false)
                TitleViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_episode, parent, false)
                MyViewHolder(view)
            }
            else -> throw RuntimeException("viewType error")
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, p: Int) {
        if(p > 0) {
            val position = list.size - p
            val holder = h as MyViewHolder
            val epText = changeTitle(list[position].eptitle)
            //val titleText = changeTitle(title)
            holder.titleText.text = epText

            holder.update_date.text = list[position].update_date
            holder.card.setOnClickListener {
                frag.setContentFragment(list[position].eptitle, list[position].link)
            }
        }
        else {
            val holder = h as TitleViewHolder
            Glide.with(pa.context).load(info.titleImage).into(holder.imageView)
            holder.artistText.text = changeTitle(info.artist)
            holder.periodText.text = info.period
        }
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val titleText : TextView = itemView.findViewById(R.id.episode_eptitle)
        val update_date : TextView = itemView.findViewById(R.id.episode_update_date)
        val card : RelativeLayout = itemView.findViewById(R.id.episode_item)
    }

    inner class TitleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.findViewById(R.id.episode_image)
        val artistText : TextView = itemView.findViewById(R.id.episode_artist)
        val periodText : TextView = itemView.findViewById(R.id.episode_period)
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> 0
            else -> 1
        }
    }

    /*private fun changeText(title : String, episode : String) : String {
        val tmp = episode.replace(title, "")

        return if(title.length > 18) {
            title.substring(0, 18) + "..." + tmp
        } else {
            episode
        }
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
}