package com.cmi.flipbuy.activity

import activity.LoginActivity
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.media.MediaRouter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.cmi.flipbuy.*
import com.cmi.flipbuy.R
import com.cmi.flipbuy.fragment.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_drawer_header.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var imgProfileHeader: CircleImageView
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var ToolBar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var menuSignout: MenuItem
    lateinit var mDatabase: DatabaseReference
    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        ToolBar = findViewById(R.id.ToolBar)
        navigationView = findViewById(R.id.navigationView)
        frameLayout = findViewById(R.id.frameLayout)
        imgProfileHeader = navigationView.getHeaderView(0).findViewById(R.id.imgProfileHeader)

        imgProfileHeader.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, AccountFragment())
                .commit()
            drawerLayout.closeDrawers()
        }

        /*mDatabase = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = dataSnapshot.child("Name").getValue().toString()
                txtUsername.text = result
            }
        })*/

        var titleName = sharedPreferences.getString("Title", "Flip buy")
        title = titleName

        openDashboard()
        setUpToolBar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it



            when (it.itemId) {
                R.id.menu_dashboard -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.menu_Offers -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, CartFragment())
                        .commit()
                    supportActionBar?.title = "Offers"
                    drawerLayout.closeDrawers()
                }

                R.id.menu_my_cart -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, CartFragment())
                        .commit()
                    supportActionBar?.title = "Your cart"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_my_wishList -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, WishListFragment()).commit()
                    supportActionBar?.title = "Your WishList"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_my_orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrdersFragment())
                        .commit()
                    supportActionBar?.title = "Your Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_my_account -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, AccountFragment())
                        .commit()
                    supportActionBar?.title = "Your Account"
                    drawerLayout.closeDrawers()

                }
                R.id.menu_seller -> {
                    val intent = Intent(
                        this,
                        AdminCategoryActivity::class.java
                    )
                    startActivity(intent)
                    supportActionBar?.title = "Admin"
                    drawerLayout.closeDrawers()

                }
                R.id.customer_service -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, CustomerServiceFragment())
                        .commit()
                    supportActionBar?.title = "Customer Service"
                    drawerLayout.closeDrawers()
                }

                R.id.menu_signout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Confirm Signout")
                    builder.setMessage("Are you sure you want to signout of this app?")
                    builder.setPositiveButton(
                        "Signout",
                        DialogInterface.OnClickListener { _, _ ->
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            sharedPreferences.edit().clear().apply()
                            finish()
                        })
                    builder.setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { _, _ -> })
                    builder.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolBar() {
        setSupportActionBar(ToolBar)
        supportActionBar?.title = "Flip buy"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
        navigationView.setCheckedItem(R.id.menu_dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }

    }

}
