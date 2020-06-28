package com.ashita.myandroidapplication.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.FavouriteRestaurantsRecyclerAdapter
import com.ashita.myandroidapplication.adapter.HomeRecyclerAdapter
import com.ashita.myandroidapplication.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.fragment_favourite_restaurants.*
import kotlinx.android.synthetic.main.fragment_home.*

class WelcomeActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar : Toolbar
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout
    var previousMenuItem : MenuItem? = null
    lateinit var sharedPreferences : SharedPreferences
    lateinit var txtUserName: TextView
    lateinit var txtUserNumber: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        frameLayout = findViewById(R.id.frame)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val convertView = LayoutInflater.from(this@WelcomeActivity).inflate(R.layout.drawer_header, null)

        txtUserName = convertView.findViewById(R.id.txtUserName)
        txtUserNumber = convertView.findViewById(R.id.txtUserNumber)

        txtUserName.text = sharedPreferences.getString("Name", null)
        txtUserNumber.text = "+91-"+sharedPreferences.getString("Mobile Number", null)

        navigationView.addHeaderView(convertView)

        setUpToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@WelcomeActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){

                R.id.homeNavigation -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        ProfileFragment()
                    ).commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FavouriteRestaurantsFragment()
                    ).commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.frame,
                    OrderHistoryFragment()
                ).commit()
                supportActionBar?.title = "Order History"
                drawerLayout.closeDrawers()
            }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FAQFragment()
                    ).commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {

                    val dialog = AlertDialog.Builder(this@WelcomeActivity)
                    dialog.setTitle("Log Out?")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setCancelable(false)
                    dialog.setPositiveButton("Yes"){ _, _ ->

                        val intent = Intent(this@WelcomeActivity, LogInActivity::class.java)
                        Toast.makeText(this@WelcomeActivity, "You are logged out of the device!", Toast.LENGTH_SHORT).show()
                        sharedPreferences.edit().clear().apply()
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No"){ _, _ ->
                        //Do Nothing
                    }
                    dialog.create()
                    dialog.show()

                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openHome(){
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.homeNavigation)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag){
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }


}
