package com.app.memoeslink.adivinador;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.HashMap;

public class Database extends SQLiteAssetHelper {
    public static final int DATABASE_VERSION = 48;
    public static final String DATABASE_NAME = "words_upgrade_47-48.sqlite";
    public static final String DATABASE_NAME_FORMAT = "words%s.sqlite";
    public static final String DEFAULT_VALUE = "?";
    private static final String ID_PREFIX = "ID";
    private static final String TABLE_ENGLISH_ABSTRACT_NOUNS = "EnglishAbstractNouns";
    private static final String TABLE_ENGLISH_ACTIONS = "EnglishActions";
    private static final String TABLE_ENGLISH_PREDICTIONS = "EnglishPredictions";
    private static final String TABLE_ENGLISH_FORTUNE_COOKIES = "EnglishFortuneCookies";
    private static final String TABLE_ENGLISH_PHRASES = "EnglishPhrases";
    private static final String TABLE_SPANISH_ABSTRACT_NOUNS = "SpanishAbstractNouns";
    private static final String TABLE_SPANISH_ACTIONS = "SpanishActions";
    private static final String TABLE_SPANISH_PREDICTIONS = "SpanishPredictions";
    private static final String TABLE_SPANISH_FORTUNE_COOKIES = "SpanishFortuneCookies";
    private static final String TABLE_SPANISH_PHRASES = "SpanishPhrases";
    private static final HashMap<String, Integer> TABLE_COUNT_REGISTRY = new HashMap<>();
    private static Database instance;

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized Database getInstance(Context context) {
        if (instance == null)
            instance = new Database(context);
        return instance;
    }

    private int countRows(String table) {
        int count = TABLE_COUNT_REGISTRY.getOrDefault(table, -1);

        if (count > -1)
            return count;
        String query = "SELECT COUNT(*) FROM " + table;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        try {
            c.moveToFirst();
            count = c.getInt(0);
            TABLE_COUNT_REGISTRY.put(table, count);
            return count;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return count;
        } finally {
            c.close();
            c = null;
            db.close();
            db = null;
        }
    }

    @SuppressWarnings({"ReturnInsideFinallyBlock", "finally"})
    private String selectRow(String query) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        try {
            c.moveToFirst();
            return c.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
            return DEFAULT_VALUE;
        } finally {
            c.close();
            c = null;
            db.close();
            db = null;
        }
    }

    public int countTableRows(String table) {
        return countRows(table);
    }

    public String selectFromTable(String table, int id) {
        return selectRow("SELECT * FROM " + table + " WHERE " + table + ID_PREFIX + "=" + id);
    }

    public int countEnglishAbstractNouns() {
        return countRows(TABLE_ENGLISH_ABSTRACT_NOUNS);
    }

    public String selectEnglishAbstractNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_ABSTRACT_NOUNS + " WHERE " + TABLE_ENGLISH_ABSTRACT_NOUNS + ID_PREFIX + "=" + id);
    }

    public int countEnglishActions() {
        return countRows(TABLE_ENGLISH_ACTIONS);
    }

    public String selectEnglishAction(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_ACTIONS + " WHERE " + TABLE_ENGLISH_ACTIONS + ID_PREFIX + "=" + id);
    }

    public int countEnglishPredictions() {
        return countRows(TABLE_ENGLISH_PREDICTIONS);
    }

    public String selectEnglishPrediction(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_PREDICTIONS + " WHERE " + TABLE_ENGLISH_PREDICTIONS + ID_PREFIX + "=" + id);
    }

    public int countEnglishFortuneCookies() {
        return countRows(TABLE_ENGLISH_FORTUNE_COOKIES);
    }

    public String selectEnglishFortuneCookie(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_FORTUNE_COOKIES + " WHERE " + TABLE_ENGLISH_FORTUNE_COOKIES + ID_PREFIX + "=" + id);
    }

    public int countEnglishPhrases() {
        return countRows(TABLE_ENGLISH_PHRASES);
    }

    public String selectEnglishPhrase(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_PHRASES + " WHERE " + TABLE_ENGLISH_PHRASES + ID_PREFIX + "=" + id);
    }

    public int countSpanishAbstractNouns() {
        return countRows(TABLE_SPANISH_ABSTRACT_NOUNS);
    }

    public String selectSpanishAbstractNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ABSTRACT_NOUNS + " WHERE " + TABLE_SPANISH_ABSTRACT_NOUNS + ID_PREFIX + "=" + id);
    }

    public int countSpanishActions() {
        return countRows(TABLE_SPANISH_ACTIONS);
    }

    public String selectSpanishAction(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ACTIONS + " WHERE " + TABLE_SPANISH_ACTIONS + ID_PREFIX + "=" + id);
    }

    public int countSpanishPredictions() {
        return countRows(TABLE_SPANISH_PREDICTIONS);
    }

    public String selectSpanishPrediction(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_PREDICTIONS + " WHERE " + TABLE_SPANISH_PREDICTIONS + ID_PREFIX + "=" + id);
    }

    public int countSpanishFortuneCookies() {
        return countRows(TABLE_SPANISH_FORTUNE_COOKIES);
    }

    public String selectSpanishFortuneCookie(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_FORTUNE_COOKIES + " WHERE " + TABLE_SPANISH_FORTUNE_COOKIES + ID_PREFIX + "=" + id);
    }

    public int countSpanishPhrases() {
        return countRows(TABLE_SPANISH_PHRASES);
    }

    public String selectSpanishPhrase(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_PHRASES + " WHERE " + TABLE_SPANISH_PHRASES + ID_PREFIX + "=" + id);
    }
}
