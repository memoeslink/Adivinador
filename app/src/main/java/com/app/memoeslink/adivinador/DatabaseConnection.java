package com.app.memoeslink.adivinador;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.HashMap;

/**
 * Created by Memoeslink on 19/08/2017.
 */

class DatabaseConnection extends SQLiteAssetHelper {
    public static final int DATABASE_VERSION = 44;
    public static final String DATABASE_NAME = "words_upgrade_43-44.sqlite";
    public static final String DATABASE_NAME_FORMAT = "words%s.sqlite";
    private static final String ID_PREFIX = "ID";
    private static final String TABLE_ENGLISH_ABSTRACT_NOUNS = "EnglishAbstractNouns";
    private static final String TABLE_ENGLISH_ACTIONS = "EnglishActions";
    private static final String TABLE_ENGLISH_DIVINATIONS = "EnglishDivinations";
    private static final String TABLE_ENGLISH_ADJECTIVES = "EnglishAdjectives";
    private static final String TABLE_ENGLISH_FEMALE_NAMES = "EnglishFemaleNames";
    private static final String TABLE_ENGLISH_FORTUNE_COOKIES = "EnglishFortuneCookies";
    private static final String TABLE_ENGLISH_MALE_NAMES = "EnglishMaleNames";
    private static final String TABLE_ENGLISH_NOUNS = "EnglishNouns";
    private static final String TABLE_ENGLISH_OCCUPATIONS = "EnglishOccupations";
    private static final String TABLE_ENGLISH_PHRASES = "EnglishPhrases";
    private static final String TABLE_ENGLISH_SURNAMES = "EnglishSurnames";
    private static final String TABLE_SPANISH_ABSTRACT_NOUNS = "SpanishAbstractNouns";
    private static final String TABLE_SPANISH_ACTIONS = "SpanishActions";
    private static final String TABLE_SPANISH_DIVINATIONS = "SpanishDivinations";
    private static final String TABLE_SPANISH_FEMALE_NAMES = "SpanishFemaleNames";
    private static final String TABLE_SPANISH_FORTUNE_COOKIES = "SpanishFortuneCookies";
    private static final String TABLE_SPANISH_MALE_NAMES = "SpanishMaleNames";
    private static final String TABLE_SPANISH_NOUNS = "SpanishNouns";
    private static final String TABLE_SPANISH_OCCUPATIONS = "SpanishOccupations";
    private static final String TABLE_SPANISH_PHRASES = "SpanishPhrases";
    private static final String TABLE_SPANISH_PLURAL_ADJECTIVES = "SpanishPluralAdjectives";
    private static final String TABLE_SPANISH_SINGULAR_ADJECTIVES = "SpanishSingularAdjectives";
    private static final String TABLE_SPANISH_SURNAMES = "SpanishSurnames";
    private static final String TABLE_FAMILY_NAMES = "FamilyNames";
    private static final String TABLE_NAMES = "Names";
    private static final String TABLE_NOUNS = "Nouns";
    private static final String TABLE_USERNAMES = "Usernames";
    private static HashMap<String, Integer> hashMap = new HashMap<>();
    private SQLiteDatabase db;

    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    private int countRows(String table) {
        return countRows(table, "SELECT COUNT(*) FROM " + table);
    }

    private int countRows(String table, String query) {
        int count = -1;
        Integer value = getIntValue(table);

        if (value == -1) {
            try {
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();
                count = c.getInt(0);
                c.close();
                c = null;
                hashMap.put(table, count);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            return count;
        } else
            return value;
    }

    private int getIntValue(String key) {
        Integer value = hashMap.get(key);

        if (value != null)
            return value;
        return -1;
    }

    @SuppressWarnings({"ReturnInsideFinallyBlock", "finally"})
    private String selectRow(String query) {
        Cursor c = db.rawQuery(query, null);
        String result = "";

        try {
            c.moveToFirst();
            result = c.getString(1);
        } catch (Exception e) {
            result = "?";
        } finally {
            c.close();
            c = null;
            return result;
        }
    }

    public int countTableRows(String table) {
        return countRows(table);
    }

    public String selectFromTable(String table, int id) {
        return selectRow("SELECT * FROM " + table + " WHERE " + table + ID_PREFIX + "=" + id);
    }

    public int countEnglishAdjectives() {
        return countRows(TABLE_ENGLISH_ADJECTIVES);
    }

    public String selectEnglishAdjective(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_ADJECTIVES + " WHERE " + TABLE_ENGLISH_ADJECTIVES + ID_PREFIX + "=" + id);
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

    public int countEnglishDivinations() {
        return countRows(TABLE_ENGLISH_DIVINATIONS);
    }

    public String selectEnglishDivination(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_DIVINATIONS + " WHERE " + TABLE_ENGLISH_DIVINATIONS + ID_PREFIX + "=" + id);
    }

    public int countEnglishFemaleNames() {
        return countRows(TABLE_ENGLISH_FEMALE_NAMES);
    }

    public String selectEnglishFemaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_FEMALE_NAMES + " WHERE " + TABLE_ENGLISH_FEMALE_NAMES + ID_PREFIX + "=" + id);
    }

    public int countEnglishFortuneCookies() {
        return countRows(TABLE_ENGLISH_FORTUNE_COOKIES);
    }

    public String selectEnglishFortuneCookie(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_FORTUNE_COOKIES + " WHERE " + TABLE_ENGLISH_FORTUNE_COOKIES + ID_PREFIX + "=" + id);
    }

    public int countEnglishMaleNames() {
        return countRows(TABLE_ENGLISH_MALE_NAMES);
    }

    public String selectEnglishMaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_MALE_NAMES + " WHERE " + TABLE_ENGLISH_MALE_NAMES + ID_PREFIX + "=" + id);
    }

    public int countEnglishNouns() {
        return countRows(TABLE_ENGLISH_NOUNS);
    }

    public String selectEnglishNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_NOUNS + " WHERE " + TABLE_ENGLISH_NOUNS + ID_PREFIX + "=" + id);
    }

    public int countEnglishOccupations() {
        return countRows(TABLE_ENGLISH_OCCUPATIONS);
    }

    public String selectEnglishOccupation(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_OCCUPATIONS + " WHERE " + TABLE_ENGLISH_OCCUPATIONS + ID_PREFIX + "=" + id);
    }

    public int countEnglishPhrases() {
        return countRows(TABLE_ENGLISH_PHRASES);
    }

    public String selectEnglishPhrase(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_PHRASES + " WHERE " + TABLE_ENGLISH_PHRASES + ID_PREFIX + "=" + id);
    }

    public int countEnglishSurnames() {
        return countRows(TABLE_ENGLISH_SURNAMES);
    }

    public String selectEnglishSurname(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_SURNAMES + " WHERE " + TABLE_ENGLISH_SURNAMES + ID_PREFIX + "=" + id);
    }

    public int countAbstractNouns() {
        return countRows(TABLE_SPANISH_ABSTRACT_NOUNS);
    }

    public String selectAbstractNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ABSTRACT_NOUNS + " WHERE " + TABLE_SPANISH_ABSTRACT_NOUNS + ID_PREFIX + "=" + id);
    }

    public int countActions() {
        return countRows(TABLE_SPANISH_ACTIONS);
    }

    public String selectAction(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ACTIONS + " WHERE " + TABLE_SPANISH_ACTIONS + ID_PREFIX + "=" + id);
    }

    public int countDivinations() {
        return countRows(TABLE_SPANISH_DIVINATIONS);
    }

    public String selectDivination(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_DIVINATIONS + " WHERE " + TABLE_SPANISH_DIVINATIONS + ID_PREFIX + "=" + id);
    }

    public int countFemaleNames() {
        return countRows(TABLE_SPANISH_FEMALE_NAMES);
    }

    public String selectFemaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_FEMALE_NAMES + " WHERE " + TABLE_SPANISH_FEMALE_NAMES + ID_PREFIX + "=" + id);
    }

    public int countFortuneCookies() {
        return countRows(TABLE_SPANISH_FORTUNE_COOKIES);
    }

    public String selectFortuneCookie(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_FORTUNE_COOKIES + " WHERE " + TABLE_SPANISH_FORTUNE_COOKIES + ID_PREFIX + "=" + id);
    }

    public int countMaleNames() {
        return countRows(TABLE_SPANISH_MALE_NAMES);
    }

    public String selectMaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_MALE_NAMES + " WHERE " + TABLE_SPANISH_MALE_NAMES + ID_PREFIX + "=" + id);
    }

    public int countNouns() {
        return countRows(TABLE_SPANISH_NOUNS);
    }

    public String selectNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_NOUNS + " WHERE " + TABLE_SPANISH_NOUNS + ID_PREFIX + "=" + id);
    }

    public int countOccupations() {
        return countRows(TABLE_SPANISH_OCCUPATIONS);
    }

    public String selectOccupation(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_OCCUPATIONS + " WHERE " + TABLE_SPANISH_OCCUPATIONS + ID_PREFIX + "=" + id);
    }

    public int countPhrases() {
        return countRows(TABLE_SPANISH_PHRASES);
    }

    public String selectPhrase(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_PHRASES + " WHERE " + TABLE_SPANISH_PHRASES + ID_PREFIX + "=" + id);
    }

    public int countPluralAdjectives() {
        return countRows(TABLE_SPANISH_PLURAL_ADJECTIVES);
    }

    public String selectPluralAdjective(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_PLURAL_ADJECTIVES + " WHERE " + TABLE_SPANISH_PLURAL_ADJECTIVES + ID_PREFIX + "=" + id);
    }

    public int countSingularAdjectives() {
        return countRows(TABLE_SPANISH_SINGULAR_ADJECTIVES);
    }

    public String selectSingularAdjective(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_SINGULAR_ADJECTIVES + " WHERE " + TABLE_SPANISH_SINGULAR_ADJECTIVES + ID_PREFIX + "=" + id);
    }

    public int countSurnames() {
        return countRows(TABLE_SPANISH_SURNAMES);
    }

    public String selectSurname(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_SURNAMES + " WHERE " + TABLE_SPANISH_SURNAMES + ID_PREFIX + "=" + id);
    }

    public int countFamilyNames() {
        return countRows(TABLE_FAMILY_NAMES);
    }

    public String selectFamilyName(int id) {
        return selectRow("SELECT * FROM " + TABLE_FAMILY_NAMES + " WHERE " + TABLE_FAMILY_NAMES + ID_PREFIX + "=" + id);
    }

    public int countNames() {
        return countRows(TABLE_NAMES);
    }

    public String selectName(int id) {
        return selectRow("SELECT * FROM " + TABLE_NAMES + " WHERE " + TABLE_NAMES + ID_PREFIX + "=" + id);
    }

    public int countCommonNouns() {
        return countRows(TABLE_NOUNS);
    }

    public String selectCommonNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_NOUNS + " WHERE " + TABLE_NOUNS + ID_PREFIX + "=" + id);
    }

    public int countUsernames() {
        return countRows(TABLE_USERNAMES);
    }

    public String selectUsername(int id) {
        return selectRow("SELECT * FROM " + TABLE_USERNAMES + " WHERE " + TABLE_USERNAMES + ID_PREFIX + "=" + id);
    }
}
