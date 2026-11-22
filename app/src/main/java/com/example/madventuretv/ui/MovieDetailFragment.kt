package com.example.madventuretv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.madventuretv.R
import com.example.madventuretv.data.ApiConfig
import com.example.madventuretv.data.Movie
import com.example.madventuretv.data.OmdbRepository
import com.example.madventuretv.util.ImageLoader

/**
 * РћС‚РґРµР»СЊРЅС‹Р№ СЌРєСЂР°РЅ СЃ РёРЅС„РѕСЂРјР°С†РёРµР№ Рѕ С„РёР»СЊРјРµ вЂ” СЂРµР°Р»РёР·РѕРІР°РЅ С„СЂР°РіРјРµРЅС‚РѕРј
 * (С‚СЂРµР±РѕРІР°РЅРёРµ Р·Р°РґР°РЅРёСЏ: РІС‹РІРѕРґ РёРЅС„РѕСЂРјР°С†РёРё Рѕ С„РёР»СЊРјРµ С‡РµСЂРµР· Fragment).
 */
class MovieDetailFragment : Fragment() {

    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var genre: TextView
    private lateinit var meta: TextView
    private lateinit var plot: TextView
    private lateinit var credits: TextView
    private lateinit var progress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_movie_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        poster = view.findViewById(R.id.poster)
        title = view.findViewById(R.id.title)
        genre = view.findViewById(R.id.genre)
        meta = view.findViewById(R.id.meta)
        plot = view.findViewById(R.id.plot)
        credits = view.findViewById(R.id.credits)
        progress = view.findViewById(R.id.progress)

        val back = view.findViewById<Button>(R.id.back)
        back.setOnClickListener { parentFragmentManager.popBackStack() }

        val movie = movieFromArguments()
        bind(movie)

        if (!movie.demo && movie.id.isNotBlank() && ApiConfig.hasKey) {
            progress.visibility = View.VISIBLE
            OmdbRepository.loadDetails(movie) { detailed, _ ->
                if (isAdded) {
                    progress.visibility = View.GONE
                    if (detailed != null) bind(detailed)
                }
            }
        } else {
            progress.visibility = View.GONE
        }

        back.post { back.requestFocus() }
    }

    private fun bind(movie: Movie) {
        title.text = movie.title

        val metaParts = ArrayList<String>(3)
        if (movie.year.isNotBlank()) metaParts.add(movie.year)
        if (movie.runtime.isNotBlank()) metaParts.add(movie.runtime)
        if (movie.rating.isNotBlank()) metaParts.add("в… ${movie.rating}")
        meta.text = metaParts.joinToString("   вЂў   ")
        meta.visibility = if (metaParts.isEmpty()) View.GONE else View.VISIBLE

        genre.text = movie.genre
        genre.visibility = if (movie.genre.isBlank()) View.GONE else View.VISIBLE

        plot.text = movie.plot
        plot.visibility = if (movie.plot.isBlank()) View.GONE else View.VISIBLE

        val creditLines = ArrayList<String>(2)
        if (movie.director.isNotBlank()) {
            creditLines.add(getString(R.string.director_label) + ": " + movie.director)
        }
        if (movie.actors.isNotBlank()) {
            creditLines.add(getString(R.string.actors_label) + ": " + movie.actors)
        }
        credits.text = creditLines.joinToString("\n")
        credits.visibility = if (creditLines.isEmpty()) View.GONE else View.VISIBLE

        ImageLoader.load(
            movie.poster,
            poster,
            radiusDp = 16f,
            aspect = 2f / 3f,
            placeholder = R.drawable.poster_placeholder_portrait
        )
    }

    private fun movieFromArguments(): Movie {
        val args = arguments ?: return Movie()
        return Movie(
            id = args.getString(ARG_ID).orEmpty(),
            title = args.getString(ARG_TITLE).orEmpty(),
            year = args.getString(ARG_YEAR).orEmpty(),
            poster = args.getString(ARG_POSTER).orEmpty(),
            plot = args.getString(ARG_PLOT).orEmpty(),
            genre = args.getString(ARG_GENRE).orEmpty(),
            rating = args.getString(ARG_RATING).orEmpty(),
            runtime = args.getString(ARG_RUNTIME).orEmpty(),
            director = args.getString(ARG_DIRECTOR).orEmpty(),
            actors = args.getString(ARG_ACTORS).orEmpty(),
            demo = args.getBoolean(ARG_DEMO, false)
        )
    }

    companion object {

        private const val ARG_ID = "id"
        private const val ARG_TITLE = "title"
        private const val ARG_YEAR = "year"
        private const val ARG_POSTER = "poster"
        private const val ARG_PLOT = "plot"
        private const val ARG_GENRE = "genre"
        private const val ARG_RATING = "rating"
        private const val ARG_RUNTIME = "runtime"
        private const val ARG_DIRECTOR = "director"
        private const val ARG_ACTORS = "actors"
        private const val ARG_DEMO = "demo"

        fun newInstance(movie: Movie): MovieDetailFragment {
            val fragment = MovieDetailFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_ID, movie.id)
                putString(ARG_TITLE, movie.title)
                putString(ARG_YEAR, movie.year)
                putString(ARG_POSTER, movie.poster)
                putString(ARG_PLOT, movie.plot)
                putString(ARG_GENRE, movie.genre)
                putString(ARG_RATING, movie.rating)
                putString(ARG_RUNTIME, movie.runtime)
                putString(ARG_DIRECTOR, movie.director)
                putString(ARG_ACTORS, movie.actors)
                putBoolean(ARG_DEMO, movie.demo)
            }
            return fragment
        }
    }
}


