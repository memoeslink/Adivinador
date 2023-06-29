package com.app.memoeslink.adivinador.preference;

import com.app.memoeslink.adivinador.R;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.Person;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import org.memoeslink.DateTimeHelper;
import org.memoeslink.StringHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferenceUtils {
    private static List<Person> people;

    static {
        validateStoredPeople();
    }

    private PreferenceUtils() {
    }

    public static String getBirthdate() {
        LocalDate date = DateTimeHelper.getCurrentDate();
        return String.format("%04d-%02d-%02d",
                PreferenceHandler.getInt(Preference.TEMP_YEAR_OF_BIRTH, date.getYear()),
                PreferenceHandler.getInt(Preference.TEMP_MONTH_OF_BIRTH, date.getMonthValue()),
                PreferenceHandler.getInt(Preference.TEMP_DAY_OF_BIRTH, date.getDayOfMonth())
        );
    }

    public static String getEnquiryDate() {
        LocalDate date = DateTimeHelper.getCurrentDate();
        return String.format("%04d-%02d-%02d",
                PreferenceHandler.getInt(Preference.TEMP_YEAR_OF_ENQUIRY, date.getYear()),
                PreferenceHandler.getInt(Preference.TEMP_MONTH_OF_ENQUIRY, date.getMonthValue()),
                PreferenceHandler.getInt(Preference.TEMP_DAY_OF_ENQUIRY, date.getDayOfMonth())
        );
    }

    public static boolean isEnquiryFormEntered() {
        Object[] fields = new Object[]{
                PreferenceHandler.getStringOrNull(Preference.TEMP_NAME),
                PreferenceHandler.getIntOrNull(Preference.TEMP_GENDER),
                PreferenceHandler.getIntOrNull(Preference.TEMP_YEAR_OF_BIRTH),
                PreferenceHandler.getIntOrNull(Preference.TEMP_MONTH_OF_BIRTH),
                PreferenceHandler.getIntOrNull(Preference.TEMP_DAY_OF_BIRTH)
        };

        for (Object field : fields) {
            if (field == null)
                return false;
        }
        return true;
    }

    public static void deleteTemp() {
        PreferenceHandler.remove(Preference.TEMP_YEAR_OF_ENQUIRY);
        PreferenceHandler.remove(Preference.TEMP_MONTH_OF_ENQUIRY);
        PreferenceHandler.remove(Preference.TEMP_DAY_OF_ENQUIRY);
        PreferenceHandler.remove(Preference.TEMP_BUSY);
        PreferenceHandler.remove(Preference.TEMP_CHANGE_FORTUNE_TELLER);
        PreferenceHandler.remove(Preference.TEMP_RESTART_ACTIVITY);
        PreferenceHandler.remove(Preference.TEMP_RESTART_ADS);

        if (!PreferenceHandler.getBoolean(Preference.SETTING_KEEP_FORM)) clearForm();
    }

    public static void clearForm() {
        PreferenceHandler.remove(Preference.TEMP_NAME);
        PreferenceHandler.remove(Preference.TEMP_GENDER);
        PreferenceHandler.remove(Preference.TEMP_YEAR_OF_BIRTH);
        PreferenceHandler.remove(Preference.TEMP_MONTH_OF_BIRTH);
        PreferenceHandler.remove(Preference.TEMP_DAY_OF_BIRTH);
        PreferenceHandler.remove(Preference.TEMP_ANONYMOUS);
    }

    public static Person getFormPerson() {
        Person person = new Person.PersonBuilder()
                .setGender(Gender.get(PreferenceHandler.getInt(Preference.TEMP_GENDER)))
                .setBirthdate(LocalDate.of(
                        PreferenceHandler.getInt(Preference.TEMP_YEAR_OF_BIRTH, 1900),
                        PreferenceHandler.getInt(Preference.TEMP_MONTH_OF_BIRTH, 1),
                        PreferenceHandler.getInt(Preference.TEMP_DAY_OF_BIRTH, 1)
                ))
                .setAttribute("requested")
                .build();

        if (PreferenceHandler.getBoolean(Preference.TEMP_ANONYMOUS)) {
            person.setUsername(PreferenceHandler.getString(Preference.TEMP_NAME));
            person.addAttribute("anonymous");
        } else
            person.setFullName(PreferenceHandler.getString(Preference.TEMP_NAME));
        return person;
    }

    public static void saveFormPerson(Person person) {
        if (person == null)
            return;
        PreferenceHandler.put(Preference.TEMP_NAME, person.getDescriptor());

        if (person.getGender() != null)
            PreferenceHandler.put(Preference.TEMP_GENDER, person.getGender().getValue());

        if (person.getBirthdate() != null) {
            PreferenceHandler.put(Preference.TEMP_YEAR_OF_BIRTH, person.getBirthdate().getYear());
            PreferenceHandler.put(Preference.TEMP_MONTH_OF_BIRTH, person.getBirthdate().getMonthValue());
            PreferenceHandler.put(Preference.TEMP_DAY_OF_BIRTH, person.getBirthdate().getDayOfMonth());
        }
        PreferenceHandler.put(Preference.TEMP_ANONYMOUS, person.hasAttribute("anonymous"));
    }

    public static boolean hasPersonTempStored() {
        return PreferenceHandler.has(Preference.TEMP_NAME)
                && PreferenceHandler.has(Preference.TEMP_GENDER)
                && PreferenceHandler.has(Preference.TEMP_YEAR_OF_BIRTH)
                && PreferenceHandler.has(Preference.TEMP_MONTH_OF_BIRTH)
                && PreferenceHandler.has(Preference.TEMP_DAY_OF_BIRTH);
    }

    public static boolean isPersonTempStored(Person person) {
        if (person == null)
            return false;
        Person formPerson = getFormPerson();
        return person.getSummary().equals(formPerson.getSummary());
    }

    public static boolean isPersonStored(Person person) {
        List<Person> people = getStoredPeople();

        if (person == null)
            return false;

        for (Person existingPerson : people) {
            if (existingPerson != null && person.getSummary().equals(existingPerson.getSummary()))
                return true;
        }
        return false;
    }

    public static void validateStoredPeople() {
        boolean valid = false;

        validation:
        {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, deserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString())).create();
            Type type = new TypeToken<ArrayList<Person>>() {
            }.getType();
            String json = PreferenceHandler.getString(Preference.DATA_STORED_PEOPLE);

            if (StringHelper.isNullOrBlank(json))
                break validation;

            try {
                JsonParser parser = new ObjectMapper().getFactory().createParser(json);

                while (parser.nextToken() != null) {
                }
            } catch (IOException e) {
                e.printStackTrace();
                break validation;
            }

            try {
                JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
                JsonSchema schema = factory.getSchema(PreferenceHandler.getContext().getResources().openRawResource(R.raw.schema));
                JsonNode node = new ObjectMapper().readTree(json);
                Set<ValidationMessage> errors = schema.validate(node);

                if (errors.size() > 1)
                    break validation;
            } catch (Exception e) {
                e.printStackTrace();
                break validation;
            }

            try {
                gson.fromJson(json, type);
            } catch (Exception e) {
                e.printStackTrace();
                break validation;
            }
            valid = true;
        }

        if (!valid)
            PreferenceHandler.remove(Preference.DATA_STORED_PEOPLE);
    }

    public static List<Person> getStoredPeople() {
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, deserializationContext) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString())).create();
        Type type = new TypeToken<ArrayList<Person>>() {
        }.getType();
        String json = PreferenceHandler.getString(Preference.DATA_STORED_PEOPLE);

        if (StringHelper.isNullOrBlank(json))
            return new ArrayList<>();
        return gson.fromJson(json, type);
    }

    public static boolean savePerson(Person person) {
        List<Person> people = getStoredPeople();

        if (PreferenceHandler.getBoolean(Preference.SETTING_SAVE_ENQUIRIES) && !isPersonStored(person)) {
            person.setDescription("");
            person.setInterpretation("");

            if (people.size() >= 100)
                people.remove(0);
            people.add(person);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, serializationContext) -> {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
            }).create();
            String json = gson.toJson(people);
            PreferenceHandler.put(Preference.DATA_STORED_PEOPLE, json);
            return true;
        }
        return false;
    }

    public static List<String> getStoredNames() {
        return new ArrayList<>(PreferenceHandler.getStringSet(Preference.DATA_STORED_NAMES));
    }

    public static boolean saveName(String name) {
        if (StringHelper.isNullOrBlank(name)) return false;
        List<String> storedNames = getStoredNames();

        if (PreferenceHandler.getBoolean(Preference.SETTING_SAVE_NAMES, true) && !storedNames.contains(name)) {
            if (storedNames.size() >= 200) storedNames.remove(0);
            storedNames.add(name);
            Set<String> set = new HashSet<>(storedNames);
            return PreferenceHandler.put(Preference.DATA_STORED_NAMES, set);
        }
        return false;
    }
}
