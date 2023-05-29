package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanishTextFilter implements TextFilter {
    public static final String COMMON_PROFANITY_REGEX = "(" + "malnacid[oa]s?|bastard[oa]s?|pendej[oa]s?|putear|puterías?|puteros?|putadas?|putazos?|cabr(ón|onas?|ones)|cabronaz[oa]s?|chingad[oa]s?|jodid[oa]s?|joder|jodederas?|(lame|chupa|mama|muerde)(culo|pija|huevo|bola|verga|polla|pene|escroto|prepucio|nalga|chota)s?|carevergas?|caraculos?|culer[oa]s?|chingar|chingaderas?|chingader(ita|ota)s?|chingonerías?|ching(ón|ones|onas?)|emputad[oa]s?|encabronad[oa]s?|güey(es)?|mam(ón|ones|onas?)|putizas?|pendejear|mamader[oa]s?" + ")";
    public static final String SEXUAL_INTERCOURSE_PROFANITY_REGEX = "(" + "sexo|sexual(es)?|coitos?|(coital(es)?)|fornicador([ea]s)?|fornicación|fornicatori[oa]s?|orgasmos?|orgásmic[oa]s?|genital(es)?|copular|cópular|copulatori[oa]s?|follar|follatori[oa]s?|follador(es|as?)?|foll(ón|ones|onas?)|follaculos?|orgías?|mamar|mamadas?" + ")";
    public static final String BODY_PROFANITY_REGEX = "(" + "nalgas?|nalgonas?|nalg(uita|ota)s?|cul[o0]s?|cul(ón|ones|onas?)|cul(ito|ote)s?|anos?|anal(es)?|rectal(es)?|pedorr[oa]s|pedorrot[oe]s?|pubis|púbic[oa]s?|entrepiernas?" + ")";
    public static final String BREASTS_PROFANITY_REGEX = "(" + "t[e3]t[a4]s?|pechos?|senos?|tetamen|tet(ón|ones|onas?)|tetudas?|pez(ón|ones)|tet(ita|ota)s?|pech(ito|ote)s|chich(ita|ota)s?|chich(ón|ones|onas?)|mamas?|mamari[oa]s?" + ")";
    public static final String FEMALE_GENITALIA_PROFANITY_REGEX = "(" + "vaginas?|vaginal(es)?|clítoris|conchud[oa]s?|coños?|coñazos?|panochas?|panoch(ita|ota)s?|panochud[oa]s?|panoch(ón|ones|onas?)" + ")";
    public static final String MALE_GENITALIA_PROFANITY_REGEX = "(" + "p[e3]n[e3]s?|vergas?|verg(uita|ota)s?|vergud[oa]s?|verg(ón|ones)|verguer[oa]s?|vergazos?|pijas?|pijud[oa]s?|testículos?|escrotos?|próstatas?|prostátic[oa]s?|pijud[0a]s?|prepucios?|esmegma|chotas?" + ")";
    public static final String FECAL_MATTER_PROFANITY_REGEX = "(" + "mierdas?|mierder[oa]s?|mierd(ón|ones|onas?)|cagadas?|cag(ón|ones|onas?)|cacas?|comemierdas?|he(z|ces)|moj(ón|ones)|soretes?" + ")";
    public static final String MASTURBATION_PROFANITY_REGEX = "(" + "masturbaci(ón|ones)|masturbatori[oa]s?|onanismos?|puñetas?|puñeter[oa]s?|chaqueter[oa]s?|pajer[oa]s?|semen|eyacular|eyaculaci(ón|ones)|eyaculatori[oa]s?|erecci(ón|ones)" + ")";
    public static final String HOMOSEXUALITY_PROFANITY_REGEX = "(" + "put[oa]s?|put(ón|ones|onas?)|maric(as?|ón|onas?|ones)|mariquitas?|trolazos?|jotos?|jotol(ón|ones)|jotaz[oa]s?|jototes?|joterías?|amariconad[oa]s?|muerdealmohadas?|mariconaz[oa]s?|jotísim[oa]s?|sodomitas?|transexual(es)?" + ")";
    public static final String PROSTITUTION_PROFANITY_REGEX = "(" + "prostitut[oa]s?|sexoservidor(es|as?)?|rameras?" + ")";
    public static final String PORNOGRAPHY_SLURS_REGEX = "(" + "pornografía|pornográfic[oa]s?|p[o0]rn[o0]s?|bukkake|dildos?" + ")";
    public static final String PARAPHILIA_SLURS_REGEX = "(" + "z[o0]{2}filia|zoofílic[oa]s?|bestialismo|pedófilos?|pedofilia|(sado)?masoquismo|(sado)?masoquistas?|sodomías?" + ")";
    public static final String RACIST_SLURS_REGEX = "(" + "sudacas?|prietos?" + ")";
    public static final String INTERNET_SLURS_REGEX = "(" + "n[o0]{2}b" + ")";
    public static final String VIOLENCE_SLURS_REGEX = "(" + "mátate|suicídate" + ")";
    public static final String CENSOR_REGEX = "(?<start>^|\\s+|[^\\p{IsLatin}])(?<text>" +
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
