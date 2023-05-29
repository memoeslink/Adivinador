package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnglishTextFilter implements TextFilter {
    public static final String COMMON_PROFANITY_REGEX = "(" + "dickheads?|suckers?|scum|scumbags?|dumbfucks?|dumbass(es)?|bitch(es)?|bastards?|bollocks?|goddamn(ed)?|kike|pricks?|twats?|wankers?|buggers?|morons?|retards?|shitheads?|motherfuckers?|motherfuckkas?|motherfucking|motherfuck" + ")";
    public static final String SEXUAL_INTERCOURSE_PROFANITY_REGEX = "(" + "fuck[a-zA-Z]+|fuckers?|fucking|fucked|fornicate|fornicators?|fornications?|sex|sexual|copulate|copulations?|orgasms?|genitalia|org(y|ies)|blowjobs?|(dick|cock)(lick|suck|eat)(ers?|ing)|fellatio|fellations?" + ")";
    public static final String BODY_PROFANITY_REGEX = "(" + "ass(es)?|arses?|booty|assh[o0]les?|anus(es)?|anal|arseholes?|rect(ums?|a)|rectal|anilingus|pubes?|pubic|crotch(es)" + ")";
    public static final String BREASTS_PROFANITY_REGEX = "(" + "tits?|boobs?|breasts?|bosoms?|busty|titt(y|ies)|bazongas?|nipples?|mammar(y|ies)" + ")";
    public static final String FEMALE_GENITALIA_PROFANITY_REGEX = "(" + "vaginas?|vaginal|vag|puss(y|ies)|clitoris(es)?|cunts?" + ")";
    public static final String MALE_GENITALIA_PROFANITY_REGEX = "(" + "dicks?|c[o0]cks?|penis(es)?|testicles?|testicular|boners?|choads?|scrot(ums?|a)|cockfoam|ballsacks?|smegma" + ")";
    public static final String FECAL_MATTER_PROFANITY_REGEX = "(" + "sh[i1!]ts?|craps?|golden showers?|(bull|horse|dog)shit|turds?|fa?eces" + ")";
    public static final String MASTURBATION_PROFANITY_REGEX = "(" + "masturbate|masturbations?|masturbatory|masterbate|semen|cums?|ejaculations?|jizz(es)?|ejaculatory|erections?" + ")";
    public static final String HOMOSEXUALITY_PROFANITY_REGEX = "(" + "fag(got)?s?|dykes?|sodomites?|lesbos?|trann(y|ies)|siss(y|ies)|cuntboys?|transexuals?" + ")";
    public static final String PROSTITUTION_PROFANITY_REGEX = "(" + "sluts?|hookers?|prostitutes?|whores?" + ")";
    public static final String PORNOGRAPHY_SLURS_REGEX = "(" + "pornography|pornographic|p[o0]rn|p[o0]rn[o0]s?|(tit|breast|ass)(job|fuck)(ers?|ing)?|tittywanks?|titwanks?|tittiefuckers?|sodom(y|ies)|bukkakes?|dild[o0]s?" + ")";
    public static final String PARAPHILIA_SLURS_REGEX = "(" + "z[o0]{2}philiac?|bestiality|pedophiles?|pedophiliac|ped[o0](es)?|cp|(sado)?masochism|(sado)?masochists?|(sado)?masochistic" + ")";
    public static final String RACIST_SLURS_REGEX = "(" + "niggers?|niggas?|wetbacks?|beaners?|nazis?|crackers?" + ")";
    public static final String INTERNET_SLURS_REGEX = "(" + "n[o0]{2}b" + ")";
    public static final String VIOLENCE_SLURS_REGEX = "(" + "kys" + ")";
    public static final String CENSOR_REGEX = "\b" +
            COMMON_PROFANITY_REGEX +
            "|" + SEXUAL_INTERCOURSE_PROFANITY_REGEX +
            "|" + BODY_PROFANITY_REGEX +
            "|" + BREASTS_PROFANITY_REGEX +
            "|" + FEMALE_GENITALIA_PROFANITY_REGEX +
            "|" + MALE_GENITALIA_PROFANITY_REGEX +
            "|" + FECAL_MATTER_PROFANITY_REGEX +
            "|" + MASTURBATION_PROFANITY_REGEX +
            "|" + HOMOSEXUALITY_PROFANITY_REGEX +
            "|" + PROSTITUTION_PROFANITY_REGEX +
            "|" + PORNOGRAPHY_SLURS_REGEX +
            "|" + PARAPHILIA_SLURS_REGEX +
            "|" + RACIST_SLURS_REGEX +
            "|" + INTERNET_SLURS_REGEX +
            "|" + VIOLENCE_SLURS_REGEX +
            //"|" + "" +
            //"|" + "" +
            "\b";
    public static final Pattern CENSOR_PATTERN = Pattern.compile(CENSOR_REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    public String censor(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;
        Matcher matcher = CENSOR_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = StringHelper.mask(matcher.group());
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
