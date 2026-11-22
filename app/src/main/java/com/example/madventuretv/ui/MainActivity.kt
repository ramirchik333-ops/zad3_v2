package com.example.madventuretv.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madventuretv.R
import com.example.madventuretv.data.ApiConfig
import com.example.madventuretv.data.Channels
import com.example.madventuretv.data.Movie
import com.example.madventuretv.data.OmdbRepository

/**
 * РћСЃРЅРѕРІРЅРѕР№ СЌРєСЂР°РЅ: РјРµРЅСЋ РєР°РЅР°Р»РѕРІ, РїРѕРёСЃРє РїРѕ РЅР°Р·РІР°РЅРёСЋ Рё СЃРµС‚РєР° С„РёР»СЊРјРѕРІ.
 * РљР°СЂС‚РѕС‡РєР° С„РёР»СЊРјР° РѕС‚РєСЂС‹РІР°РµС‚СЃСЏ РЅР° РѕС‚РґРµР»СЊРЅРѕРј СЌРєСЂР°РЅРµ вЂ” С„СЂР°РіРјРµРЅС‚Рµ.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var movieAdapter: MovieAdapter

    private lateinit var channelList: RecyclerView
    private lateinit var movieList: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var statusText: TextView
    private lateinit var progress: ProgressBar
    private lateinit var detailContainer: FrameLayout
    private lateinit var demoBadge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channelList = findViewById(R.id.channels)
        movieList = findViewById(R.id.movies)
        searchInput = findViewById(R.id.search_input)
        searchButton = findViewById(R.id.search_button)
        statusText = findViewById(R.id.status)
        progress = findViewById(R.id.progress)
        detailContainer = findViewById(R.id.detail_container)
        demoBadge = findViewById(R.id.demo_badge)

        setupChannels()
        setupMovies()
        setupSearch()

        demoBadge.visibility = if (ApiConfig.hasKey) View.GONE else View.VISIBLE

        supportFragmentManager.addOnBackStackChangedListener { onDetailStackChanged() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        selectChannel(0)
    }

    private fun setupChannels() {
        channelAdapter = ChannelAdapter { index -> selectChannel(index) }
        channelList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        channelList.adapter = channelAdapter
        channelList.itemAnimator = null
    }

    private fun setupMovies() {
        movieAdapter = MovieAdapter { movie -> showDetails(movie) }
        movieList.layoutManager = GridLayoutManager(this, 3)
        movieList.adapter = movieAdapter
        movieList.itemAnimator = null
        movieList.clipChildren = false
    }

    private fun setupSearch() {
        searchButton.setOnClickListener { performSearch() }

        searchInput.setOnEditorActionListener { _, actionId, event ->
            val enter = event != null &&
                    event.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_SEARCH || enter) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun selectChannel(index: Int) {
        channelAdapter.select(index)
        loadChannel(index)
    }

    private fun loadChannel(index: Int) {
        showLoading()
        OmdbRepository.loadChannel(Channels.list[index]) { movies, error ->
            render(movies, error)
        }
    }

    private fun performSearch() {
        val query = searchInput.text.toString().trim()
        if (query.isEmpty()) {
            loadChannel(channelAdapter.selectedIndex())
            return
        }
        showLoading()
        OmdbRepository.search(query) { movies, error ->
            render(movies, error)
        }
    }

    private fun showLoading() {
        progress.visibility = View.VISIBLE
        statusText.text = getString(R.string.loading)
        statusText.visibility = View.VISIBLE
        movieAdapter.submit(emptyList())
    }

    private fun render(movies: List<Movie>?, error: String?) {
        progress.visibility = View.GONE

        if (error != null) {
            val message = if (error == OmdbRepository.ERROR_API_KEY) {
                R.string.error_api_key
            } else {
                R.string.error_network
            }
            statusText.text = getString(message)
            statusText.visibility = View.VISIBLE
            movieAdapter.submit(emptyList())
            return
        }

        val list = movies.orEmpty()
        movieAdapter.submit(list)

        if (list.isEmpty()) {
            statusText.text = getString(R.string.empty)
            statusText.visibility = View.VISIBLE
        } else {
            statusText.visibility = View.GONE
        }

        movieList.post { focusFirstCard() }
    }

    private fun focusFirstCard() {
        if (currentFocus != null) return
        val holder = movieList.findViewHolderForAdapterPosition(0) ?: return
        holder.itemView.requestFocus()
    }

    private fun showDetails(movie: Movie) {
        if (supportFragmentManager.isStateSaved) return

        detailContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.detail_container, MovieDetailFragment.newInstance(movie))
            .addToBackStack("movie_details")
            .commit()
    }

    private fun onDetailStackChanged() {
        val hasDetails = supportFragmentManager.backStackEntryCount > 0
        detailContainer.visibility = if (hasDetails) View.VISIBLE else View.GONE
        if (!hasDetails) {
            movieList.post { focusFirstCard() }
        }
    }
}


