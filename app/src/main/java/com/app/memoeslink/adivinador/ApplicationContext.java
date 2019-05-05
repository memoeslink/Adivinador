package com.app.memoeslink.adivinador;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import java.io.File;
import java.util.Locale;

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
        changeLanguage(Methods.getLanguage(ApplicationContext.this));

        //Set preference default values
        PreferenceManager.setDefaultValues(ApplicationContext.this, R.xml.default_preferences, false);

        //Delete old databases
        deleteOldDatabases();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeLanguage(Methods.getLanguage(ApplicationContext.this));
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

    @SuppressWarnings("deprecation")
    public void changeLanguage(String language) {
        if (language != null && !language.isEmpty()) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Resources res = ApplicationContext.this.getResources();
            Configuration config;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config = res.getConfiguration();
                config.setLocale(locale);
                LocaleList localeList = new LocaleList(locale);
                LocaleList.setDefault(localeList);
                config.setLocales(localeList);
            } else {
                config = res.getConfiguration();
                config.setLocale(locale);
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
    }
}