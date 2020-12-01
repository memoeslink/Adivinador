package com.app.memoeslink.adivinador;

import android.preference.PreferenceManager;

import androidx.multidex.MultiDexApplication;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class ApplicationContext extends MultiDexApplication {
    private SharedPreferencesHelper preferences;
    private SharedPreferencesHelper defaultPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new SharedPreferencesHelper(this, SharedPreferencesHelper.PREFERENCES);
        defaultPreferences = new SharedPreferencesHelper(this);

        //Initialize libraries
        JodaTimeAndroid.init(this);

        //Set preference default values
        PreferenceManager.setDefaultValues(ApplicationContext.this, R.xml.default_preferences, false);

        //Delete old databases
        deleteOldDatabases();
    }

    public File getDatabaseTrace(int version) {
        if (version >= preferences.getInt("revisedDatabaseVersion", 1)) {
            String databaseName;

            if (version > 1)
                databaseName = String.format(DatabaseConnection.DATABASE_NAME_FORMAT, "_upgrade_" + (version - 1) + "-" + version);
            else
                databaseName = String.format(DatabaseConnection.DATABASE_NAME_FORMAT, "");
            File database = getDatabasePath(databaseName);

            if (database.exists()) {
                preferences.putInt("revisedDatabaseVersion", version);
                return database;
            }
            getDatabaseTrace(version - 1);
        }
        return null;
    }

    private File[] getDatabaseList() {
        try {
            File database = getDatabaseTrace(DatabaseConnection.DATABASE_VERSION);

            if (database != null) {
                File directory = database.getParentFile();
                System.out.println("Database directory: " + directory);
                File[] files = directory.listFiles();
                System.out.println("Number of files in database directory: " + files.length);

                for (int i = 0; i < files.length; i++) {
                    System.out.println("Database (" + (i + 1) + "): " + files[i].getName());
                }
                return files;
            }
            System.out.println("There's not any database.");
        } catch (Exception ignored) {
        }
        return null;
    }

    public void deleteOldDatabases() {
        File[] files = getDatabaseList();

        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!StringUtils.startsWith(file.getName(), "google_") && !file.getName().equals(DatabaseConnection.DATABASE_NAME)) {
                    if (file.exists()) {
                        if (file.delete())
                            System.out.println("Database was successfully deleted: " + file.toURI());
                        else
                            System.out.println("Database couldn't be deleted: " + file.toURI());
                    }
                }
            }
        } else
            System.out.println("There wasn't any old database to delete.");
    }
}
