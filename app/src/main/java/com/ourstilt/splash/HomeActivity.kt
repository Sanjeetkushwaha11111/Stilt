package com.ourstilt.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.ourstilt.base.fadeIn
import com.ourstilt.base.fadeOut
import com.ourstilt.base.hide
import com.ourstilt.base.isGone
import com.ourstilt.base.isVisible
import com.ourstilt.base.show
import com.ourstilt.databinding.ActivityHomeBinding
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupScreen()
        setupAppBarBehavior()
    }

    private fun setupScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAppBarBehavior() {
        val searchBarPinned = binding.searchBarPinned
        val searchBarFloating = binding.searchBarFloating

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = binding.appbar.totalScrollRange
            // Detect when the AppBar just starts expanding
            if (verticalOffset > -10 && verticalOffset < 0) {
                searchBarFloating.show()
                searchBarFloating.fadeIn(150)
                searchBarPinned.fadeOut(150) {
                    searchBarPinned.hide()
                }
            } else if (abs(verticalOffset) in 1 until totalScrollRange) {
                searchBarPinned.show()
                searchBarPinned.fadeIn(300)
                searchBarFloating.fadeOut(300)
            } else if (abs(verticalOffset) == totalScrollRange) {
                searchBarPinned.fadeIn(300)
                searchBarFloating.fadeOut(300)
            } else if (verticalOffset == 0) {
                searchBarFloating.fadeIn(300)
                searchBarPinned.fadeOut(300) {
                    searchBarPinned.hide()
                }
            }
        })
    }
}
