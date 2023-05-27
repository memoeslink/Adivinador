package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnglishTextFilter implements TextFilter {
    public static final String CENSOR_REGEX = "\b" + "ass(es)?|arses?|booty|assh[o0]les?|anus(es)?|anal|arseholes?|rect(ums?|a)|rectal|anilingus|" +
            "tits?|boobs?|breasts?|bosoms?|busty|(tit|breast|ass)(job|fuck)(ers?|ing)?|titt(y|ies)|bazongas?|nipples?|mammar(y|ies)|tittywanks?|titwanks?|tittiefuckers?|" +
            "sh[i1!]ts?|craps?|golden showers?|(bull|horse|dog)shit|turds?|fae(x|ces)|" +
            "fag(got)?s?|dykes?|sodomites?|lesbos?|trann(y|ies)|siss(y|ies)|cuntboys?|" +
            "niggers?|niggas?|wetbacks?|beaners?|nazis?|crackers?|" +
            "fuck[a-zA-Z]+|fucks?|fuckers?|fucking|fucked|fornicate|fornicators?|fornications?|" +
            "sex|sexual|copulate|copulations?|orgasms?|genitalia|crotch(es)|" +
            "dickheads?|suckers?|scum|scumbags?|dumbfucks?|dumbass(es)?|bitch(es)?|bastards?|bollocks?|goddamn(ed)?|kike|pricks?|twats?|wankers?|buggers?|morons?|retards?|shitheads?|motherfuckers?|motherfuckkas?|motherfucking|motherfuck|" +
            "dicks?|c[o0]cks?|penis(es)?|jizz(es)?|semen|cums?|ejaculations?|ejaculatory|erections?|testicles?|testicular|boners?|choads?|scrot(ums?|a)|cockfoam|dild[o0]s?|ballsacks?|smegma|bukkake|" +
            "vaginas?|vaginal|vag|puss(y|ies)|clitoris(es)?|cunts?|pubes?|pubic|" +
            "blowjobs?|(dick|cock)(lick|suck|eat)(ers?|ing)|fellatio|fellations?|masturbate|masturbations?|masturbatory|masterbate|" +
            "sluts?|hookers?|prostitutes?|whores?|" +
            "kys|" +
            "pornography|pornographic|p[o0]rn|p[o0]rn[o0]s?|z[o0]{2}philiac?|bestiality|cp|(sado)?masochism|(sado)?masochists?|(sado)?masochistic|sodom(y|ies)|" +
            //"|" +
            //"|" +
            "\b";
    public static final Pattern CENSOR_PATTERN = Pattern.compile(CENSOR_REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    public String censor(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;
        Matcher matcher = CENSOR_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = "*".repeat(matcher.group().length());
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
