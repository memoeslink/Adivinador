package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanishTextFilter implements TextFilter {
    public static final String COMMON_PROFANITY_REGEX = "(" + "malnacid[oa]s?|bastard[oa]s?|putear|puterías?|puteros?|putadas?|putazos?|cabr(ón|onas?|ones)|cabronaz[oa]s?|chingar|ching(ón|ones|onas?)|chingader(a|ita|ota)s?|chingonerías?|chingad[oa]s?|chingaqueditos?|joder|jodid([oa]|it[oa]|ot[ea])s?|jodederas?|(chupa|lame|mama|muerde)(bola|chota|culo|escroto|huevo|nalga|pene|pija|polla|prepucio|verga)s?|carevergas?|caraculos?|culer([oa]|it[oa]|ot[ea])s?|emputad[oa]s?|encabronad[oa]s?|mam(ón|ones|onas?)|mamader[oa]s?|pendejear|pendej([oa]|it[oa]|ot[ea])s?|putizas?|pedorr[oa]s|güey(es)?|imbécil(es)?" + ")";
    public static final String INTERNET_PROFANITY_REGEX = "(" + "n[o0]{2}b" + ")";
    public static final String VIOLENCE_PROFANITY_REGEX = "(" + "mátate|suicídate" + ")";
    public static final String BODY_PROFANITY_REGEX = "(" + "cul([o0]|ito|ote|azo)s?|cul(ón|ones|onas?)|nalg(a|uita|ota)s?|nalgonas?|pedorr(o|ito|ote|azo)s?|pedorr(ón|ones|onas?)|an(o|ito|ote)s?|anal(es)?|rectal(es)?|entrepiernas?|pubis|púbic[oa]s?|t[e3]t([a4]|ita|ota|aza)s?|tet(ón|ones|onas?|ud[oa]s?)|tetamen|pechos?|senos?|pez(ón|ones)|tet(ita|ota)s?|pech(ito|ote)s?|chich(ita|ota)s?|chich(ón|ones|onas?)|mamas?|mamari[oa]s?" + ")";
    public static final String FEMALE_GENITALIA_PROFANITY_REGEX = "(" + "vagin(a|ita|ota|azo)s?|vaginud[oa]s?|vaginal(es)?|clítoris|conchud[oa]s?|coñ(o|ito|ote|azo)s?|panoch(a|ita|ota|azo)s?|panoch(ón|ones|onas?|ud[oa]s?)|panochud[oa]s?" + ")";
    public static final String MALE_GENITALIA_PROFANITY_REGEX = "(" + "p[e3]n[e3](cito|zote)?s?|verg(a|uita|ota)s?|verg(ón|ones|onas?|ud[oa]s?)|verguer[oa]s?|vergazos?|pij(a|ita|ota)s?|pijud[oa]s?|testículos?|escrotos?|próstatas?|prostátic[oa]s?|pijud[0a]s?|prepucios?|chotas?|esmegma" + ")";
    public static final String MASTURBATION_SLURS_REGEX = "(" + "masturbaci(ón|ones)|masturbatori[oa]s?|masturbador(es|as)?|onanismos?|puñetas?|puñeter[oa]s?|chaqueter[oa]s?|pajer[oa]s?" + ")";
    public static final String PORNOGRAPHY_SLURS_REGEX = "(" + "pornografía|pornográfic[oa]s?|p[o0]rn[o0]s?|bukkake|dildos?|futanari" + ")";
    public static final String SEXUAL_INTERCOURSE_PROFANITY_REGEX = "(" + "sexo|sexual(es)?|coitos?|(coital(es)?)|fornicador([ea]s)?|fornicación|fornicatori[oa]s?|orgasmos?|orgásmic[oa]s?|genital(es)?|copular|cópular|copulatori[oa]s?|follar|follatori[oa]s?|follador(es|as?)?|foll(ón|ones|onas?)|folla(culo|negr[oa]|perr[oa])s?|orgías?|mamar|mamad(a|ita|ota)s?|erecci(ón|ones)|semen|eyacular|eyaculaci(ón|ones)|eyaculatori[oa]s?|anilingus|prostitut[oa]s?|sexoservidor(es|as?)?|rameras?" + ")";
    public static final String FECAL_MATTER_SLURS_REGEX = "(" + "mierd(a|ita|ota|aza)s?|mierd(ón|ones|onas?)|mierder[oa]s?|cagad([oa]|it[oa]|ot[ea])s?|cag(ón|ones|onas?)|ca(ca|quita|cota)s?|comemierdas?|he(z|ces)|moj(ón|ones|on(cito|zote)s?)|soretes?" + ")";
    public static final String PARAPHILIA_SLURS_REGEX = "(" + "z[o0]{2}filia|zoofílic[oa]s?|bestialismo|pedófilos?|pedofilia|(sado)?masoquismo|(sado)?masoquistas?|sodomías?" + ")";
    public static final String HOMOPHOBIC_OR_TRANSPHOBIC_INSULTS_REGEX = "(" + "put([oa]|it[oa]|ot[ea]|az[oa])s?|put(ón|ones|onas?)|maric(as?|ón|onas?|ones|on(cito|zote)s?)|mariquitas?|trolazos?|jot(o|it[oa]|ot[ea])s?|jotol(ón|ones)|jotaz[oa]s?|jototes?|joterías?|amariconad[oa]s?|muerdealmohadas?|mariconaz[oa]s?|jotísim[oa]s?|sodomitas?|transexual(es)?" + ")";
    public static final String RACIST_OR_XENOPHOBIC_INSULTS_REGEX = "(" + "sudacas?|prietos?" + ")";
    public static final String CENSOR_REGEX = "(?<start>^|\\s+|[^\\p{IsLatin}])(?<text>" +
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
            ")(?<end>\\s+|[^\\p{IsLatin}]|$)";
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
