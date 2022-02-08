package com.app.memoeslink.adivinador;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends CommonActivity {
    protected DrawerLayout drawerLayout = null;
    protected NavigationView navigationView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_menu, null));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuActivity.this.getMenuInflater().inflate(R.menu.default_menu, menu); //Inflate Menu
        setCustomActionBar(); //Set ActionBar aspect
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        closeDrawer();
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                closeDrawer();
                break;
        }
        return super.onKeyDown(keycode, e);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent i;

        switch (item.getItemId()) {
            case android.R.id.home:
                toggleDrawer();
                break;
            case R.id.menu_set_data: //Set data for consultation
                i = new Intent(MenuActivity.this, InputActivity.class);
                startActivity(i);
                break;
            case R.id.menu_settings: //Open settings
                i = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_about: //Show information about application
                i = new Intent(MenuActivity.this, AboutActivity.class);
                startActivity(i);
                break;
            case R.id.menu_donate: //Show Activity for donations
                i = new Intent(MenuActivity.this, DonationsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_exit: //Exit application
                finishAffinity();
                //android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0); //Try to stop current threads
                break;
        }
        return true;
    }

    protected void toggleDrawer() {
        if (drawerLayout != null && navigationView != null) {
            if (!drawerLayout.isDrawerOpen(navigationView))
                drawerLayout.openDrawer(GravityCompat.START);
            else
                drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    protected boolean closeDrawer() {
        if (drawerLayout != null && navigationView != null && drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
