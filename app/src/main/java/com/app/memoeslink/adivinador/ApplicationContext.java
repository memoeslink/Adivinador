package com.app.memoeslink.adivinador;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

import java.io.File;

/**
 * Created by Memoeslink on 11/05/2016.
 */
public class ApplicationContext extends MultiDexApplication {
    private SharedPreferences defaultPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Set language (this needs to be before any other method)
        Methods.changeLanguage(ApplicationContext.this);

        //Set preference default values
        PreferenceManager.setDefaultValues(ApplicationContext.this, R.xml.default_preferences, false);

        //Delete old databases
        deleteOldDatabases();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Methods.changeLanguage(ApplicationContext.this);
    }

    private File[] getDatabaseList() {
        try {
            File database = null;

            for (int n = DatabaseConnection.DATABASE_VERSION; n >= 1; n--) {
                if (n == DatabaseConnection.DATABASE_VERSION)
                    database = getDatabasePath(DatabaseConnection.DATABASE_NAME);
                else if (n == 1)
                    database = getDatabasePath(getString(R.string.default_database));
                else
                    database = getDatabasePath(String.format(getString(R.string.default_database_version), n - 1, n));

                if (database.exists()) {
                    File directory = database.getParentFile();
                    System.out.println("Database directory: " + directory);
                    File[] files = directory.listFiles();
                    System.out.println("Number of files in database directory: " + files.length);

                    for (int i = 0; i < files.length; i++) {
                        System.out.println("Database (" + (i + 1) + "): " + files[i].getName());
                    }
                    return files;
                }
            }
            System.out.println("There's not any database.");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteOldDatabases() {
        File[] files = getDatabaseList();

        if (files != null && files.length > 0) {
            for (int n = 0; n < files.length; n++) {
                if (!files[n].getName().equals(DatabaseConnection.DATABASE_NAME)) {
                    if (files[n].exists()) {
                        if (files[n].delete())
                            System.out.println("Database was successfully deleted: " + files[n].toURI());
                        else
                            System.out.println("Database couldn't be deleted: " + files[n].toURI());
                    }
                }
            }
        } else
            System.out.println("There wasn't any old database to delete.");
    }
}
