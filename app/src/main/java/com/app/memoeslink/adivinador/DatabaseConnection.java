package com.app.memoeslink.adivinador;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Memoeslink on 19/08/2017.
 */

class DatabaseConnection extends SQLiteAssetHelper {
    public static final String DATABASE_NAME = "words_upgrade_39-40.sqlite";
    public static final int DATABASE_VERSION = 40;
    private static final String ID_PREFIX = "ID";
    private static final String TABLE_ENGLISH_ABSTRACT_NOUN = "EN_AbstractNoun";
    private static final String TABLE_ENGLISH_ACTION = "EN_Action";
    private static final String TABLE_ENGLISH_DIVINATION = "EN_Divination";
    private static final String TABLE_ENGLISH_ADJECTIVE = "EN_Adjective";
    private static final String TABLE_ENGLISH_FEMALE_NAME = "EN_FemaleName";
    private static final String TABLE_ENGLISH_FORTUNE_COOKIE = "EN_FortuneCookie";
    private static final String TABLE_ENGLISH_MALE_NAME = "EN_MaleName";
    private static final String TABLE_ENGLISH_NOUN = "EN_Noun";
    private static final String TABLE_ENGLISH_OCCUPATION = "EN_Occupation";
    private static final String TABLE_ENGLISH_PHRASE = "EN_Phrase";
    private static final String TABLE_ENGLISH_SURNAME = "EN_Surname";
    private static final String TABLE_SPANISH_ABSTRACT_NOUN = "ES_AbstractNoun";
    private static final String TABLE_SPANISH_ACTION = "ES_Action";
    private static final String TABLE_SPANISH_ADJECTIVE = "ES_Adjective";
    private static final String TABLE_SPANISH_DIVINATION = "ES_Divination";
    private static final String TABLE_SPANISH_FEMALE_NAME = "ES_FemaleName";
    private static final String TABLE_SPANISH_FORTUNE_COOKIE = "ES_FortuneCookie";
    private static final String TABLE_SPANISH_MALE_NAME = "ES_MaleName";
    private static final String TABLE_SPANISH_NOUN = "ES_Noun";
    private static final String TABLE_SPANISH_OCCUPATION = "ES_Occupation";
    private static final String TABLE_SPANISH_PHRASE = "ES_Phrase";
    private static final String TABLE_SPANISH_SURNAME = "ES_Surname";
    private static final String TABLE_FAMILY_NAME = "FamilyName";
    private static final String TABLE_NAME = "Name";
    private static final String TABLE_NOUN = "Noun";
    private static final String TABLE_USERNAME = "Username";
    private static final String FIELD_TYPE = "Type";
    private static HashMap<String, Integer> hashMap = new HashMap<>();
    private SQLiteDatabase db;

    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    @SuppressWarnings("ReturnInsideFinallyBlock")
    private int countRows(String table) {
        int count = -1;
        Integer value = getIntValue(table);

        if (value == -1) {
            try {
                Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
                c.moveToFirst();
                count = c.getInt(0);
                c.close();
                hashMap.put(table, count);
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                return count;
            }
        } else
            return value;
    }

    private Integer getIntValue(String key) {
        Integer value = hashMap.get(key);

        if (value != null)
            return hashMap.get(key).intValue();
        else {
            if (hashMap.containsKey(key))
                return -1;
            else
                return -1;
        }
    }

    @SuppressWarnings({"ReturnInsideFinallyBlock", "finally"})
    private String selectRow(String query) {
        Cursor cursor = db.rawQuery(query, null);
        String result = "";

        try {
            cursor.moveToFirst();
            result = cursor.getString(1);
        } catch (Exception e) {
            result = "?";
        } finally {
            cursor.close();
            return result;
        }
    }

    private List<String> selectRows(String query) {
        Cursor cursor = db.rawQuery(query, null);
        List<String> results = new ArrayList<>();

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                results.add(cursor.getString(1));
            }
        } catch (Exception e) {
            results = null;
        } finally {
            cursor.close();
            return results;
        }
    }

    public int countUnknownRow(String table) {
        return countRows(table);
    }

    public String selectUnknownRow(String table, int id) {
        return selectRow("SELECT * FROM " + table + " WHERE " + table + ID_PREFIX + "=" + id);
    }

    public int countEnglishAdjectives() {
        return countRows(TABLE_ENGLISH_ADJECTIVE);
    }

    public String selectEnglishAdjective(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_ADJECTIVE + " WHERE " + TABLE_ENGLISH_ADJECTIVE + ID_PREFIX + "=" + id);
    }

    public int countEnglishAbstractNouns() {
        return countRows(TABLE_ENGLISH_ABSTRACT_NOUN);
    }

    public String selectEnglishAbstractNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_ABSTRACT_NOUN + " WHERE " + TABLE_ENGLISH_ABSTRACT_NOUN + ID_PREFIX + "=" + id);
    }

    public int countEnglishActions() {
        return countRows(TABLE_ENGLISH_ACTION);
    }

    public String selectEnglishAction(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_ACTION + " WHERE " + TABLE_ENGLISH_ACTION + ID_PREFIX + "=" + id);
    }

    public int countEnglishDivinations() {
        return countRows(TABLE_ENGLISH_DIVINATION);
    }

    public String selectEnglishDivination(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_DIVINATION + " WHERE " + TABLE_ENGLISH_DIVINATION + ID_PREFIX + "=" + id);
    }

    public int countEnglishFemaleNames() {
        return countRows(TABLE_ENGLISH_FEMALE_NAME);
    }

    public String selectEnglishFemaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_FEMALE_NAME + " WHERE " + TABLE_ENGLISH_FEMALE_NAME + ID_PREFIX + "=" + id);
    }

    public int countEnglishFortuneCookies() {
        return countRows(TABLE_ENGLISH_FORTUNE_COOKIE);
    }

    public String selectEnglishFortuneCookie(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_FORTUNE_COOKIE + " WHERE " + TABLE_ENGLISH_FORTUNE_COOKIE + ID_PREFIX + "=" + id);
    }

    public int countEnglishMaleNames() {
        return countRows(TABLE_ENGLISH_MALE_NAME);
    }

    public String selectEnglishMaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_MALE_NAME + " WHERE " + TABLE_ENGLISH_MALE_NAME + ID_PREFIX + "=" + id);
    }

    public int countEnglishNouns() {
        return countRows(TABLE_ENGLISH_NOUN);
    }

    public String selectEnglishNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_NOUN + " WHERE " + TABLE_ENGLISH_NOUN + ID_PREFIX + "=" + id);
    }

    public List<String> selectEnglishNouns() {
        return selectRows("SELECT * FROM " + TABLE_ENGLISH_NOUN);
    }

    public int countEnglishOccupations() {
        return countRows(TABLE_ENGLISH_OCCUPATION);
    }

    public String selectEnglishOccupation(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_OCCUPATION + " WHERE " + TABLE_ENGLISH_OCCUPATION + ID_PREFIX + "=" + id);
    }

    public int countEnglishPhrases() {
        return countRows(TABLE_ENGLISH_PHRASE);
    }

    public String selectEnglishPhrase(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_PHRASE + " WHERE " + TABLE_ENGLISH_PHRASE + ID_PREFIX + "=" + id);
    }

    public int countEnglishSurnames() {
        return countRows(TABLE_ENGLISH_SURNAME);
    }

    public String selectEnglishSurname(int id) {
        return selectRow("SELECT * FROM " + TABLE_ENGLISH_SURNAME + " WHERE " + TABLE_ENGLISH_SURNAME + ID_PREFIX + "=" + id);
    }

    public int countAbstractNouns() {
        return countRows(TABLE_SPANISH_ABSTRACT_NOUN);
    }

    public String selectAbstractNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ABSTRACT_NOUN + " WHERE " + TABLE_SPANISH_ABSTRACT_NOUN + ID_PREFIX + "=" + id);
    }

    public int countActions() {
        return countRows(TABLE_SPANISH_ACTION);
    }

    public String selectAction(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ACTION + " WHERE " + TABLE_SPANISH_ACTION + ID_PREFIX + "=" + id);
    }

    public int countAdjectives() {
        return countRows(TABLE_SPANISH_ADJECTIVE);
    }

    public String selectAdjective(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_ADJECTIVE + " WHERE " + TABLE_SPANISH_ADJECTIVE + ID_PREFIX + "=" + id);
    }

    public List<String> selectPluralAdjectives() {
        return selectRows("SELECT * FROM " + TABLE_SPANISH_ADJECTIVE + " WHERE " + FIELD_TYPE + "=" + "\'p\'");
    }

    public List<String> selectSingularAdjectives() {
        return selectRows("SELECT * FROM " + TABLE_SPANISH_ADJECTIVE + " WHERE " + FIELD_TYPE + "=" + "\'s\'");
    }

    public int countDivinations() {
        return countRows(TABLE_SPANISH_DIVINATION);
    }

    public String selectDivination(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_DIVINATION + " WHERE " + TABLE_SPANISH_DIVINATION + ID_PREFIX + "=" + id);
    }

    public int countFemaleNames() {
        return countRows(TABLE_SPANISH_FEMALE_NAME);
    }

    public String selectFemaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_FEMALE_NAME + " WHERE " + TABLE_SPANISH_FEMALE_NAME + ID_PREFIX + "=" + id);
    }

    public int countFortuneCookies() {
        return countRows(TABLE_SPANISH_FORTUNE_COOKIE);
    }

    public String selectFortuneCookie(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_FORTUNE_COOKIE + " WHERE " + TABLE_SPANISH_FORTUNE_COOKIE + ID_PREFIX + "=" + id);
    }

    public int countMaleNames() {
        return countRows(TABLE_SPANISH_MALE_NAME);
    }

    public String selectMaleName(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_MALE_NAME + " WHERE " + TABLE_SPANISH_MALE_NAME + ID_PREFIX + "=" + id);
    }

    public int countNouns() {
        return countRows(TABLE_SPANISH_NOUN);
    }

    public String selectNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_NOUN + " WHERE " + TABLE_SPANISH_NOUN + ID_PREFIX + "=" + id);
    }

    public List<String> selectNouns() {
        return selectRows("SELECT * FROM " + TABLE_SPANISH_NOUN);
    }

    public int countSurnames() {
        return countRows(TABLE_SPANISH_SURNAME);
    }

    public String selectSurname(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_SURNAME + " WHERE " + TABLE_SPANISH_SURNAME + ID_PREFIX + "=" + id);
    }

    public int countOccupations() {
        return countRows(TABLE_SPANISH_OCCUPATION);
    }

    public String selectOccupation(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_OCCUPATION + " WHERE " + TABLE_SPANISH_OCCUPATION + ID_PREFIX + "=" + id);
    }

    public int countPhrases() {
        return countRows(TABLE_SPANISH_PHRASE);
    }

    public String selectPhrase(int id) {
        return selectRow("SELECT * FROM " + TABLE_SPANISH_PHRASE + " WHERE " + TABLE_SPANISH_PHRASE + ID_PREFIX + "=" + id);
    }

    public int countFamilyNames() {
        return countRows(TABLE_FAMILY_NAME);
    }

    public String selectFamilyName(int id) {
        return selectRow("SELECT * FROM " + TABLE_FAMILY_NAME + " WHERE " + TABLE_FAMILY_NAME + ID_PREFIX + "=" + id);
    }

    public int countNames() {
        return countRows(TABLE_NAME);
    }

    public String selectName(int id) {
        return selectRow("SELECT * FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + ID_PREFIX + "=" + id);
    }

    public int countCommonNouns() {
        return countRows(TABLE_NOUN);
    }

    public String selectCommonNoun(int id) {
        return selectRow("SELECT * FROM " + TABLE_NOUN + " WHERE " + TABLE_NOUN + ID_PREFIX + "=" + id);
    }

    public int countUsernames() {
        return countRows(TABLE_USERNAME);
    }

    public String selectUsername(int id) {
        return selectRow("SELECT * FROM " + TABLE_USERNAME + " WHERE " + TABLE_USERNAME + ID_PREFIX + "=" + id);
    }
}
