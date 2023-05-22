package com.app.memoeslink.adivinador

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import androidx.preference.PreferenceManager
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler
import com.memoeslink.generator.common.Session
import java.io.File

class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        //Initialize SharedPreferences
        PreferenceHandler.init(applicationContext);

        //Initialize Generator database
        Session.getInstance().initDatabase(applicationContext)

        //Set preference default values
        PreferenceManager.setDefaultValues(
            applicationContext, R.xml.default_preferences, false
        )

        //Delete old databases
        deleteOldDatabases()

        //Initialize LifecycleEventObserver
        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    foreground = false
                    println("App is running in the background.")

                    if (!PreferenceHandler.getBoolean(Preference.SETTING_BACKGROUND_READING_ENABLED)) Speech.getInstance(
                        this@BaseApplication
                    ).suppress()
                }

                Lifecycle.Event.ON_START -> {
                    foreground = true
                    println("App is running in the foreground.")
                }

                else -> {}
            }
        })
    }

    private fun getDatabaseTrace(version: Int): File? {
        if (version >= PreferenceHandler.getInt(
                Preference.SYSTEM_REVISED_DATABASE_VERSION
            )
        ) {
            val databaseName = if (version > 1) String.format(
                Database.DATABASE_NAME_FORMAT, "_upgrade_" + (version - 1) + "-" + version
            ) else String.format(Database.DATABASE_NAME_FORMAT, "")
            val database = getDatabasePath(databaseName)

            if (database.exists()) {
                PreferenceHandler.put(Preference.SYSTEM_REVISED_DATABASE_VERSION, version)
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
            if (file.name.startsWith("google_") && file.name != Database.DATABASE_NAME) {
                when {
                    !file.exists() -> println("Database couldn't be found to be deleted: " + file.toURI())
                    !file.delete() -> println("Database couldn't be deleted: " + file.toURI())
                    else -> println("Database was successfully deleted: " + file.toURI())
                }
            }
        } ?: println("There wasn't any old database to delete.")
    }

    companion object {
        var foreground: Boolean = true
    }
}