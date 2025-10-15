package com.example.cancook.presentation.screen.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cancook.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var pageTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pageTitle = findViewById(R.id.whereView)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavContainer)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            pageTitle.text = when (destination.id) {
                R.id.menuHomeID -> "Home"
                R.id.menuSearchID -> "Search"
                R.id.menuSavedID -> "Saved"
                else -> "Home"
            }
        }//ending tag

        bottomNav.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.menuHomeID) {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
                val navController = navHostFragment.navController

                navController.popBackStack(R.id.menuHomeID, false)
            }
        }

    }
}