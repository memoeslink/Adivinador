package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnglishTextFilter implements TextFilter {
    public static final String CENSOR_REGEX = "\b" + "ass(es)?|arses?|booty|assh[o0]les?|anus(es)?|anal|arseholes?|" +
            "tits?|boobs?|breasts?|busty|(tit|breast)(job|fuck)(er)?s?|titt(y|ies)|bazongas?|nipples?|" +
            "shits?|craps?|golden showers?|(bull|horse|dog)shit|turds?|fae(x|ces)|" +
            "fag(got)?s?|dykes?|sodomites?|lesbos?|trann(y|ies)|" +
            "niggers?|niggas?|wetbacks?|beaners?|nazis?|crackers?|" +
            "fucks?|fuckers?|fucking|fucked|fornicate|fornicators?|fornications?|" +
            "sex|sexual|copulate|copulations?|orgasms?|genitalia|" +
            "dickheads?|suckers?|scum|scumbags?|dumbfucks?|dumbass(es)?|bitch(es)?|bastards?|bollocks?|cunts?|goddamn(ed)?|kike|pricks?|twats?|wankers?|buggers?|" +
            "dicks?|cocks?|penis(es)?|jizz(es)?|semen|cums?|ejaculations?|ejaculatory|erections?|testicles?|testicular|boners?|choads?|scrot(ums?|a)|cockfoam|dild[o0]s?|" +
            "vaginas?|vaginal|puss(y|ies)|" +
            "blowjobs?|(dick|cock)(lick|suck|eat)(ers?|ing)|fellatio|fellations?|" +
            "sluts?|hookers?|prostitutes?|whores?|" +
            "kys|" +
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
