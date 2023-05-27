package com.app.memoeslink.adivinador.textfilter;

import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanishTextFilter implements TextFilter {
    public static final String CENSOR_REGEX = "(^|\\s+|\\-)" + "sexo|sexual(es)?|coitos?|(coital(es)?)|fornicador([ea]s)?|fornicación|fornicatori[oa]s?|orgasmos?|orgásmic[oa]s?|genital(es)?|copular|cópular|copulatori[oa]s?|follar|follador(es|as?)?|foll(ón|ones|onas?)|follaculos?| " +
            "tetonas?|tetas?|pechos?|senos?|tetamen|tetudas?|pez(ón|ones)|tetotas?|chichotas?|chichonas?|" +
            "penes?|vergas?|vergudos?|verg(ón|ones)|vergazos?|testículos?|escrotos?|próstatas?|prostátic[oa]s?|erecci(ón|ones)|pijud[0a]s?|dildos?|" +
            "nalgas?|nalgonas?|nalg(uita|ota)s?|culos?|cul(ón|ones|onas?)|cul(ito|ote)s?|anos?|anal(es)?|rectal(es)?|" +
            "mierdas?|mierder[oa]s?|mierd(ón|ones|onas?)|cagadas?|cag(ón|ones|onas?)|cacas?|comemierdas?|he(z|ces)|" +
            "semen|eyaculaci(ón|ones)|eyaculatori[oa]s?|masturbaci(ón|ones)|onanismos?|puñetas?|puñeter[oa]s?|chaqueter[oa]s?|pajer[oa]s?|" +
            "malnacid[oa]s?|bastard[oa]s?|pendej[oa]s?|putear|put[oa]s?|puterías?|puteros?|putadas?|cabr(ón|onas?|ones)|cabronaz[oa]s?|maric(as?|ón|onas?|ones)|mariquitas?|jotos?|trolazos?|" +
            "(lame|chupa|mama)(culo|pija|huevo|bola|verga|polla|pene|escroto)s?|pijas?|pijud[oa]s?|carevergas?|caraculos?|" +
            "culer[oa]s?|chingar|chingaderas?|chingader(ita|ota)s?|chingonerías?|ching(ón|ones|onas?)|emputad[oa]s?|encabronad[oa]s?|güey(es)?|mam(ón|ones|onas?)|mamadas?|mamar|putizas?|pendejear|mamader[oa]s?|" +
            "amariconad[oa]s?|muerdealmohadas?|mariconaz[oa]s?|" +
            "prostitut[oa]s?|sexoservidor(es|as?)?|rameras?|meretri(z|ces)|" +
            //"|" +
            //"|" +
            "(\\-|\\s+|$)";
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
