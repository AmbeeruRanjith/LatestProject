package com.example.latestproject

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.latestproject.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val titleView = findViewById<TextView>(R.id.customTitle)
        titleView.text = "AgriBazar"

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.categories -> loadFragment(CategoriesFragment())
                R.id.organic -> loadFragment(OrganicFragment())
                R.id.cart -> loadFragment(CartFragment())
                R.id.profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.apply {
            queryHint = "Search for items..."
            setBackgroundResource(R.drawable.search_view_background)

            try {
                val field = SearchView::class.java.getDeclaredField("mSearchSrcTextView")
                field.isAccessible = true
                val searchEditText = field.get(this) as? EditText
                searchEditText?.setTextColor(getColor(android.R.color.black))
                searchEditText?.setHintTextColor(getColor(android.R.color.darker_gray))
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error accessing search field", Toast.LENGTH_SHORT).show()
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Handle search submit
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Handle real-time search input
                    return true
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
