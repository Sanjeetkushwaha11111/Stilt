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
import com.ourstilt.R
import com.ourstilt.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val fruits = listOf(
        "Apple", "Banana", "Orange", "Mango", "Pineapple", "Strawberry", "Blueberry",
        "Grapes", "Watermelon", "Peach", "Plum", "Kiwi", "Papaya", "Pear", "Cherry",
        "Pomegranate", "Dragonfruit", "Lychee", "Guava", "Apricot", "Cantaloupe"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupScreen()
        binding.userIv.setOnClickListener {
            binding.topBg.visibility= View.VISIBLE
        }
        setupRecyclerView()
        setupAppBarBehavior()
    }

    private fun setupScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = FruitsAdapter(fruits)
        recyclerView.adapter = adapter
    }

    private fun setupAppBarBehavior() {
        // Get references to the search bars
        val searchBarPinned = binding.searchBarPinned
        val searchBarFloating = binding.searchBarFloating

        // Set up AppBarLayout to detect scroll changes
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = binding.appbar.totalScrollRange

            // Detect when the AppBar just starts expanding
            if (verticalOffset > -10 && verticalOffset < 0) {
                // AppBar just started expanding
                searchBarFloating.visibility = View.VISIBLE
                searchBarFloating.animate()
                    .alpha(1f) // Fade in floating search bar
                    .setDuration(150) // Short duration for smooth transition
                    .start()

                searchBarPinned.animate()
                    .alpha(0f) // Fade out pinned search bar
                    .setDuration(150)
                    .withEndAction {
                        searchBarPinned.visibility = View.GONE // Hide after fade out
                    }
                    .start()
            }

            // AppBar is collapsing, show pinned search bar and hide floating
            else if (Math.abs(verticalOffset) in 1 until totalScrollRange) {
                searchBarPinned.visibility = View.VISIBLE
                searchBarPinned.animate()
                    .alpha(1f) // Fade in
                    .setDuration(300)
                    .start()

                searchBarFloating.animate()
                    .alpha(0f) // Fade out
                    .setDuration(300)
                    .start()
            }

            // AppBar is fully collapsed
            else if (Math.abs(verticalOffset) == totalScrollRange) {
                searchBarPinned.animate()
                    .alpha(1f) // Ensure it's fully visible
                    .setDuration(300)
                    .start()

                searchBarFloating.animate()
                    .alpha(0f) // Fade out floating search bar
                    .setDuration(300)
                    .start()
            }

            // AppBar is fully expanded
            else if (verticalOffset == 0) {
                searchBarFloating.animate()
                    .alpha(1f) // Fade in floating search bar
                    .setDuration(300)
                    .start()

                searchBarPinned.animate()
                    .alpha(0f) // Fade out
                    .setDuration(300)
                    .withEndAction {
                        searchBarPinned.visibility = View.GONE
                    }
                    .start()
            }
        })
    }


}

class FruitsAdapter(private val fruits: List<String>) :
    RecyclerView.Adapter<FruitsAdapter.FruitViewHolder>() {

    inner class FruitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fruitName: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return FruitViewHolder(view)
    }

    override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
        holder.fruitName.text = fruits[position]
    }

    override fun getItemCount(): Int = fruits.size
}
