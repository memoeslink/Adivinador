package com.app.memoeslink.adivinador

import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.memoeslink.generator.common.Session
import com.memoeslink.generator.common.StringHelper
import com.memoeslink.helper.SharedPreferencesHelper
import java.io.File

class BaseApplication : MultiDexApplication() {
    private var preferences: SharedPreferencesHelper? = null
    private var defaultPreferences: SharedPreferencesHelper? = null

    override fun onCreate() {
        super.onCreate()
        preferences = SharedPreferencesHelper.getPreferencesHelper(this@BaseApplication)
        defaultPreferences = SharedPreferencesHelper(this@BaseApplication)

        //Initialize Generator database
        Session.getInstance().initDatabase(this@BaseApplication)

        //Set preference default values
        PreferenceManager.setDefaultValues(
            this@BaseApplication,
            R.xml.default_preferences,
            false
        )

        //Delete old databases
        deleteOldDatabases()
    }

    private fun getDatabaseTrace(version: Int): File? {
        if (version >= preferences?.getIntOrNull(Preference.SYSTEM_REVISED_DATABASE_VERSION.name) ?: 1) {
            val databaseName = if (version > 1) {
                String.format(
                    Database.DATABASE_NAME_FORMAT,
                    "_upgrade_" + (version - 1) + "-" + version
                )
            } else {
                String.format(Database.DATABASE_NAME_FORMAT, "")
            }
            val database = getDatabasePath(databaseName)

            if (database.exists()) {
                preferences?.put(Preference.SYSTEM_REVISED_DATABASE_VERSION.name, version)
                return database
            }
            getDatabaseTrace(version - 1)
        }
        return null
    }

    private fun getDatabaseList(): Array<File>? {
        try {
            getDatabaseTrace(Database.DATABASE_VERSION)?.parentFile.let { directory ->
                val files = directory?.listFiles()
                println("Database directory: $directory")
                println("Number of files in database directory: " + files?.size)

                files?.forEachIndexed { index, file ->
                    println("Database (" + (index + 1) + "): " + file.name)
                }
                return files
            }
        } catch (ignored: Exception) {
            println("There's not any database.")
            return null
        }
    }

    private fun deleteOldDatabases() {
        val files = getDatabaseList()

        files?.takeUnless {
            files.isEmpty()
        }?.forEach { file ->
            if (!StringHelper.startsWith(
                    file.name,
                    "google_"
                ) && file.name != Database.DATABASE_NAME
            ) {
                when {
                    !file.exists() -> println("Database couldn't be found to be deleted: " + file.toURI())
                    !file.delete() -> println("Database couldn't be deleted: " + file.toURI())
                    else -> println("Database was successfully deleted: " + file.toURI())
                }
            }
        } ?: println("There wasn't any old database to delete.")
    }
}