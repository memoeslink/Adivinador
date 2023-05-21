package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;

import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.Maker;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.TextProcessor;

import java.time.Period;
import java.util.HashMap;

public class Identity extends ContextWrapper {
    private final Person person;

    public Identity(Context context) {
        this(context, new Person.PersonBuilder().build());
    }

    public Identity(Context context, Person person) {
        super(context);
        this.person = person;
    }

    public HashMap<String, String> getInformation() {
        HashMap<String, String> information = new HashMap<>();
        String uniqueSeed = person.getSha256();

        information.put("zodiacSign", getZodiacSignName());
        information.put("astrologicalHouse", getAstrologicalHouse());
        information.put("ruler", getRuler());
        information.put("element", getElement());
        information.put("signColor", getSignColor());
        information.put("signNumbers", getSignNumbers());
        information.put("compatibility", getCompatibility());
        information.put("incompatibility", getIncompatibility());
        information.put("walterBergZodiacSign", getWalterBergZodiacSignName());
        information.put("chineseZodiacSign", getChineseZodiacSignName());
        information.put("animal", getAnimal());
        information.put("psychologicalType", getPsychologicalType());
        information.put("secretName", getSecretName());
        information.put("demonicName", getDemonicName());
        information.put("previousName", getPreviousName());
        information.put("futureName", getFutureName());
        information.put("recommendedUsername", getRecommendedUsername());
        information.put("uniqueColor", Maker.getColor(uniqueSeed));
        information.put("daysBetweenDates", getDaysBetweenDates(DateTimeHelper.getStrCurrentDate()));
        information.put("timeBetweenDates", getTimeBetweenDates(DateTimeHelper.getStrCurrentDate()));
        return information;
    }

    public ZodiacSign getZodiacSign() {
        return ZodiacSign.get(person.getBirthdate().getMonthValue(), person.getBirthdate().getDayOfMonth());
    }

    public WalterBergZodiacSign getWalterBergZodiacSign() {
        return WalterBergZodiacSign.get(person.getBirthdate().getMonthValue(), person.getBirthdate().getDayOfMonth());
    }

    public ChineseZodiacSign getChineseZodiacSign() {
        return ChineseZodiacSign.get(person.getBirthdate().getYear());
    }

    public String getZodiacSignName() {
        return getZodiacSign().getName(getBaseContext()) + " " + getZodiacSign().getEmoji();
    }

    public String getAstrologicalHouse() {
        return getZodiacSign().getAstrologicalHouse(getBaseContext());
    }

    public String getRuler() {
        return getZodiacSign().getRuler(getBaseContext());
    }

    public String getElement() {
        return getZodiacSign().getElement(getBaseContext());
    }

    public String getSignColor() {
        return String.format("<font color=\"%s\">%s</font>", getZodiacSign().getHexColor(), TextFormatter.formatText(getZodiacSign().getColor(getBaseContext()), "u"));
    }

    public String getSignNumbers() {
        return getZodiacSign().getNumbers(getBaseContext());
    }

    public String getCompatibility() {
        return getZodiacSign().getCompatibility(getBaseContext());
    }

    public String getIncompatibility() {
        return getZodiacSign().getIncompatibility(getBaseContext());
    }

    public String getWalterBergZodiacSignName() {
        return getWalterBergZodiacSign().getName(getBaseContext()) + " " + getWalterBergZodiacSign().getEmoji();
    }

    public String getChineseZodiacSignName() {
        return getChineseZodiacSign().getName(getBaseContext());
    }

    public String getAnimal() {
        return StringHelper.capitalizeFirst(getResourceExplorer().getResourceFinder().getStrFromStrArrayRes(R.array.animal));
    }

    public String getPsychologicalType() {
        return getResourceExplorer().getResourceFinder().getStrFromStrArrayRes(R.array.psychological_type);
    }

    public String getSecretName() {
        return TextFormatter.formatName(getResourceExplorer().getGeneratorManager().getNameGenerator().getName(NameType.SECRET_NAME));
    }

    public String getDemonicName() {
        return TextFormatter.formatName(TextProcessor.demonize(person.getDescriptor(), getResourceExplorer().getGeneratorManager().getNameGenerator().getName(NameType.SECRET_NAME)));
    }

    public String getPreviousName() {
        Person pastPerson = person;
        pastPerson.setBirthdate(pastPerson.getBirthdate().minusDays(1));
        long seed = LongHelper.getSeed(StringHelper.sha256(pastPerson.getSha256()));
        ResourceExplorer resourceExplorer = new ResourceExplorer(getBaseContext(), seed);
        return TextFormatter.formatName(resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson());
    }

    public String getFutureName() {
        Person futurePerson = person;
        futurePerson.setBirthdate(futurePerson.getBirthdate().plusDays(1));
        long seed = LongHelper.getSeed(StringHelper.sha256(futurePerson.getSha256()));
        ResourceExplorer resourceExplorer = new ResourceExplorer(getBaseContext(), seed);
        return TextFormatter.formatName(resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson());
    }

    public String getRecommendedUsername() {
        return TextFormatter.formatUsername(getResourceExplorer().getGeneratorManager().getNameGenerator().getUsername());
    }

    public String getDaysBetweenDates(String enquiryDate) {
        String birthdate = DateTimeHelper.toIso8601Date(person.getBirthdate());
        return String.valueOf(DateTimeHelper.getDifferenceInDays(birthdate, enquiryDate));
    }

    public String getTimeBetweenDates(String enquiryDate) {
        String birthdate = DateTimeHelper.toIso8601Date(person.getBirthdate());
        Period period = DateTimeHelper.getTimeBetweenDates(birthdate, enquiryDate);
        return getString(R.string.time_elapsed, period.getYears(), period.getMonths(), period.getDays());
    }

    private ResourceExplorer getResourceExplorer() {
        long seed = LongHelper.getSeed(StringHelper.sha256(person.getSha256()));
        return new ResourceExplorer(getBaseContext(), seed);
    }
}
