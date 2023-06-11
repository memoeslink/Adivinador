package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnglishTextFilter implements TextFilter {
    public static final String COMMON_PROFANITY_REGEX = "(" + "dickheads?|suckers?|scum|scumbags?|dumbfucks?|dumbass(es)?|bitch(es)?|bastards?|bollocks?|goddamn(ed)?|kike|pricks?|twats?|wankers?|buggers?|morons?|retards?|shitheads?|motherfuckers?|motherfuckkas?|motherfucking|motherfuck|(bull|horse|dog)shit" + ")";
    public static final String INTERNET_PROFANITY_REGEX = "(" + "n[o0]{2}b" + ")";
    public static final String VIOLENCE_PROFANITY_REGEX = "(" + "kys" + ")";
    public static final String BODY_PROFANITY_REGEX = "(" + "ass(es)?|arses?|booty|assh[o0]les?|anus(es)?|anal|arseholes?|rect(ums?|a)|rectal|anilingus|pubes?|pubic|crotch(es)|tits?|boobs?|breasts?|bosoms?|busty|titt(y|ies)|bazongas?|nipples?|mammar(y|ies)" + ")";
    public static final String FEMALE_GENITALIA_PROFANITY_REGEX = "(" + "vaginas?|vaginal|vag|puss(y|ies)|clitoris(es)?|cunts?" + ")";
    public static final String MALE_GENITALIA_PROFANITY_REGEX = "(" + "dicks?|c[o0]cks?|penis(es)?|testicles?|testicular|choads?|scrot(ums?|a)|ballsacks?|glan(de)?s|smegma" + ")";
    public static final String MASTURBATION_SLURS_REGEX = "(" + "masturbate|masturbations?|masturbatory|masterbate|fap|onanisms?" + ")";
    public static final String PORNOGRAPHY_SLURS_REGEX = "(" + "pornography|pornographic|p[o0]rn|p[o0]rn[o0]s?|(tit|breast|ass)(job|fuck)(ers?|ing)?|tittywanks?|titwanks?|tittiefuckers?|sodom(y|ies)|bukkakes?|dild[o0]s?|futanari|milfs?|pegging|queening" + ")";
    public static final String SEXUAL_INTERCOURSE_PROFANITY_REGEX = "(" + "fuck[a-z]*|fornicate|fornicators?|fornications?|sex|sexual|copulate|copulations?|orgasms?|genitalia|org(y|ies)|blowjobs?|(dick|cock)(lick|suck|eat)(ers?|ing)|fellatio|fellations?|erections?|semen|cums?|ejaculations?|ejaculatory|jizz(es)?|cockfoam|boners?|anilingus|sluts?|hookers?|prostitutes?|whores?" + ")";
    public static final String FECAL_MATTER_SLURS_REGEX = "(" + "sh[i1!]ts?|shites?|craps?|turds?|fa?eces|fecal" + ")";
    public static final String PARAPHILIA_SLURS_REGEX = "(" + "z[o0]{2}philiac?|bestiality|yiff|pedophiles?|pedophiliac|ped[o0](es)?|cp|(sado)?masochism|(sado)?masochists?|(sado)?masochistic" + ")";
    public static final String HOMOPHOBIC_OR_TRANSPHOBIC_INSULTS_REGEX = "(" + "fag(got)?s?|dykes?|sodomites?|lesbos?|trann(y|ies)|siss(y|ies)|cuntboys?|transexuals?|shemales?" + ")";
    public static final String RACIST_OR_XENOPHOBIC_INSULTS_REGEX = "(" + "niggers?|niggas?|wetbacks?|beaners?|nazis?|crackers?" + ")";
    public static final String CENSOR_REGEX = "(?<start>^|\\s+|\\W)(?<text>" +
            COMMON_PROFANITY_REGEX +
            "|" + INTERNET_PROFANITY_REGEX +
            "|" + VIOLENCE_PROFANITY_REGEX +
            "|" + BODY_PROFANITY_REGEX +
            "|" + FEMALE_GENITALIA_PROFANITY_REGEX +
            "|" + MALE_GENITALIA_PROFANITY_REGEX +
            "|" + MASTURBATION_SLURS_REGEX +
            "|" + PORNOGRAPHY_SLURS_REGEX +
            "|" + SEXUAL_INTERCOURSE_PROFANITY_REGEX +
            "|" + FECAL_MATTER_SLURS_REGEX +
            "|" + PARAPHILIA_SLURS_REGEX +
            "|" + HOMOPHOBIC_OR_TRANSPHOBIC_INSULTS_REGEX +
            "|" + RACIST_OR_XENOPHOBIC_INSULTS_REGEX +
            //"|" + "" +
            //"|" + "" +
            ")(?<end>\\s+|\\W|$)";
    public static final Pattern CENSOR_PATTERN = Pattern.compile(CENSOR_REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    public String censor(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;
        Matcher matcher = CENSOR_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = matcher.group("start") + StringHelper.mask(matcher.group("text")) + matcher.group("end");
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
