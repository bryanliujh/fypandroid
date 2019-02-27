package com.hiddenshrineoffline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class BetaVideoARActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var context: Context? = null
    private lateinit var arFragment: BetaSwarmArFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_ar)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        context = applicationContext

        startAr()

    }

    fun startAr() {
        arFragment = BetaSwarmArFragment()

        supportFragmentManager.beginTransaction().replace(R.id.ar_fragment, arFragment).commit()

    }



    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId


        when (id) {
            R.id.nav_shrine_map -> {
                val main = Intent(this@BetaVideoARActivity, MainActivity::class.java)
                startActivity(main)
            }
            R.id.nav_shrine_ar -> {
                val ar = Intent(this@BetaVideoARActivity, shrine_ar::class.java)
                startActivity(ar)
            }
            R.id.nav_game_ar -> {
                val game = Intent(this@BetaVideoARActivity, GameARActivity::class.java)
                startActivity(game)
            }
            R.id.nav_video_ar -> {
                val video = Intent(this@BetaVideoARActivity, BetaVideoARActivity::class.java)
                startActivity(video)
            }
            R.id.nav_settings -> {
                val settings = Intent(this@BetaVideoARActivity, settings::class.java)
                startActivity(settings)
            }
            R.id.nav_favourite -> {
                val favourites = Intent(this@BetaVideoARActivity, FavouriteActivity::class.java)
                startActivity(favourites)
            }
            R.id.nav_nearest_shrine -> {
                val nearest_shrine = Intent(this@BetaVideoARActivity, NearestShrineActivity::class.java)
                startActivity(nearest_shrine)
            }
            R.id.nav_beta_ar -> {
                val beta = Intent(this@BetaVideoARActivity, BetaVideoARActivity::class.java)
                startActivity(beta)
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }



}
