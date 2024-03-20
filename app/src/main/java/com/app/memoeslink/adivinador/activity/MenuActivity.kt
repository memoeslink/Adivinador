package com.app.memoeslink.adivinador.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.app.memoeslink.adivinador.R
import com.google.android.material.navigation.NavigationView

open class MenuActivity : CommonActivity() {
    protected var drawerLayout: DrawerLayout? = null
    protected var navigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setHomeAsUpIndicator(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ic_list_menu, null
            )
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this@MenuActivity.menuInflater.inflate(R.menu.default_menu, menu) // Inflate Menu
        setCustomActionBar() // Set ActionBar aspect
        return true
    }

    override fun onStart() {
        super.onStart()
        invalidateOptionsMenu()
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        closeDrawer()
        return super.onMenuOpened(featureId, menu)
    }

    override fun onKeyDown(keycode: Int, e: KeyEvent): Boolean {
        when (keycode) {
            KeyEvent.KEYCODE_MENU -> closeDrawer()
            else -> {}
        }
        return super.onKeyDown(keycode, e)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        val i: Intent

        when (item.itemId) {
            android.R.id.home -> toggleDrawer()
            R.id.menu_settings -> { // Open settings
                i = Intent(this@MenuActivity, SettingsActivity::class.java)
                startActivity(i)
            }

            R.id.menu_about -> { // Show information about the app
                i = Intent(this@MenuActivity, AboutActivity::class.java)
                startActivity(i)
            }

            R.id.menu_donate -> { // Open donations
                i = Intent(this@MenuActivity, DonationsActivity::class.java)
                startActivity(i)
            }

            R.id.menu_exit -> { // Exit application
                finishAffinity()
                //android.os.Process.killProcess(android.os.Process.myPid())
                Runtime.getRuntime().exit(0) // Try to stop current threads
            }
        }
        return true
    }

    private fun toggleDrawer() = run {
        val drawerLayout = drawerLayout
        val navigationView = navigationView

        when {
            drawerLayout == null || navigationView == null -> return
            else -> {
                if (drawerLayout.isDrawerOpen(navigationView)) drawerLayout.closeDrawer(
                    GravityCompat.START
                )
                else drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    protected fun closeDrawer(): Boolean = run {
        val drawerLayout = drawerLayout
        val navigationView = navigationView

        when {
            drawerLayout == null || navigationView == null -> return false
            else -> {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                return false
            }
        }
    }
}