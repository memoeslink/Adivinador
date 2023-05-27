package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanishTextFilter implements TextFilter {
    public static final String CENSOR_REGEX = "(?<start>^|\\s+|[^\\p{IsLatin}])(?<text>" + "sexo|sexual(es)?|coitos?|(coital(es)?)|fornicador([ea]s)?|fornicación|fornicatori[oa]s?|orgasmos?|orgásmic[oa]s?|genital(es)?|copular|cópular|copulatori[oa]s?|follar|follatori[oa]s?|follador(es|as?)?|foll(ón|ones|onas?)|follaculos?|" +
            "t[e3]t[a4]s?|pechos?|senos?|tetamen|tet(ón|ones|onas?)|tetudas?|pez(ón|ones)|tet(ita|ota)s?|pech(ito|ote)s|chich(ita|ota)s?|chich(ón|ones|onas?)|mamar|mamas?|mamari[oa]s?|" +
            "vaginas?|vaginal(es)?|clítoris|conchud[oa]s?|coños?|coñazos?|panochas?|panoch(ita|ota)s?|panochud[oa]s?|panoch(ón|ones|onas?)|pubis|púbic[oa]s?|" +
            "p[e3]n[e3]s?|vergas?|verg(uita|ota)s?|vergud[oa]s?|verg(ón|ones)|verguer[oa]s?|vergazos?|pijas?|pijud[oa]s?|testículos?|escrotos?|próstatas?|prostátic[oa]s?|erecci(ón|ones)|pijud[0a]s?|dildos?|prepucios?|esmegma|" +
            "nalgas?|nalgonas?|nalg(uita|ota)s?|cul[o0]s?|cul(ón|ones|onas?)|cul(ito|ote)s?|anos?|anal(es)?|rectal(es)?|pedorr[oa]s|pedorrot[oe]s?|" +
            "mierdas?|mierder[oa]s?|mierd(ón|ones|onas?)|cagadas?|cag(ón|ones|onas?)|cacas?|comemierdas?|he(z|ces)|moj(ón|ones)" +
            "semen|eyaculaci(ón|ones)|eyaculatori[oa]s?|masturbaci(ón|ones)|masturbatori[oa]s?|onanismos?|puñetas?|puñeter[oa]s?|chaqueter[oa]s?|pajer[oa]s?|" +
            "malnacid[oa]s?|bastard[oa]s?|pendej[oa]s?|putear|put[oa]s?|puterías?|puteros?|putadas?|putazos?|cabr(ón|onas?|ones)|cabronaz[oa]s?|chingad[oa]s?|jodid[oa]s?|joder|jodederas?|" +
            "(lame|chupa|mama|muerde)(culo|pija|huevo|bola|verga|polla|pene|escroto|prepucio)s?|carevergas?|caraculos?|" +
            "culer[oa]s?|chingar|chingaderas?|chingader(ita|ota)s?|chingonerías?|ching(ón|ones|onas?)|emputad[oa]s?|encabronad[oa]s?|güey(es)?|mam(ón|ones|onas?)|mamadas?|putizas?|pendejear|mamader[oa]s?|put(ón|ones|onas?)|" +
            "maric(as?|ón|onas?|ones)|mariquitas?|trolazos?|jotos?|jotol(ón|ones)|jotaz[oa]s?|jototes?|joterías?|amariconad[oa]s?|muerdealmohadas?|mariconaz[oa]s?|jotísim[oa]s?|" +
            "prostitut[oa]s?|sexoservidor(es|as?)?|rameras?|" +
            "pornografía|pornográfic[oa]s?|p[o0]rn[o0]s?|z[o0]{2}filia|zoofílic[oa]s?|bestialismo|pedofilia|(sado)?masoquismo|(sado)?masoquistas?|" +
            //"|" +
            //"|" +
            ")(?<end>\\s+|[^\\p{IsLatin}]|$)";
    public static final Pattern CENSOR_PATTERN = Pattern.compile(CENSOR_REGEX, Pattern.CASE_INSENSITIVE);

    @Override
    public String censor(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;
        Matcher matcher = CENSOR_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = matcher.group("start") + "*".repeat(matcher.group("text").length()) + matcher.group("end");
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
