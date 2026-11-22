package com.example.madventuretv.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madventuretv.R
import com.example.madventuretv.data.Movie
import com.example.madventuretv.util.ImageLoader

/** РђРґР°РїС‚РµСЂ СЃРµС‚РєРё С„РёР»СЊРјРѕРІ. РљР°СЂС‚РѕС‡РєРё С„РѕРєСѓСЃРёСЂСѓСЋС‚СЃСЏ РїСѓР»СЊС‚РѕРј Рё СѓРІРµР»РёС‡РёРІР°СЋС‚СЃСЏ РїСЂРё РІС‹Р±РѕСЂРµ. */
class MovieAdapter(
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieHolder>() {

    private val items = ArrayList<Movie>()

    fun submit(movies: List<Movie>) {
        items.clear()
        items.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.poster)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val descr: TextView = itemView.findViewById(R.id.descr)
        private val details: TextView = itemView.findViewById(R.id.details)

        init {
            itemView.isFocusable = true
            itemView.isClickable = true
            itemView.setOnFocusChangeListener { view, hasFocus ->
                val scale = if (hasFocus) 1.05f else 1f
                view.animate().scaleX(scale).scaleY(scale).setDuration(160L).start()
            }
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(items[position])
                }
            }
        }

        fun bind(movie: Movie) {
            title.text = movie.title
            descr.text = movie.plot.ifBlank { movie.year }
            details.text = itemView.context.getString(R.string.details)
            itemView.scaleX = 1f
            itemView.scaleY = 1f
            ImageLoader.load(movie.poster, poster, radiusDp = 12f, aspect = 16f / 9f)
        }
    }
}

