package com.app.memoeslink.adivinador;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.AnyRes;
import androidx.annotation.ArrayRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.app.memoeslink.adivinador.english.IndefiniteArticle;
import com.app.memoeslink.adivinador.spanish.Article;
import com.app.memoeslink.adivinador.spanish.Noun;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.encryptorcode.pluralize.Pluralize;

class Methods extends BaseWrapper {
    public static final String FULL_STOP = ".";
    public static final String ZERO_WIDTH_SPACE = "\u200B";
    public static final String[] SEPARATOR = {"-", "~", ".", "_", " "};
    public static String networkCountry = null;
    public static Locale[] locales;
    public static List<Name> availableNames = new ArrayList<>();
    public static List<Name> supportedNames = new ArrayList<>();
    private static final String ACCENTED_CHARACTERS = "àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇØøÅåÆæǼǽǣŒœÐÞαßþðŠšÝýÝÿŽž";
    private static final String HEX_DIGITS = "0123456789ABCDEF";
    private static final String FULL_LOWERCASE_VOWELS = "aàáâãäåāăąǻȁȃạảấầẩẫậắằẳẵặḁæǽeȅȇḕḗḙḛḝẹẻẽếềểễệēĕėęěèéêëiȉȋḭḯỉịĩīĭįiìíîïĳoœøǿȍȏṍṏṑṓọỏốồổỗộớờởỡợōòóŏőôõöuũūŭůűųùúûüȕȗṳṵṷṹṻụủứừửữựyẙỳỵỷỹŷÿý";
    private static final String LOWERCASE_VOWELS = "aeiou";
    private static final String LOWERCASE_CONSONANTS = "bcdfghjklmnpqrstvwxyz";
    private static final String LOWERCASE_ENDING_CONSONANTS = "lmnrstz";
    private static final String LOWERCASE_REPEATED_CONSONANTS = "bbbccccdddfffggghjjjkllllmmmmnnnnpppqrrrrsssssttttvwxyz";
    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DOUBLE_FULL_STOP_REGEX = "\\.(\\s*</?\\w+>\\s*)*\\.";
    private static final String FORMAT_REGEX = "^((a|b|big|i|s|small|tt|u),)*(a|b|big|i|s|small|tt|u)$";
    private static final String LATIN_OR_SPACE_REGEX = "[\\p{L}\\s]+";
    private static final String INTEGER_REGEX = "(-?[1-9]\\d*|0)";
    private static final String INDEFINITE_ARTICLE_REGEX = "(\\b[Aa]/an\\b)(\\s+)(\\b[\\w\\d\\?><;,\\{\\}\\[\\]\\-_\\+=!@\\#\\$%^&\\*\\|\\']+\\b)";
    private static final String ROMAN_NUMERAL_REGEX = "M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})";
    private static final String RANDOM_TAG_REGEX = "\\{rand:[^{}⸠⸡;]+(;[^{}⸠⸡;]+)*\\}";
    private static final String TAG_REGEX = "\\{((string|database):([^{}\\[\\]⸠⸡:⸻⛌⸮]+)(\\[!?[\\d]+\\])?(\\s*⸻(⛌|⸮|" + INTEGER_REGEX + "))?|method:[a-zA-Z0-9_$]+)\\}";
    private static final String WORD_REGEX = "\\[\\^?\\^?[\\w\\s\\p{L}ªº∅']*\\[[\\w\\s\\p{L}ªº∅']*(,[\\w\\s\\p{L}ªº∅']*)*\\][\\w\\s\\p{L}ªº∅']*\\]";
    private static final String WORD_TAG_REGEX = "\\{(" + WORD_REGEX + ")(⸻(⛌|⸮|" + INTEGER_REGEX + "))?\\}";
    private static final int[] PROBABILITY_DISTRIBUTION = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};
    private static final char[] REPLACEMENTS = {'⚨', '⚩', '⁇', '⍰', '�', '□', '?'};
    private static final String[] COMMON_COLORS = {"#FEFF5B", "#6ABB6A", "#E55B5B", "#5B72E5", "#925BFF"};
    private static final String[] NAME_PATTERNS = {"cv", "cvc", "cvc kvc", "cvcv", "cvccv", "cvcve", "cvcvc cvcve", "cvcvmv", "cvcvmve", "cve", "cvmce", "cvmv", "cvmve", "cvvc", "cvvcvcv", "cvvcvve", "cvve", "cwcvce", "cwcve", "cw cwcv", "cwe", "cwe cve", "cwecvcwe", "kvcv", "kvcve", "kve", "kvmv", "kvmvcv", "kvmvmv", "kwcv", "kwcve", "kwe", "kwmve", "vccv", "vccvc", "vccvcv", "vcv", "vcvc", "vcvccvc", "vcvcv", "vcvcvc", "vcvcvcv", "vcvcvcvc", "vcvcwcv", "vcve", "vcve vcw", "vmv", "vmvcv", "vmve", "vmvmv", "vmvmve", "wcv", "wcvcve", "wcve", "wmv", "wmvcv", "wmve"};
    private static final String[][] GENERATED_NAME_START = {
            {"a", "an", "as", "bra", "ce", "cen", "den", "e", "el", "en", "ghal", "gra", "i", "in", "is", "ka", "kan", "ken", "kha", "kra", "li", "me", "o", "os", "ren", "rha", "se", "sen", "te", "tra", "u", "ul", "un", "ze", "æ"},
            {"a", "a", "a", "a", "a", "ae", "ae", "ae", "ba", "ba", "ba", "be", "be", "be", "bi", "bi", "bi", "bla", "ble", "bli", "blo", "blu", "bo", "bo", "bo", "bra", "bre", "bri", "bro", "bru", "bu", "bu", "bu", "ca", "ca", "ca", "ce", "ce", "ce", "cha", "che", "chi", "cho", "chu", "ci", "ci", "ci", "cla", "cle", "cli", "clo", "clu", "co", "co", "co", "cra", "cre", "cri", "cro", "cru", "cu", "cu", "cu", "da", "da", "da", "de", "de", "de", "di", "di", "di", "dla", "dle", "dli", "dlo", "dlu", "do", "do", "do", "dra", "dre", "dri", "dro", "dru", "du", "du", "du", "e", "e", "e", "e", "e", "fa", "fa", "fa", "fe", "fe", "fe", "fi", "fi", "fi", "fla", "fle", "fli", "flo", "flu", "fo", "fo", "fo", "fra", "fre", "fri", "fro", "fru", "fu", "fu", "fu", "ga", "ga", "ga", "ge", "ge", "ge", "gi", "gi", "gi", "gla", "gle", "gli", "glo", "glu", "go", "go", "go", "gra", "gre", "gri", "gro", "gru", "gu", "gu", "gu", "gue", "gui", "güe", "güi", "ha", "he", "hi", "ho", "hu", "i", "i", "i", "i", "i", "ja", "ja", "ja", "je", "je", "je", "ji", "ji", "ji", "jo", "jo", "jo", "ju", "ju", "ju", "ka", "ka", "ke", "ke", "ki", "ki", "ko", "ko", "ku", "ku", "la", "la", "la", "le", "le", "le", "li", "li", "li", "li", "lla", "lle", "lli", "llo", "llu", "lo", "lo", "lo", "lu", "lu", "lu", "ma", "ma", "ma", "me", "me", "me", "mi", "mi", "mi", "mo", "mo", "mo", "mu", "mu", "mu", "na", "na", "na", "ne", "ne", "ne", "ni", "ni", "ni", "no", "no", "no", "nu", "nu", "nu", "o", "o", "o", "o", "o", "ou", "ou", "ou", "pa", "pa", "pa", "pe", "pe", "pe", "pi", "pi", "pi", "pla", "ple", "pli", "plo", "plu", "po", "po", "po", "pra", "pre", "pri", "pro", "pru", "pu", "pu", "pu", "que", "qui", "ra", "ra", "ra", "re", "re", "re", "ri", "ri", "ri", "ro", "ro", "ro", "ru", "ru", "ru", "sa", "sa", "sa", "se", "se", "se", "sha", "she", "shi", "sho", "shu", "si", "si", "si", "so", "so", "so", "su", "su", "su", "ta", "ta", "ta", "te", "te", "te", "ti", "ti", "ti", "tla", "tle", "tli", "tlo", "tlu", "to", "to", "to", "tra", "tre", "tri", "tro", "tru", "tu", "tu", "tu", "u", "u", "u", "u", "u", "va", "va", "va", "ve", "ve", "ve", "vi", "vi", "vi", "vo", "vo", "vo", "vu", "vu", "vu", "wa", "wa", "we", "we", "wi", "wi", "wo", "wo", "wu", "wu", "xa", "xe", "xi", "xo", "xu", "ya", "ya", "ye", "ye", "yi", "yi", "yo", "yo", "yu", "yu", "za", "za", "ze", "ze", "zi", "zi", "zo", "zo", "zu", "zu"},
            {"a", "au", "be", "ba", "da", "do", "dra", "dul", "e", "el", "ga", "gan", "gwen", "gwyn", "gyl", "ha", "i", "il", "ki", "kin", "la", "le", "li", "lo", "ma", "mae", "mal", "mir", "mla", "nae", "ne", "ni", "nu", "ny", "o", "rau", "sa", "sae", "sal", "san", "se", "sil", "syl", "ta", "tho", "ti", "ty", "u", "ua", "va", "vi", "vyr"}};
    private static final String[][] GENERATED_NAME_MIDDLE = {
            {"ba", "ce", "da", "de", "dho", "dra", "ga", "ge", "gen", "gha", "gi", "hla", "hlo", "ka", "kar", "ko", "ma", "na", "pa", "par", "ta", "tha", "va", "ve", "ze", "zhe"},
            {"", "", "", "", "", "", "", "ba", "ba", "ba", "be", "be", "be", "bi", "bi", "bi", "bla", "ble", "bli", "blo", "blu", "bo", "bo", "bo", "bra", "bre", "bri", "bro", "bru", "bu", "bu", "bu", "ca", "ca", "ca", "ce", "ce", "ce", "cha", "che", "chi", "cho", "chu", "ci", "ci", "ci", "cia", "cie", "cio", "ciu", "cla", "cle", "cli", "clo", "clu", "co", "co", "co", "cra", "cre", "cri", "cro", "cru", "cu", "cu", "cu", "da", "da", "da", "de", "de", "de", "di", "di", "di", "dia", "die", "dio", "diu", "dla", "dle", "dli", "dlo", "dlu", "do", "do", "do", "dra", "dre", "dri", "dro", "dru", "du", "du", "du", "fa", "fa", "fa", "fe", "fe", "fe", "fi", "fi", "fi", "fla", "fle", "fli", "flo", "flu", "fo", "fo", "fo", "fra", "fre", "fri", "fro", "fru", "fu", "fu", "fu", "ga", "ga", "ga", "ge", "ge", "ge", "gi", "gi", "gi", "gla", "gle", "gli", "glo", "glu", "gna", "gno", "go", "go", "go", "gra", "gre", "gri", "gro", "gru", "gu", "gu", "gu", "gue", "gui", "güe", "güi", "ha", "he", "hi", "ho", "hu", "ja", "ja", "ja", "je", "je", "je", "ji", "ji", "ji", "jo", "jo", "jo", "ju", "ju", "ju", "ka", "ka", "ke", "ke", "ki", "ki", "ko", "ko", "ku", "ku", "la", "la", "la", "lba", "lbo", "le", "le", "le", "li", "li", "li", "lla", "lle", "lli", "llo", "llu", "lma", "lmo", "lo", "lo", "lo", "lu", "lu", "lu", "ma", "ma", "ma", "me", "me", "me", "mi", "mi", "mi", "mo", "mo", "mo", "mu", "mu", "mu", "na", "na", "na", "nae", "nai", "nao", "nau", "ne", "ne", "ne", "ni", "ni", "ni", "nia", "nie", "nio", "niu", "no", "no", "no", "nu", "nu", "nu", "pa", "pa", "pa", "pe", "pe", "pe", "pi", "pi", "pi", "pla", "ple", "pli", "plo", "plu", "po", "po", "po", "pra", "pre", "pri", "pro", "pru", "pu", "pu", "pu", "que", "qui", "ra", "ra", "ra", "re", "re", "re", "ri", "ri", "ri", "ro", "ro", "ro", "rra", "rre", "rri", "rro", "rru", "ru", "ru", "ru", "sa", "sa", "sa", "se", "se", "se", "sha", "she", "shi", "sho", "shu", "si", "si", "si", "so", "so", "so", "su", "su", "su", "ta", "ta", "ta", "te", "te", "te", "ti", "ti", "ti", "tla", "tle", "tli", "tlo", "tlu", "to", "to", "to", "tra", "tre", "tri", "tro", "tru", "tu", "tu", "tu", "va", "va", "va", "ve", "ve", "ve", "vi", "vi", "vi", "vo", "vo", "vo", "vu", "vu", "vu", "wa", "wa", "we", "we", "wi", "wi", "wo", "wo", "wu", "wu", "xa", "xe", "xi", "xo", "xu", "ya", "ya", "ye", "ye", "yi", "yi", "yo", "yo", "yu", "yu", "za", "za", "ze", "ze", "zi", "zi", "zo", "zo", "zu", "zu", "ña", "ñe", "ñi", "ño", "ñu"},
            {"", "", "", "", "bri", "ci", "cia", "da", "di", "dil", "do", "dre", "dri", "dy", "dyr", "fyl", "fyr", "la", "lan", "li", "lin", "lir", "los", "lu", "ma", "mi", "mil", "mir", "na", "nae", "ni", "nim", "ny", "nya", "ra", "re", "rea", "ri", "rina", "rio", "ryn", "sa", "sar", "sil", "sur", "tar", "tau", "to", "tou", "ve", "vha", "vi", "vil", "zi", "zi", "zur"}};
    private static final String[][] GENERATED_NAME_ENDING = {
            {"drin", "gen", "ghar", "gra", "kan", "ken", "kin", "ko", "kyo", "ma", "na", "nen", "nia", "nin", "rar", "ria", "rin", "rio", "rion", "ryo", "ryon", "til", "vka", "vkin", "vko", "vrin", "vyon", "zen", "zin"},
            {"ba", "ba", "ba", "be", "be", "be", "bel", "bela", "bi", "bi", "bi", "bia", "bio", "bla", "ble", "bli", "blo", "blu", "bo", "bo", "bo", "bra", "bre", "bri", "bro", "bru", "bu", "bu", "bu", "ca", "ca", "ca", "ce", "ce", "ce", "cha", "che", "chi", "cho", "chu", "ci", "ci", "ci", "cia", "ciana", "ciano", "cio", "cion", "cla", "cle", "cli", "clo", "clu", "co", "co", "co", "cra", "cre", "cri", "crita", "crito", "cro", "cru", "cta", "cto", "cu", "cu", "cu", "da", "da", "da", "de", "de", "de", "dea", "deo", "des", "di", "di", "di", "dia", "dio", "dios", "dla", "dle", "dli", "dlo", "dlu", "dna", "dno", "do", "do", "do", "don", "dona", "dor", "dora", "dra", "dra", "dre", "dri", "dro", "dro", "dru", "du", "du", "du", "fa", "fa", "fa", "fas", "fe", "fe", "fe", "fi", "fi", "fi", "fla", "fle", "fli", "flo", "flu", "fo", "fo", "fo", "fra", "fre", "fri", "fro", "fru", "fu", "fu", "fu", "ga", "ga", "ga", "ge", "ge", "ge", "gi", "gi", "gi", "gla", "gle", "gli", "glo", "glu", "gna", "gno", "go", "go", "go", "gra", "gre", "gri", "gro", "gru", "gu", "gu", "gu", "gue", "gui", "güe", "güi", "ha", "he", "hi", "ho", "hu", "ja", "ja", "ja", "je", "je", "je", "ji", "ji", "ji", "jo", "jo", "jo", "ju", "ju", "ju", "ka", "ka", "ke", "ke", "ki", "ki", "ko", "ko", "ku", "ku", "l", "l", "l", "la", "la", "la", "lba", "lbo", "lda", "ldo", "le", "le", "le", "lea", "leo", "li", "li", "li", "lia", "liano", "lina", "lino", "lio", "lla", "lle", "lli", "llo", "llu", "lma", "lmo", "lo", "lo", "lo", "lon", "lona", "lu", "lu", "lu", "ma", "ma", "ma", "me", "me", "me", "mi", "mi", "mi", "mia", "min", "mina", "mio", "mo", "mo", "mo", "mu", "mu", "mu", "n", "n", "n", "n", "na", "na", "na", "nca", "ncia", "ncio", "nco", "nda", "ndo", "ne", "ne", "ne", "ni", "ni", "ni", "nia", "nio", "no", "no", "no", "nsa", "nso", "nta", "nto", "nu", "nu", "nu", "pa", "pa", "pa", "pe", "pe", "pe", "pi", "pi", "pi", "pla", "ple", "pli", "plo", "plu", "po", "po", "po", "pra", "pre", "pri", "pro", "pru", "pu", "pu", "pu", "que", "qui", "r", "r", "r", "r", "ra", "ra", "ra", "rda", "rda", "rdo", "rdo", "re", "re", "re", "rea", "reo", "res", "ri", "ri", "ri", "ria", "riana", "riano", "rio", "ro", "ro", "ro", "rra", "rrat", "rre", "rri", "rro", "rru", "rta", "rta", "rto", "rto", "ru", "ru", "ru", "s", "s", "s", "s", "sa", "sa", "sa", "sar", "sara", "sca", "sco", "se", "se", "se", "sha", "she", "shi", "sho", "shu", "si", "si", "si", "sia", "sio", "sme", "so", "so", "so", "su", "su", "su", "ta", "ta", "ta", "tan", "tano", "te", "te", "te", "tea", "teo", "ti", "ti", "ti", "tian", "tiana", "tilde", "tin", "tina", "tla", "tle", "tli", "tlo", "tlu", "to", "to", "to", "tra", "tre", "tri", "triz", "tro", "tru", "tu", "tu", "tu", "va", "va", "va", "ve", "ve", "ve", "vi", "vi", "vi", "via", "vio", "vo", "vo", "vo", "vu", "vu", "vu", "wa", "wa", "we", "we", "wi", "wi", "wo", "wo", "wu", "wu", "xa", "xe", "xi", "xo", "xu", "ya", "ya", "ye", "ye", "yi", "yi", "yo", "yo", "yu", "yu", "z", "z", "z", "z", "za", "za", "ze", "ze", "zi", "zi", "zo", "zo", "zon", "zona", "zu", "zu", "ña", "ñe", "ñi", "ño", "ñu"},
            {"baus", "dam", "dar", "dha", "dhae", "dho", "dia", "dil", "dio", "dor", "dra", "drian", "driel", "druth", "dur", "dyl", "ema", "la", "lae", "len", "lis", "lien", "lynn", "mir", "mor", "myr", "na", "nae", "nar", "nia", "nil", "nio", "nna", "nni", "nor", "nya", "ra", "rae", "rail", "ran", "rea", "rean", "reon", "reus", "rhial", "ria", "riel", "ril", "rio", "ris", "rium", "ron", "ryn", "sa", "sil", "sila", "sin", "tael", "tha", "thaus", "the", "thur", "tra", "tur", "ver", "via", "vil", "vio", "vir", "vis", "vlis"}};
    private static final String[][] GENERATED_NAME_FAMILY_NAME_SUFFIX = {
            {"gha", "gho", "kem", "kema", "ken", "kenna", "ma", "mi", "n", "na", "nem", "nema", "ni", "nma", "ra", "re", "rha", "rhin", "rho", "rin", "ten", "tenna", "zen", "zenna", "zha", "zho", "zya", "zya"},
            {"aba", "abe", "abi", "aca", "ach", "aco", "ada", "ade", "adi", "ado", "aga", "ago", "ahi", "ain", "aiz", "aja", "ajo", "ala", "ale", "ali", "all", "alo", "ama", "ami", "amo", "ana", "ane", "ani", "ano", "ans", "anu", "any", "anz", "ara", "ard", "ari", "aro", "art", "aru", "asa", "aso", "ata", "ate", "ati", "ato", "aujo", "ave", "aya", "ayo", "aza", "azo", "bal", "ban", "bar", "bas", "bel", "bes", "bez", "bia", "bon", "bra", "cal", "can", "cas", "cea", "ces", "cha", "che", "chez", "chi", "cho", "cia", "cio", "ciu", "con", "cos", "dad", "dal", "dan", "dar", "das", "dea", "der", "des", "dez", "dia", "din", "dina", "dino", "dio", "don", "dor", "dos", "dra", "dro", "ean", "eca", "ech", "eco", "eda", "edo", "ega", "egi", "ego", "eja", "ejo", "ela", "ell", "elo", "ena", "eno", "ens", "ent", "er", "era", "eri", "ero", "ert", "esa", "eso", "eta", "ete", "eto", "eva", "ez", "eza", "gal", "gan", "gas", "ger", "ges", "gil", "gon", "gos", "gua", "gue", "guer", "guez", "gui", "hal", "han", "har", "hea", "her", "hir", "hou", "ia", "ian", "ias", "ibi", "ica", "ich", "ico", "ida", "ide", "idi", "ido", "iel", "ier", "ies", "iga", "igo", "ijo", "ila", "ili", "imi", "ina", "ine", "ini", "ino", "ion", "ios", "ira", "ire", "iri", "iro", "is", "isa", "iso", "ita", "iti", "ito", "iuc", "iva", "iz", "iza", "izo", "jar", "jas", "jon", "jos", "la", "lah", "lal", "lan", "lar", "las", "lat", "lda", "lde", "ldo", "lea", "ler", "les", "let", "lez", "li", "lia", "lin", "lio", "lis", "lla", "lle", "lli", "llo", "lls", "lo", "lon", "los", "lta", "lva", "man", "mar", "mas", "med", "mes", "mil", "min", "mon", "mpa", "na", "nal", "nas", "nau", "nca", "nce", "nco", "nda", "nde", "ndi", "ndo", "nea", "ner", "nes", "net", "nez", "nga", "ngo", "nis", "niz", "no", "nos", "nov", "nta", "nte", "nto", "nza", "nzo", "oba", "oca", "oiu", "ola", "oli", "olo", "ols", "ona", "oni", "ons", "ora", "oro", "ort", "osa", "oso", "ota", "ote", "oto", "oud", "ouh", "oui", "ouk", "oul", "oun", "our", "out", "ova", "oya", "oyo", "oza", "pez", "que", "qui", "ra", "ral", "ran", "ras", "rat", "ray", "raz", "rca", "rda", "rdi", "rdo", "rea", "ren", "res", "ret", "rey", "rez", "rga", "ria", "rin", "rio", "ris", "riu", "riz", "rna", "ron", "ronda", "rondo", "ros", "rra", "rre", "rri", "rro", "rta", "rte", "rto", "rza", "san", "sar", "sas", "sca", "sco", "scu", "sen", "ses", "sio", "son", "ssa", "ssi", "sta", "ste", "sti", "sto", "tal", "tan", "tar", "tas", "tea", "tel", "ter", "tes", "tia", "tin", "to", "ton", "tor", "tos", "tra", "tre", "tro", "tti", "uan", "ubi", "uca", "uch", "udi", "udo", "uel", "uer", "ues", "uet", "uez", "uin", "ula", "umi", "una", "uni", "ura", "uri", "uro", "uru", "usa", "uta", "uti", "uza", "val", "van", "vas", "ver", "ves", "via", "ya", "yan", "yes", "yo", "zan", "zar", "zas", "zo", "zon"},
            {"udaeus", "udalis", "udeus", "udhil", "udhur", "udyr", "udur", "udyl", "ufur", "ulden", "uldor", "uldur", "ulen", "ulenyr", "ulinor", "ulnur", "ulond", "ulur", "ulthur", "um", "umur", "umus", "umyr", "unden", "unor", "unor", "urde", "ureus", "urin", "uris", "urus", "uryn", "us", "ustur", "utur", "uvaeus", "uvaerus", "uvir", "uvur", "uvurus"}};
    private static final String[] MIDDLE_CONSONANTS = {"bd", "bn", "bs", "cc", "ct", "dj", "ds", "gn", "lf", "lm", "lp", "ls", "lt", "mb", "mn", "mp", "ms", "nc", "nf", "ng", "nj", "nk", "nl", "nm", "nn", "nr", "ns", "nz", "nt", "nv", "nz", "pc", "ps", "pt", "rb", "rc", "rd", "rg", "rj", "rl", "rm", "rn", "rp", "rr", "rs", "rt", "sb", "sc", "sc", "sf", "sl", "sn", "sp", "ss", "st"};
    private static final String[] ENDING_CONSONANTS = {"ng", "nn", "nt", "rn", "rsk", "rst", "rg", "st", "th", "tt"};
    private static final String[] CONSONANT_PAIRS = {"bl", "br", "ch", "cl", "cr", "dl", "dr", "fl", "fr", "gl", "gr", "kh", "kl", "kr", "ll", "pl", "pr", "rh", "sh", "tl", "tr", "vl", "vr"};
    private static final String[] ANY_CONSONANT_PAIRS = {"bl", "br", "by", "ch", "cl", "cr", "cy", "dr", "dw", "fl", "fn", "fr", "fy", "gh", "gl", "gn", "gr", "gw", "gy", "hl", "hw", "hy", "kn", "kr", "ky", "ly", "ny", "ph", "pr", "ps", "py", "rh", "ry", "sc", "my", "sh", "sk", "sl", "sm", "sn", "sp", "sq", "st", "sw", "sy", "pl", "tp", "tr", "tw", "ty", "vy", "wh", "wl", "th", "vl", "wr", "wy", "yb", "yc", "yd", "yf", "yh", "yl", "ym", "yn", "yp", "yr", "ys", "yt", "yv", "yw"};
    private static final String[] VOWEL_PAIRS = {"ae", "ai", "ao", "au", "ea", "ei", "eo", "eu", "ia", "ie", "io", "iu", "oa", "oe", "oi", "ou", "ua", "ue", "ui", "uo", "æ", "œ"};
    private static final String[] STARTING_CONSONANTS = {"bh", "bj", "bl", "br", "by", "cc", "ch", "ck", "cl", "cn", "cr", "ct", "cy", "cz", "dd", "dh", "dm", "dn", "dr", "dw", "dy", "dz", "fl", "fr", "gh", "gl", "gm", "gn", "gr", "gs", "gw", "gy", "hj", "hl", "hm", "hn", "hr", "hs", "hw", "hy", "jd", "js", "jy", "kh", "kj", "kl", "kn", "kr", "kw", "ky", "ld", "lh", "lj", "ll", "lm", "ls", "lt", "lv", "ly", "mb", "mc", "ml", "mn", "mp", "mr", "my", "nc", "nd", "ng", "nn", "ns", "nt", "ny", "pf", "ph", "pl", "pr", "ps", "py", "rd", "rh", "rl", "rn", "rr", "rs", "rt", "rv", "rw", "ry", "sc", "sh", "sj", "sk", "sl", "sm", "sn", "sp", "sq", "sr", "ss", "st", "sv", "sw", "sy", "sz", "tc", "th", "tj", "tl", "tr", "ts", "tt", "tw", "ty", "tz", "vh", "vl", "vr", "vt", "vy", "wh", "wr", "ws", "wy", "xy", "yc", "yd", "yf", "yg", "yk", "yl", "ym", "yn", "ys", "yt", "yv", "yw", "zh", "zr", "zs", "zw", "zy"};
    private static final String[] SUPPORTED_LANGUAGES = {"ar", "de", "fr", "hi", "it", "pt"};
    private static final Pattern DOUBLE_CONSONANT_PATTERN = Pattern.compile("^[" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "]{2}.+");
    private static final Pattern DOUBLE_CONSONANT_AND_VOWEL_START_PATTERN = Pattern.compile("^[" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "]{2}[" + FULL_LOWERCASE_VOWELS + "](.+)");
    private static final Pattern TRIPLE_CONSONANT_PATTERN = Pattern.compile("([" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "])[" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "]([" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "])");
    private static final Pattern MULTIPLE_CONSONANT_PATTERN = Pattern.compile("([" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "]{2})([" + LOWERCASE_CONSONANTS + "ðᵹþſƿ" + "]{1,})");
    private static final Pattern DOUBLE_DOT_PATTERN = Pattern.compile(DOUBLE_FULL_STOP_REGEX);
    private static final Pattern FORMAT_PATTERN = Pattern.compile(FORMAT_REGEX);
    private static final Pattern LATIN_OR_SPACE_PATTERN = Pattern.compile(LATIN_OR_SPACE_REGEX);
    private static final Pattern SEX_PATTERN = Pattern.compile("(｢[0-2]｣)");
    private static final Pattern SEX_APPENDIX_PATTERN = Pattern.compile("⸻" + INTEGER_REGEX);
    private static final Pattern INDEFINITE_ARTICLE_PATTERN = Pattern.compile(INDEFINITE_ARTICLE_REGEX);
    private static final Pattern ROMAN_NUMERAL_PATTERN = Pattern.compile("(^|\\s+)" + ROMAN_NUMERAL_REGEX + "($|\\s+)");
    private static final Pattern MULTIPLE_VOWEL_PATTERN = Pattern.compile("([" + FULL_LOWERCASE_VOWELS + "])[" + FULL_LOWERCASE_VOWELS + "]{1,}([" + FULL_LOWERCASE_VOWELS + "])");
    private static final Pattern RANDOM_TAG_PATTERN = Pattern.compile(RANDOM_TAG_REGEX);
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final Pattern WORD_PATTERN = Pattern.compile(WORD_REGEX);
    private static final Pattern WORD_TAG_PATTERN = Pattern.compile(WORD_TAG_REGEX);
    private static Field[] versionCodes;
    private static Toast toast;
    private static MediaPlayer mediaPlayer;
    private static Paint paint = new Paint();
    private static List<String> commonNames;
    private static List<String> commonLastNames;
    private static List<String> compoundLastNames;
    private static List<String> romanNumerals;
    private static List<String> postNominalLetters;
    private static List<String> colors;
    private static List<String> contactNames;
    private static HashMap<Integer, Integer> countRegistry = new HashMap<>();
    private static boolean initialized = false;
    private static boolean retrieved = false;
    private static char[] sexes = {'⁇', '⚲', '♂', '♀'};
    private int themeId = 0;
    private DatabaseConnection myDB;
    private Randomizer randomizer;

    static {
        locales = Locale.getAvailableLocales();
        versionCodes = Build.VERSION_CODES.class.getFields();
    }

    public Methods(Context context) {
        super(context);
        myDB = new DatabaseConnection(context);
        randomizer = new Randomizer();

        if (!initialized) {
            commonNames = getStringAsList(R.string.common_names);
            commonLastNames = getStringAsList(R.string.common_last_names);
            compoundLastNames = getStringAsList(R.string.compound_last_names);
            romanNumerals = getStringAsList(R.string.roman_numerals);
            postNominalLetters = getStringAsList(R.string.post_nominal_letters);
            colors = getStringAsList(R.string.common_colors);

            for (int n = -1, length = sexes.length; ++n < length; ) {
                if (!isGlyphDisplayable(sexes[n])) {
                    int start = -1;

                    if (sexes[n] != '⚲')
                        start = 1;

                    for (int o = start; ++o < REPLACEMENTS.length; ) {
                        if (isGlyphDisplayable(sexes[n] = REPLACEMENTS[o]))
                            o = REPLACEMENTS.length;
                    }
                }
            }
            networkCountry = getNetworkCountry();
            availableNames = getPermittedNames(availableNames, true);
            supportedNames = getPermittedNames(supportedNames, false);
            initialized = true;
        }

        if (!retrieved && context instanceof Activity) {
            contactNames = getContactNames();
            retrieved = true;
        }
    }

    public Methods(Context context, Long seed) {
        this(context);
        getRandomizer().bindSeed(seed);
    }

    public Randomizer getRandomizer() {
        return randomizer;
    }

    /* String methods :: Common */

    public String getDeviceCommonUser() {
        switch (randomizer.getInt(0, 9)) {
            case 0:
                String name = "0.0.0";

                if (versionCodes != null && versionCodes.length > 0) {
                    for (Field field : versionCodes) {
                        String fieldName = field.getName();
                        int fieldValue = -1;

                        try {
                            fieldValue = field.getInt(new Object());
                        } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                            e.printStackTrace();
                        }

                        if (fieldValue == Build.VERSION.SDK_INT)
                            name = fieldName;
                    }
                }

                if (name.equals("0.0.0")) {
                    try {
                        name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return getString(R.string.device_version_user, genderify(getString(R.string.default_user), getSex()).getText(), name + " " + "(" + Build.VERSION.RELEASE + ")");
            case 1:
                return getString(R.string.device_user, genderify(getString(R.string.default_user), getSex()).getText(), (Build.MANUFACTURER.isEmpty() ? "?" : Build.MANUFACTURER) + (Build.MODEL.isEmpty() ? "" : " ") + Build.MODEL);
            case 2:
                return getString(R.string.device_user, genderify(getString(R.string.default_user), getSex()).getText(), (Build.BRAND.isEmpty() ? "?" : Build.BRAND) + (Build.MODEL.isEmpty() ? "" : " ") + Build.MODEL);
            case 3:
                return getString(R.string.device_user, genderify(getString(R.string.default_user), getSex()).getText(), Build.PRODUCT.isEmpty() ? "?" : Build.PRODUCT);
            case 4:
                return getString(R.string.android_device_user, genderify(getString(R.string.default_user), getSex()).getText(), getDeviceId());
            case 5:
                return getString(R.string.device_brand_user, genderify(getString(R.string.default_user), getSex()).getText(), Build.BRAND.isEmpty() ? "?" : Build.BRAND);
            case 6:
                if (isNetworkAvailable()) {
                    String networkName = getNetworkName().trim();
                    return getString(R.string.network_user, networkName.isEmpty() ? "?" : networkName);
                } else
                    return getStringFromRes(R.string.no_network_user);
            case 7:
                String networkOperator = getNetworkOperator();

                if (networkOperator.trim().isEmpty())
                    return getStringFromRes(R.string.no_network_operator_user);
                else
                    return getString(R.string.network_operator_user, networkOperator);
            case 8:
                String ipAddress = getLocalIpAddress();

                if (ipAddress == null || ipAddress.isEmpty())
                    return getStringFromRes(R.string.disconnected_device_user);
                else
                    return getString(R.string.connected_device_user, ipAddress);
            default:
                return "";
        }
    }

    public TextComponent genderify(String s, Integer sex) {
        TextComponent component = new TextComponent();
        component.setText(s);

        if (sex == null)
            return component;
        else if (sex < -1 || sex > 2)
            sex = -1;
        Matcher matcher = WORD_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String replacement = "";
            String substring = StringUtils.substring(matcher.group(), 1, matcher.group().length() - 1);
            boolean capitalized = !substring.equals(substring = StringUtils.removeStart(substring, "^"));
            boolean fullyCapitalized = capitalized && !substring.equals(substring = StringUtils.removeStart(substring, "^"));
            String prefix = StringUtils.substringBefore(substring, "[");
            String suffix = StringUtils.substringAfter(substring, "]");
            substring = StringUtils.substringsBetween(substring, "[", "]")[0];
            List<String> items = Arrays.asList(substring.split(",\\s*"));
            boolean shortened = false;

            if (StringUtils.isNotEmpty(substring) && StringUtils.isBlank(suffix)) {
                List<String> sortedItems = new ArrayList<>(items);

                Collections.sort(sortedItems, (item, otherItem) -> {
                    if (item.length() > otherItem.length())
                        return 1;
                    else
                        return item.compareTo(otherItem);
                });

                if (sortedItems.size() >= 2) {
                    if (sortedItems.get(0).length() <= 1 && sortedItems.get(sortedItems.size() - 1).length() <= 1)
                        shortened = true;
                }
            }

            if (items.size() == 0)
                replacement = prefix + suffix;
            else if (items.size() == 1)
                replacement = prefix + items.get(0) + suffix;
            else if (sex == -1 || sex == 0) {
                if (shortened)
                    replacement = prefix + items.get(0) + "(" + StringUtils.join(items.subList(1, items.size()), ", ") + ")";
                else {
                    for (int n = 0; n < items.size(); n++) {
                        items.set(n, prefix + items.get(n) + suffix);
                    }
                    replacement = StringUtils.join(items, "/");
                }
            } else
                replacement = prefix + (sex == 1 ? items.get(0) : items.get(1)) + suffix;

            if (fullyCapitalized)
                replacement = capitalize(replacement);
            else if (capitalized)
                replacement = capitalizeFirst(replacement);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        if (StringUtils.isNotBlank(sb.toString()))
            component.setText(sb.toString());
        component.setHegemonicSex(sex);
        return component;
    }

    public String getDesignation(Enum designation) {
        if (designation instanceof Name) {
            List<Name> preferredNames = new ArrayList<>();
            preferredNames.add(Name.EMPTY);
            preferredNames.add(Name.TEST_CASE);
            preferredNames.add((Name) designation);
            Entity entity = getEntity(preferredNames);
            return entity.getSimpleName();
        } else if (designation instanceof Pseudonym)
            return generatePseudonym((Pseudonym) designation);
        return "";
    }

    public String getNames(int sex) {
        float[] probability = {1.0F, 0.8F, 0.2F, 0.05F, 0.0125F};
        StringBuilder sb = new StringBuilder();

        for (int n = -1, length = probability.length; ++n < length; ) {
            if (randomizer.getFloat() <= probability[n]) {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(sex == 1 ? getMaleName() : getFemaleName());
            } else
                n = length - 1;
        }
        return sb.toString();
    }

    public String getLastNames() {
        float probability = randomizer.getFloat();

        if (probability <= 0.075F)
            return getCompoundSurname() + " " + getLastName();
        else if (probability <= 0.15F)
            return getLastName() + " " + getCompoundSurname();
        else
            return getLastName() + " " + getLastName();
    }

    public String[] getGeneratedAdjustedNames(int aggregate, String subtype) {
        String[] names = aggregate <= 0 ? new String[]{""} : new String[aggregate];
        int index;

        if (subtype != null && !subtype.isEmpty()) {
            String[] subtypes = {"old_english", "spanish"};
            index = ArrayUtils.lastIndexOf(subtypes, subtype);
            index = index > -1 ? index : randomizer.getInt(0, subtypes.length);
        } else
            index = randomizer.getInt(0, getArrayResLength(R.array.shaper_strings));
        String resourceName = getStringFromStringArray(R.array.shaper_strings, index);
        String letters = getRawStringByName(resourceName);

        if (letters != null && !letters.isEmpty()) {
            for (int n = -1; ++n < aggregate; ) {
                names[n] = getPreformedName(letters, StringUtils.removeStart(resourceName, "shaper_"));
            }
        }

        if (ArrayUtils.isEmpty(names))
            Arrays.fill(names, "");
        return names;
    }

    public String[] getGeneratedNames(int aggregate, String subtype) {
        String[] names = aggregate <= 0 ? new String[]{""} : new String[aggregate];
        int index;

        if (subtype != null && !subtype.isEmpty()) {
            String[] subtypes = {"afrikaans", "english", "latin", "marshallese", "spanish"};
            index = 0;

            for (int n = 0; n < subtypes.length; n++) {
                if (subtypes[n].equals(subtype))
                    n = subtypes.length;
                else
                    index++;
            }

            if (index == subtypes.length)
                index = randomizer.getInt(0, index);
        } else
            index = randomizer.getInt(0, getArrayResLength(R.array.frequency_strings));
        int minLength = 4, extraLength = new int[]{6, 4, 6, 4, 8}[index];
        String letters = getStringFromStringArray(R.array.frequency_strings, index);

        if (letters != null && !letters.isEmpty()) {
            for (int n = 0; n < aggregate; n++) {
                names[n] = generateName(letters, randomizer.getInt(minLength, extraLength + 1));
            }
        }

        if (!ArrayUtils.isNotEmpty(names))
            Arrays.fill(names, "");
        return names;
    }

    public String getCompoundSurname() {
        return getStringFromList(compoundLastNames);
    }

    public String getDoubleBarrelledSurname() {
        return getEnglishLastName() + SEPARATOR[0] + getEnglishLastName();
    }

    public String getContactName(boolean formatted) {
        String contactName = "";

        try {
            if (contactNames != null && contactNames.size() > 0) {
                contactName = contactNames.get(randomizer.getInt(0, contactNames.size())).trim();

                if (!formatted)
                    return contactName;

                if (StringUtils.isNotEmpty(contactName)) {
                    if (isEmailValid(contactName))
                        return "<font color=#FFFFC6>" + contactName + "</font>";
                    else if (isUrlValid(contactName))
                        contactName = formatText(contactName, "a");
                    else if (!StringUtils.containsWhitespace(contactName))
                        contactName = formatText(contactName, "b,tt");
                    else
                        contactName = formatText(contactName, "b");
                    return "<font color=" + getColorString() + ">" + contactName + "</font>";
                }
            }
        } catch (Exception ignored) {
        }
        return contactName;
    }

    public String getSuggestedName(boolean formatted) {
        String suggestedName = "";
        List<String> suggestions = new ArrayList(preferences.getStringSet("nameList"));

        if (suggestions != null && suggestions.size() >= 1) {
            suggestions.add(getString(R.string.developer));

            do {
                suggestedName = suggestions.get(randomizer.getInt(0, suggestions.size()));
            } while (preferences.getString("temp_name").equals(suggestedName));
            suggestedName = StringUtils.defaultIfBlank(suggestedName, "?");

            if (!formatted)
                return suggestedName;

            if (StringUtils.equalsIgnoreCase(suggestedName, getString(R.string.developer)))
                suggestedName = Methods.formatText(suggestedName, "b, i");
            else
                suggestedName = Methods.formatText(suggestedName, "b");
        }
        return suggestedName;
    }

    public String getDemonicName(String s) {
        if (StringUtils.isNotBlank(s)) {
            s = RegExUtils.replaceAll(s, ROMAN_NUMERAL_PATTERN, " ").trim();

            if (StringUtils.isAlphaSpace(s) || LATIN_OR_SPACE_PATTERN.matcher(s).matches()) {
            } else {
                String temp = RegExUtils.removeAll(s, "[^\\p{Latin}\\s]").trim();

                if (StringUtils.isAllBlank(temp))
                    s = generateName();
                else
                    s = temp;
            }
            s = normalize(s);
            s = s.toLowerCase();
            s = StringUtils.reverse(s);
            s = capitalize(s);
        }
        return s;
    }

    public String generateGibberish(int minLength) {
        if (minLength < 0)
            minLength = 0;
        int length = randomizer.getInt(minLength, 13);
        int spaceIndex = 0;
        int index = 0;
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        boolean defined = false;

        for (int i = 0; i < length; i++) {
            if (!defined) {
                if (randomizer.getBoolean())
                    spaceIndex = randomizer.getInt(0, 7) + randomizer.getInt(0, 6) + 1;
                else
                    spaceIndex = randomizer.getInt(1, 12);
                index = 0;
                defined = true;
            }
            sb.append(ACCENTED_CHARACTERS.charAt(rnd.nextInt(ACCENTED_CHARACTERS.length())));
            index++;

            if (index >= spaceIndex && i < length - 1) {
                sb.append(" ");
                defined = false;
            }
        }
        return sb.toString();
    }

    private String generateName() {
        String name;
        String namePattern = NAME_PATTERNS[randomizer.getInt(0, NAME_PATTERNS.length)];
        StringBuilder sb = new StringBuilder();

        if (randomizer.getBoolean())
            namePattern = StringUtils.replaceChars(namePattern, 'e', 'ɘ');

        if (randomizer.getBoolean())
            namePattern = StringUtils.replaceChars(namePattern, 'k', 'q');

        for (char c : namePattern.toCharArray()) {
            switch (c) {
                case 'c':
                    sb.append(LOWERCASE_CONSONANTS.charAt(randomizer.getInt(0, LOWERCASE_CONSONANTS.length())));
                    break;
                case 'e':
                    sb.append(LOWERCASE_ENDING_CONSONANTS.charAt(randomizer.getInt(0, LOWERCASE_ENDING_CONSONANTS.length())));
                    break;
                case 'ɘ':
                    sb.append(ENDING_CONSONANTS[randomizer.getInt(0, ENDING_CONSONANTS.length)]);
                    break;
                case 'k':
                    sb.append(CONSONANT_PAIRS[randomizer.getInt(0, CONSONANT_PAIRS.length)]);
                    break;
                case 'm':
                    sb.append(MIDDLE_CONSONANTS[randomizer.getInt(0, MIDDLE_CONSONANTS.length)]);
                    break;
                case 'q':
                    sb.append(STARTING_CONSONANTS[randomizer.getInt(0, STARTING_CONSONANTS.length)]);
                    break;
                case 'v':
                    sb.append(LOWERCASE_VOWELS.charAt(randomizer.getInt(0, LOWERCASE_VOWELS.length())));
                    break;
                case 'w':
                    sb.append(VOWEL_PAIRS[randomizer.getInt(0, VOWEL_PAIRS.length)]);
                    break;
                case ' ':
                    sb.append(' ');
                    break;
            }
        }
        name = sb.toString();
        name = capitalize(name);
        return name;
    }

    private String generateName(String letters, int length) {
        String s;
        StringBuilder sb = new StringBuilder();
        char previousLetter = '\0';
        char currentLetter = getAChar(letters);
        boolean sameType = false;
        boolean allowed;
        boolean equal;
        boolean vowelFound, anotherVowelFound;
        float[] approvalRate = {0.5F, 0.25F, 0F};

        int count = 0;

        if (letters == null || letters.length() == 0)
            letters = LOWERCASE_VOWELS + LOWERCASE_CONSONANTS;

        if (length < 1 || length > 9999)
            length = 1;

        for (int n = -1; ++n < length; ) {
            if (sb.length() >= 1) {
                previousLetter = sb.charAt(sb.length() - 1);

                if (sameType) {
                    do {
                        currentLetter = getAChar(letters);
                        allowed = true;

                        if (previousLetter == currentLetter) {
                            equal = true;
                            allowed = randomizer.getBoolean();
                        } else
                            equal = false;
                    }
                    while ((vowelFound = isVowel(previousLetter)) != isVowel(currentLetter) || ((!vowelFound || n == length - 1) && equalsAny(currentLetter, "ñç")) || (equal && isNonConsecutiveConsonant(currentLetter)) || !allowed);
                } else {
                    do {
                        currentLetter = getAChar(letters);
                    }
                    while ((vowelFound = isVowel(previousLetter)) == (anotherVowelFound = isVowel(currentLetter)) || ((!vowelFound || n == length - 1) && equalsAny(currentLetter, "ñç")) || (equalsAny(previousLetter, "ñç") && !anotherVowelFound));
                }
            }
            sb.append(currentLetter);

            if (randomizer.getFloat() <= approvalRate[count] && !equalsAny(currentLetter, "ñç")) {
                count++;
                sameType = true;
            } else {
                count = 0;
                sameType = false;
            }
        }
        s = sb.toString();

        if (!hasVowels(s)) {
            char[] c = s.toCharArray();
            c[randomizer.getInt(0, length)] = getAChar(LOWERCASE_VOWELS);
            s = String.valueOf(c);
        }
        return capitalizeFirst(s);
    }

    public String generateName(int iterations) {
        StringBuilder sb = new StringBuilder();

        if (iterations < 1 || iterations > 100)
            iterations = 1;

        if (randomizer.getInt(0, 3) == 0)
            sb.append(getVowels());
        sb.append(randomizer.getBoolean() ? getStringFromStringArray(CONSONANT_PAIRS) : getAChar(LOWERCASE_REPEATED_CONSONANTS));
        sb.append(getVowels());

        for (int i = 1; i < iterations; i++) {
            float probability = randomizer.getFloat();

            if (probability <= 0.7F)
                sb.append(getAChar(LOWERCASE_REPEATED_CONSONANTS));
            else if (probability <= 0.85F)
                sb.append(getStringFromStringArray(CONSONANT_PAIRS));
            else
                sb.append(getStringFromStringArray(MIDDLE_CONSONANTS));
            sb.append(getVowels());
        }

        if (randomizer.getInt(0, 5) == 0)
            sb.append(getAChar(LOWERCASE_ENDING_CONSONANTS));
        return capitalizeFirst(sb.toString());
    }

    private String generateName(boolean different, boolean uppercase, boolean dropped) {
        return generateName(different, uppercase, dropped, -1);
    }

    private String generateName(boolean different, boolean uppercase, boolean dropped, int type) {
        String name;

        if (type < 0)
            type = randomizer.getInt(0, GENERATED_NAME_FAMILY_NAME_SUFFIX.length);
        else if (type > GENERATED_NAME_FAMILY_NAME_SUFFIX.length - 1)
            type = 0;

        name = getStringFromStringArray(GENERATED_NAME_START[type]) + getStringFromStringArray(GENERATED_NAME_MIDDLE[type]) + getStringFromStringArray(GENERATED_NAME_ENDING[type]);

        if (different)
            name = removeDuplicates(name, Letter.VOWEL);

        if (dropped)
            name = dropLetters(name);
        return uppercase ? capitalizeFirst(name) : name;
    }

    public String getPreformedName(String letters, String type) {
        String name = "";
        int length = 3 + randomizer.getInt(0, 8);

        if (StringUtils.isNotBlank(letters) && letters.length() >= length) {
            int firstMark = randomizer.getInt(0, letters.length()), secondMark;

            if (firstMark + length - 1 > letters.length()) {
                secondMark = firstMark;
                firstMark = secondMark - length + 1;
            } else
                secondMark = firstMark + length - 1;
            name = StringUtils.substring(letters, firstMark, secondMark + 1);

            if (!hasVowels(name))
                name = getPreformedName(letters, type);
            else {
                name = randomizer.getBoolean() ? StringUtils.reverse(name) : name;
                name = fixLetters(name, type);
                name = capitalize(name);
            }
        }
        return name;
    }

    private String generateLastName(boolean different, boolean uppercase, boolean dropped) {
        return generateLastName(different, uppercase, dropped, -1);
    }

    private String generateLastName(boolean different, boolean uppercase, boolean dropped, int type) {
        String lastName;
        String suffix;

        if (type < 0)
            type = randomizer.getInt(0, GENERATED_NAME_FAMILY_NAME_SUFFIX.length);
        else if (type > GENERATED_NAME_FAMILY_NAME_SUFFIX.length - 1)
            type = 0;

        lastName = generateName(different, false, dropped, type);
        suffix = GENERATED_NAME_FAMILY_NAME_SUFFIX[type][randomizer.getInt(0, GENERATED_NAME_FAMILY_NAME_SUFFIX[type].length)];

        switch (type) {
            case 1:
                if (lastName.matches("^.*[aeiou]$") && suffix.matches("^[aeiou].*$"))
                    lastName = lastName.substring(0, lastName.length() - 1) + suffix;
                else
                    lastName = lastName + suffix;
                lastName = removeDuplicates(lastName, Letter.VOWEL);
                break;
            default:
                lastName = lastName + suffix;
                break;
        }

        if (dropped)
            lastName = dropLetters(lastName);
        return uppercase ? capitalizeFirst(lastName) : lastName;
    }

    private String[] generateFullName(boolean different, boolean dropped) {
        return generateFullName(different, dropped, -1);
    }

    private String[] generateFullName(boolean different, boolean dropped, int type) {
        if (type < 0)
            type = randomizer.getInt(0, GENERATED_NAME_FAMILY_NAME_SUFFIX.length);
        else if (type > GENERATED_NAME_FAMILY_NAME_SUFFIX.length - 1)
            type = 0;
        return new String[]{generateName(different, true, dropped, type), generateLastName(different, true, dropped, type)};
    }

    public String generateUsername() {
        Pseudonym[] pseudonyms = {
                Pseudonym.USERNAME,
                Pseudonym.USERNAME,
                Pseudonym.COMPOUND_USERNAME,
                Pseudonym.SPANISH_COMPOUND_USERNAME,
                Pseudonym.DERIVED_USERNAME
        };
        return generatePseudonym(pseudonyms[randomizer.getInt(0, pseudonyms.length)]);
    }

    public String generatePseudonym(Pseudonym pseudonym) {
        switch (pseudonym) {
            case USERNAME:
                return getUsername();
            case COMPOUND_USERNAME:
                return getCompositeUsername(Pseudonym.COMPOUND_USERNAME);
            case SPANISH_COMPOUND_USERNAME:
                return getCompositeUsername(Pseudonym.SPANISH_COMPOUND_USERNAME);
            case DERIVED_USERNAME:
                return getDerivedUsername();
            case ANONYMOUS_NAME:
                return getNickname(Pseudonym.ANONYMOUS_NAME);
            case SPANISH_ANONYMOUS_NAME:
                return getNickname(Pseudonym.SPANISH_ANONYMOUS_NAME);
        }
        return "";
    }

    private String getCompositeUsername(Pseudonym pseudonym) {
        String username = "";
        String base = "";
        int separatorType = randomizer.getInt(0, SEPARATOR.length - 1);

        //Get base username with a noun and an adjective
        if (pseudonym == Pseudonym.COMPOUND_USERNAME) {
            base = getEnglishAdjective() + " " + getCommonNoun();
            base = RegExUtils.replaceAll(base, "[^a-zA-Z0-9\\s]", " ");
        } else if (pseudonym == Pseudonym.SPANISH_COMPOUND_USERNAME) {
            Noun noun = getSpanishNoun();
            base = noun.getNoun() + " " + getSpanishAdjective(noun.getArticle().getSex(), noun.getArticle().isPlural());
            base = normalize(base);
        }
        base = base.trim();

        //Separate words with characters or using camel case
        if (StringUtils.isBlank(base)) {
        } else if (randomizer.getBoolean()) {
            base = capitalize(base);
            base = StringUtils.remove(base, " ");
        } else
            base = StringUtils.replace(base, " ", SEPARATOR[separatorType]);
        username = base;

        //Append number, if required
        if (StringUtils.isBlank(username)) {
        } else if (randomizer.getBoolean()) {
            username = username + (username.contains(SEPARATOR[separatorType]) ? SEPARATOR[separatorType] : "");
            float probability = randomizer.getFloat();

            if (probability <= 0.45F) {
                int number = randomizer.getInt(0, 1000);
                username = username + (randomizer.getBoolean() ? String.format("%03d", number) : number);
            } else if (probability <= 0.9F) {
                int year = getYear();
                int difference = randomizer.getInt(0, 201);

                if (difference < 0)
                    year = year - difference;
                else
                    year = year + difference;

                if (year < 1)
                    year = 2000;
                username = username + year;
            } else {
                String[] numbers = {"0", "002", "007", "2", "69", "69", "69", "666", "777", "420", "420", "420", "911", "999"};
                username += numbers[randomizer.getInt(0, numbers.length)];
            }
        }
        return username;
    }

    private String getDerivedUsername() {
        String username = "";
        String appendix = getFamilyName();
        appendix = normalize(appendix);
        appendix = RegExUtils.replaceAll(appendix, "[^a-zA-Z]", "");
        username = Character.toString(getAChar(UPPERCASE_ALPHABET));

        if (appendix.length() > 4)
            username += StringUtils.substring(appendix, 0, 5);
        else
            username += appendix;
        username += randomizer.getInt(0, 101);
        return username;
    }

    private String getNickname(Pseudonym pseudonym) {
        String nickname = "";

        if (pseudonym == Pseudonym.ANONYMOUS_NAME)
            nickname = getEnglishAdjective() + " " + getCommonNoun();
        else if (pseudonym == Pseudonym.SPANISH_ANONYMOUS_NAME) {
            Noun noun = getSpanishNoun();
            nickname = noun.getNoun() + " " + getSpanishAdjective(noun.getArticle().getSex(), noun.getArticle().isPlural());
        }
        return nickname;
    }

    private String getVowels() {
        if (randomizer.getInt(0, 10) > 0)
            return Character.toString(getAChar(LOWERCASE_VOWELS));
        else {
            String vowels = getStringFromStringArray(VOWEL_PAIRS);

            if (vowels.length() == 1 && !isGlyphDisplayable(vowels.charAt(0)))
                vowels = StringUtils.replaceEach(vowels, new String[]{"æ", "œ",}, new String[]{"ae", "oe"});
            return vowels;
        }
    }

    private String dropLetters(String s) {
        s = MULTIPLE_CONSONANT_PATTERN.matcher(s).replaceAll("$2");
        return s;
    }

    private String fixLetters(String s, String type) {
        if (StringUtils.isBlank(s))
            return s;

        switch (type) {
            case "standard":
                s = TRIPLE_CONSONANT_PATTERN.matcher(s).replaceAll("$1" + getAChar(LOWERCASE_VOWELS) + "$2");
                break;
            case "common":
                s = MULTIPLE_VOWEL_PATTERN.matcher(s).replaceAll("$1" + getAChar(LOWERCASE_CONSONANTS) + "$2");

                if (DOUBLE_CONSONANT_AND_VOWEL_START_PATTERN.matcher(s).matches())
                    s = StringUtils.startsWithAny(s, CONSONANT_PAIRS) ? s : CONSONANT_PAIRS[randomizer.getInt(0, CONSONANT_PAIRS.length)] + StringUtils.substring(s, 2);
                break;
            case "japanese_romanization":
                s = StringUtils.replaceEach(s, new String[]{"aa", "ii", "uu", "ei", "ee", "ou", "oo"}, new String[]{"ā", "ī", "ū", "ē", "ē", "ō", "ō"});
                s = removeDuplicates(s, Letter.VOWEL);
                break;
            case "old_english":
                s = TRIPLE_CONSONANT_PATTERN.matcher(s).replaceAll("$1" + getAChar(LOWERCASE_VOWELS + "æ") + "$2");

                if (DOUBLE_CONSONANT_AND_VOWEL_START_PATTERN.matcher(s).matches()) {
                    boolean matching = false;

                    for (int n = 0; n < ANY_CONSONANT_PAIRS.length; n++) {
                        if (StringUtils.startsWith(s, ANY_CONSONANT_PAIRS[n]))
                            matching = true;
                    }

                    if (!matching)
                        s = ANY_CONSONANT_PAIRS[randomizer.getInt(0, ANY_CONSONANT_PAIRS.length)] + StringUtils.substring(s, 2);
                }
                break;
            case "spanish":
                s = TRIPLE_CONSONANT_PATTERN.matcher(s).replaceAll("$1" + getAChar(LOWERCASE_VOWELS) + "$2");

                if (DOUBLE_CONSONANT_AND_VOWEL_START_PATTERN.matcher(s).matches())
                    s = StringUtils.startsWithAny(s, CONSONANT_PAIRS) ? s : CONSONANT_PAIRS[randomizer.getInt(0, CONSONANT_PAIRS.length)] + StringUtils.substring(s, 2);
                s = StringUtils.endsWith(s, "ñ") ? StringUtils.removeEnd(s, "ñ") + "n" : s;
                break;
            default:
                s = normalize(s);
                break;
        }
        return s;
    }

    public String removeDuplicates(String s, Letter letter) {
        switch (letter) {
            case ANY_CHARACTER:
                return s.replaceAll("(.)\\1{1,}", "$1");
            case JAVA_LETTER:
                return s.replaceAll("(\\p{javaLetter})\\1{1,}", "$1"); //Removes any duplicate where Character.isLetter(c) is true
            case UNICODE_LETTER:
                return s.replaceAll("(\\p{L})\\1{1,}", "$1"); //Removes duplicate Unicode letters
            case ASCII_LETTER:
                return s.replaceAll("(\\p{Alpha})\\1{1,}", "$1"); //Removes duplicate ASCII letters
            case VOWEL:
                return s.replaceAll("([aeiouAEIOU])\\1{1,}", "$1"); //Remove duplicate vowels
            case CONSONANT:
                return s.replaceAll("([a-zA-Z&&[^aeiouAEIOU]])\\1{1,}", "$1"); //Remove duplicate consonants
            default:
                return s;
        }
    }

    private String getHonoraryTitle(HonoraryTitle honoraryTitle, int sex) {
        String temp = "";

        if (sex < -1 || sex > 2)
            sex = randomizer.getInt(-1, 4);

        switch (honoraryTitle) {
            case TITLE:
                return genderify(getString(R.string.title, getSplitString(R.string.title_descriptor), getSplitString(R.string.title_level), getSplitString(R.string.title_job)), sex).getText();
            case CLASS:
                return genderify(getString(R.string.class_component, getSplitString(R.string.classes), randomizer.getInt(1, 99)), sex).getText();
            case HONORIFIC:
                return temp = getSplitString(R.string.honorifics) == "∅" ? "" : genderify(temp, sex).getText();
            case ROYAL_TITLE:
                return temp = getSplitString(R.string.royal_titles) == "∅" ? "" : genderify(temp, sex).getText();
            case OCCUPATION:
                return replaceTags("{method:getOccupation}", sex).getText();
        }
        return "";
    }

    private String getNetworkName() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = null;

        if (wifiManager != null)
            wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED)
            return wifiInfo.getSSID();
        return "";
    }

    public String getNetworkOperator() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if ((manager != null ? manager.getSimState() : 0) == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkOperatorName();
        return "";
    }

    public String getNetworkCountry() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if ((manager != null ? manager.getSimState() : 0) == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkCountryIso();
        return "";
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();

                    if (!inetAddress.isLoopbackAddress())
                        return inetAddress.getHostAddress();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTestDeviceId() {
        return MD5(getDeviceId()).toUpperCase();
    }

    public String getDeviceId() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (StringUtils.isBlank(androidId) || androidId.length() <= 4)
            return getDevicePseudoId();
        return StringUtils.overlay(androidId, StringUtils.repeat("*", androidId.length() - 4), 0, androidId.length() - 4);
    }

    public String getDevicePseudoId() {
        return getHexDigit(Build.BOARD.length()) +
                getHexDigit(Build.BOOTLOADER.length()) +
                getHexDigit(Build.BRAND.length()) +
                getHexDigit(Build.DEVICE.length()) +
                getHexDigit(Build.DISPLAY.length()) +
                getHexDigit(Build.FINGERPRINT.length()) +
                getHexDigit(Build.HARDWARE.length()) +
                getHexDigit(Build.HOST.length()) +
                getHexDigit(Build.ID.length()) +
                getHexDigit(Build.MANUFACTURER.length()) +
                getHexDigit(Build.MODEL.length()) +
                getHexDigit(Build.PRODUCT.length()) +
                getHexDigit(Build.TAGS.length()) +
                getHexDigit(Build.TYPE.length()) +
                getHexDigit(Build.USER.length());
    }

    private String getHexDigit(Integer value) {
        if (value == null || value < 0)
            return "0";
        return Integer.toHexString(value % 16);
    }

    public String generateDivination() {
        String divination = "?";
        float probability = randomizer.getFloat();
        int sex = -1;

        if (probability <= 0.4F) {
            divination = getDivination();
            divination = replaceTags(divination).getText();
        } else if (probability <= 0.8F) {
            int days = 0;
            boolean plural = false;
            boolean done = false;
            TextComponent component;
            String segment;

            //Define indices
            List<Integer> indices = new ArrayList<>();
            indices.add(randomizer.getInt(0, 15) > 0 ? randomizer.getInt(1, getArrayResLength(R.array.divination_start) - 1) : 0);
            indices.add(randomizer.getInt(0, getArrayResLength(R.array.divination_middle)));
            indices.add(randomizer.getInt(0, getArrayResLength(R.array.divination_end) - (indices.get(1) > 0 ? 0 : 10)));
            indices.add(getArrayResLength(R.array.divination_cause) - 1 - indices.get(2) <= 1 ? 0 : randomizer.getInt(0, getArrayResLength(R.array.divination_cause)));

            //Get segments for the divination
            List<String> segments = new ArrayList<>();
            segments.add(getStringFromStringArray(R.array.divination_start, indices.get(0)));
            segments.add(getStringFromStringArray(R.array.divination_middle, indices.get(1)));
            segments.add(getStringFromStringArray(R.array.divination_end, indices.get(2)));
            segments.add(getStringFromStringArray(R.array.divination_cause, indices.get(3)));

            //Format start of divination
            segment = StringUtils.replaceOnce(segments.get(0), "‽1", String.valueOf(days = randomizer.getGaussianInt(5, 7, 1)));
            segment = StringUtils.replaceOnce(segment, "‽2", addDaysToDate(preferences.getString("temp_enquiryDate", SimpleDate.UNKNOWN_DATE), days));
            segments.set(0, segment);
            preferences.remove("temp_enquiryDate");

            //Format middle of divination
            segment = (component = replaceTags(segments.get(1))).getText();

            if (component.getHegemonicSex() != null && component.getHegemonicSex() != -1)
                sex = component.getHegemonicSex();

            if (StringUtils.startsWithAny(segment, "tus", "los", "las"))
                plural = true;
            segments.set(1, segment);

            //Format end of divination
            segment = segments.get(2);
            segment = StringUtils.replace(segment, "＃", String.valueOf(sex));
            segment = replaceTags(segment).getText();

            if (getString(R.string.locale).equals("es"))
                segment = !plural ? segment.replaceAll("¦\\p{L}+¦", "") : StringUtils.replace(segment, "¦", "");
            segments.set(2, segment);

            //Format cause of divination
            segment = segments.get(3);

            if (!segment.isEmpty()) {
                segment = replaceTags(segment).getText();

                if (segment.contains("%%"))
                    segment = StringUtils.replace(segment, "%%", replaceTags("{string:unspecific_person⸻⸮}").getText());
                segment = StringUtils.replace(segment, " a el ", " al ");
            }
            segments.set(3, segment);

            //Make some replacements and truncate divination if needed
            for (int n = 0, size = segments.size(); n < size; n++) {
                if (done)
                    segments.set(n, "");
                else {
                    segment = segments.get(n);

                    if (StringUtils.endsWith(segment, "꘎")) {
                        segment = StringUtils.remove(segment, '꘎');
                        done = true;
                    }
                    segments.set(n, segment);
                }
            }
            segments.removeAll(Collections.singleton(""));
            divination = StringUtils.join(segments, " ");
        } else {
            divination = replaceTags(getStringFromStringArray(R.array.divination)).getText();
            divination = StringUtils.replace(divination, " de el ", " del ");
        }
        return divination;
    }

    public String getEmotions() {
        String s = "";
        String currentEmotion;
        String[] emotions = getResources().getStringArray(R.array.emotions);
        HashMap<String, Pair<Integer, Integer>> hashMap = new HashMap<>();
        Pair<Integer, Integer> pair;
        int points, remainingPoints = 100;

        for (int n = -1, length = emotions.length; ++n < length; ) {
            hashMap.put(emotions[n], new Pair<>(n, 0));
        }

        while (remainingPoints > 0) {
            points = randomizer.getInt(0, remainingPoints + 1);
            remainingPoints -= points;
            pair = hashMap.get(currentEmotion = emotions[randomizer.getInt(0, emotions.length)]);
            pair.setSubValue(pair.getSubValue() + points);
            hashMap.put(currentEmotion, pair);
        }

        for (Map.Entry<String, Pair<Integer, Integer>> entry : hashMap.entrySet()) {
            if (entry.getValue().getSubValue() > 0)
                s += (s.isEmpty() ? "" : "<br>") + getIdeogramByUnicode(getIntFromIntArray(R.array.feelings, entry.getValue().getSubKey())) + " " + entry.getKey() + " (" + entry.getValue().getSubValue() + "%)";
        }
        return s;
    }

    public String generateTime() {
        SimpleTime time = randomizer.getTime();
        return String.format("%02d", time.getHour()) + ":" + String.format("%02d", time.getMinute());
    }

    public String generatePercentage() {
        return randomizer.getInt(0, 101) + "%";
    }

    public String getChineseZodiacSign(int year) {
        return getStringFromStringArray(R.array.chinese_zodiac_sign, year % 12);
    }

    public String getChainOfEvents(String name, boolean user) {
        String chain = "";
        List<Object[]> people = new ArrayList<>();
        int listSize;
        int arrayLength = getArrayResLength(R.array.person);
        int typeOfSecondPerson = randomizer.getInt(1, arrayLength - 1);
        int typeOfThirdPerson = typeOfSecondPerson <= 9 ? randomizer.getInt(11, arrayLength - 11) : randomizer.getInt(1, arrayLength - 1);
        TextComponent closestPerson = new TextComponent();
        TextComponent thirdParty;
        char letter = 'A';

        if (contactNames != null)
            listSize = contactNames.size();
        else
            listSize = 0;

        if (listSize < 0)
            listSize = 0;
        else if (listSize > 1000)
            listSize = 1000;
        double probability = 0.1F + (0.5F / 1000 * listSize);

        if (user && randomizer.getFloat() <= probability && listSize > 0) {
            closestPerson.setText(getContactName(true));
            closestPerson.setHegemonicSex(-1);

            if (closestPerson.getText().isEmpty())
                closestPerson.setText("?");
        } else {
            closestPerson = replaceTags("{string:person[" + typeOfSecondPerson + "]⸻⸮}");
            closestPerson.setText(StringUtils.replace(closestPerson.getText(), "%%", name));
        }
        thirdParty = replaceTags("{string:person[" + typeOfThirdPerson + "]⸻⸮}");
        thirdParty.setText(StringUtils.replace(thirdParty.getText(), "%%", closestPerson.getText()));
        thirdParty.setText(StringUtils.replace(thirdParty.getText(), " de el ", " del "));

        //Add unknown people
        for (int n = -1, limit = PROBABILITY_DISTRIBUTION[randomizer.getInt(0, PROBABILITY_DISTRIBUTION.length)]; ++n < limit; ) {
            people.add(new Object[]{
                    getStringFromStringArray(R.array.person, 0) + " " + "<font color=" + COMMON_COLORS[n] + ">" + formatText(Character.toString(letter), "b,i,tt") + "</font>",
                    getStringFromStringArray(R.array.probability) + " " + genderify(getStringFromStringArray(R.array.individual), randomizer.getInt(1, 2)).getText(),
                    true
            });
            letter++;
        }

        //Add identified people
        for (int n = -1, limit = randomizer.getInt(3, 8); ++n < limit; ) {
            if (randomizer.getInt(0, 4) > 0) {
                Entity entity = getEntity(supportedNames);
                people.add(new Object[]{
                        "<font color=" + getColorString() + ">" + entity.getFormattedFullName() + "</font>",
                        sexes[entity.getSex() + 1],
                        false
                });
            } else
                people.add(new Object[]{
                        "<font color=" + getColorString() + ">" + formatText(generateUsername(), "b,tt") + "</font>",
                        sexes[0],
                        false
                });
        }

        //Add close people
        people.add(new Object[]{thirdParty.getText(), sexes[thirdParty.getHegemonicSex() + 1], false});
        people.add(new Object[]{closestPerson.getText(), sexes[closestPerson.getHegemonicSex() + 1], false});

        //Add oneself
        people.add(new Object[]{name, "", false});

        //Calculate karma
        int totalKarma = 0;

        //Format descriptions
        String[] descriptions = new String[people.size()];

        for (int n = -1, size = people.size(); ++n < size; ) {
            descriptions[n] = String.valueOf(people.get(n)[1]);

            if (!descriptions[n].isEmpty()) {
                if (descriptions[n].length() == 1)
                    descriptions[n] = "<font color=#B599FC>" + formatText(descriptions[n], "b") + "</font>";
                descriptions[n] = " " + "(" + descriptions[n] + ")";
            }
        }

        //Define chains
        for (int n = 0; n < people.size() - 1; n++) {
            int karma = randomizer.getInt(-10, 21);
            totalKarma = totalKarma + karma;

            if ((Boolean) people.get(n)[2])
                chain += getString(R.string.chain_link, n + 1, people.get(n)[0] + descriptions[n], ",", getStringFromStringArray(R.array.uncertainty), people.get(n + 1)[0] + descriptions[n + 1], formatNumber(karma), formatNumber(totalKarma));
            else
                chain += getString(R.string.chain_link, n + 1, people.get(n)[0] + descriptions[n], "", "(" + getAction() + ")", people.get(n + 1)[0] + descriptions[n + 1], formatNumber(karma), formatNumber(totalKarma));

            //Define link delimiter
            if (isPrintable('⬇') && !isCharacterMissingInFont('⬇'))
                chain += "<br><font color=#A0A8C7>⬇</font><br>";
            else
                chain += "<br>";
        }
        float percentage = (float) Math.abs(totalKarma) / (people.size() - 1) * 10;
        String effect;

        if (totalKarma == 0)
            effect = getStringFromRes(R.string.chain_null_effect);
        else if (totalKarma < 0)
            effect = getStringFromRes(R.string.chain_negative_effect);
        else
            effect = getStringFromRes(R.string.chain_positive_effect);
        chain += getString(R.string.chain_effect, "<b><font color=#FFF870>" + (totalKarma == 0 ? effect : String.format("%.2f", percentage) + "% " + effect) + "</font></b>", name);
        chain = StringUtils.replace(chain, " a el ", " al ");
        return getString(R.string.html_format, chain);
    }

    /* String methods :: String retrieval */

    public String getLineFromRaw(@RawRes int id) {
        String s = "";

        if (isResource(id) && getResources().getResourceTypeName(id).equals("raw")) {
            InputStream is = getResources().openRawResource(id);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            LineNumberReader lnr = new LineNumberReader(br);
            Integer lineCount = 0;

            if (countRegistry.containsKey(id))
                lineCount = countRegistry.get(id);
            else {
                try {
                    lnr.skip(Long.MAX_VALUE);
                    lineCount = lnr.getLineNumber() + 1;
                    countRegistry.put(id, lineCount);
                    lnr.close();
                    lnr = null;
                } catch (IOException ignored) {
                }
            }

            if (lineCount != null && lineCount > 0) {
                try {
                    for (int x = -1, limit = randomizer.getInt(1, lineCount); ++x < limit; ) {
                        br.readLine();
                    }
                    s = br.readLine();
                    br.close();
                    br = null;
                } catch (IOException ignored) {
                }
            }
        }
        return s;
    }

    public String getRawString(@RawRes int id) {
        if (isResource(id) && getResources().getResourceTypeName(id).equals("raw")) {
            try {
                InputStream is = getResources().openRawResource(id);

                byte[] b = new byte[is.available()];
                is.read(b);
                is.close();
                is = null;
                return new String(b);
            } catch (IOException ignored) {
            }
        }
        return "";
    }

    public String getRawStringByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "raw", getPackageName());

                if (resourceId != 0)
                    return getRawString(resourceId);
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    public String getStringFromRes(@StringRes int id) {
        if (id != 0) {
            try {
                return getString(id);
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    public String getStringFromResByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "string", getPackageName());

                if (resourceId != 0)
                    return getString(resourceId);
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    public String[] getStringArrayFromResByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "array", getPackageName());

                if (resourceId != 0)
                    return getResources().getStringArray(resourceId);
            } catch (Exception ignored) {
            }
        }
        return new String[]{};
    }

    public String getSplitString(@StringRes int id) {
        return getSplitString(id, -1);
    }

    public String getSplitString(@StringRes int id, int index) {
        List<String> list = getStringAsList(id);

        try {
            if (index < 0)
                index = randomizer.getInt(0, list.size());
            else if (index >= list.size())
                index = 0;
            return list.get(index);
        } catch (Exception e) {
            return "";
        }
    }

    public String[] getStringAsArray(@StringRes int id) {
        String s = getStringFromRes(id);

        if (StringUtils.isNotBlank(s))
            return s.split("¶[ ]*");
        return new String[]{""};
    }

    public List<String> getStringAsList(@StringRes int id) {
        return Arrays.asList(getStringAsArray(id));
    }

    public String getStringFromList(List<String> list) {
        return getStringFromList(list, -1);
    }

    public String getStringFromList(List<String> list, int index) {
        if (list == null || list.size() > 0) {
            if (index < 0)
                index = randomizer.getInt(0, list.size());
            else if (index >= list.size())
                index = 0;
            return list.get(index);
        }
        return "";
    }

    public String getStringFromStringArray(String[] array) {
        if (array != null && array.length > 0) {
            int index = randomizer.getInt(0, array.length);
            return array[index];
        }
        return "";
    }

    public String getStringFromStringArray(@ArrayRes int id) {
        int length = getArrayResLength(id);
        return getStringFromStringArray(id, randomizer.getInt(0, length));
    }

    public String getStringFromStringArray(@ArrayRes int id, int index) {
        if (getResourceName(id) != null) {
            String[] stringArray = getResources().getStringArray(id);

            if (index < 0 || index >= stringArray.length)
                index = 0;
            return stringArray[index];
        }
        return "";
    }

    public String getResourceName(@AnyRes int id) {
        return getResources().getResourceName(id);
    }

    public String getColorString() {
        return getStringFromList(colors);
    }

    /* String methods :: Database retrieval */

    public String getAbstractNoun() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectAbstractNoun(randomizer.getInt(1, myDB.countAbstractNouns()));
            case "en":
                return myDB.selectEnglishAbstractNoun(randomizer.getInt(1, myDB.countEnglishAbstractNouns()));
            default:
                return "";
        }
    }

    public String getAction() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectAction(randomizer.getInt(1, myDB.countActions()));
            case "en":
                return myDB.selectEnglishAction(randomizer.getInt(1, myDB.countEnglishActions()));
            default:
                return "";
        }
    }

    public String getAdjective() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectSingularAdjective(randomizer.getInt(1, myDB.countActions()));
            case "en":
                return myDB.selectEnglishAdjective(randomizer.getInt(1, myDB.countEnglishActions()));
            default:
                return "";
        }
    }

    public String getDivination() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectDivination(randomizer.getInt(1, myDB.countDivinations()));
            case "en":
                return myDB.selectEnglishDivination(randomizer.getInt(1, myDB.countEnglishDivinations()));
            default:
                return "";
        }
    }

    public String getFortuneCookie() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectFortuneCookie(randomizer.getInt(1, myDB.countFortuneCookies()));
            case "en":
                return myDB.selectEnglishFortuneCookie(randomizer.getInt(1, myDB.countEnglishFortuneCookies()));
            default:
                return "";
        }
    }

    public String getNoun() {
        switch (getString(R.string.locale)) {
            case "es":
                Noun noun = getSpanishNoun();
                return randomizer.getBoolean() ? noun.getNounWithArticle() : noun.getNounWithIndefiniteArticle();
            case "en":
                return myDB.selectEnglishNoun(randomizer.getInt(1, myDB.countEnglishNouns()));
            default:
                return "";
        }
    }

    public String getOccupation() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectOccupation(randomizer.getInt(1, myDB.countOccupations()));
            case "en":
                return myDB.selectEnglishOccupation(randomizer.getInt(1, myDB.countEnglishOccupations()));
            default:
                return "";
        }
    }

    public String getPhrase() {
        switch (getString(R.string.locale)) {
            case "es":
                return myDB.selectPhrase(randomizer.getInt(1, myDB.countPhrases()));
            case "en":
                return myDB.selectEnglishPhrase(randomizer.getInt(1, myDB.countEnglishPhrases()));
            default:
                return "";
        }
    }

    public String getEnglishWord() {
        return myDB.selectEnglishWord(randomizer.getInt(1, myDB.countEnglishPhonetics()));
    }

    public String getEnglishPhoneticScript(String word) {
        return myDB.selectEnglishPhoneticScript(word);
    }

    private String getName() {
        return myDB.selectName(randomizer.getInt(1, myDB.countNames()));
    }

    private String getMiddleName() {
        return getAChar(UPPERCASE_ALPHABET) + FULL_STOP;
    }

    private String getFamilyName() {
        return myDB.selectFamilyName(randomizer.getInt(1, myDB.countFamilyNames()));
    }

    private String getMaleName() {
        return myDB.selectMaleName(randomizer.getInt(1, myDB.countMaleNames()));
    }

    private String getFemaleName() {
        return myDB.selectFemaleName(randomizer.getInt(1, myDB.countFemaleNames()));
    }

    private String getLastName() {
        return myDB.selectSurname(randomizer.getInt(1, myDB.countSurnames()));
    }

    private String getEnglishMaleName() {
        return myDB.selectEnglishMaleName(randomizer.getInt(1, myDB.countEnglishMaleNames()));
    }

    private String getEnglishFemaleName() {
        return myDB.selectEnglishFemaleName(randomizer.getInt(1, myDB.countEnglishFemaleNames()));
    }

    private String getEnglishLastName() {
        return myDB.selectEnglishSurname(randomizer.getInt(1, myDB.countEnglishSurnames()));
    }

    private String getUsername() {
        return myDB.selectUsername(randomizer.getInt(1, myDB.countUsernames()));
    }

    private String getCommonNoun() {
        String noun = myDB.selectCommonNoun(randomizer.getInt(1, myDB.countCommonNouns()));

        if (randomizer.getBoolean() && Pluralize.isSingular(noun))
            return Pluralize.plural(noun);
        return noun;
    }

    private String getEnglishAdjective() {
        return myDB.selectEnglishAdjective(randomizer.getInt(1, myDB.countEnglishAdjectives()));
    }

    private String getSpanishAdjective(int sex, boolean plural) {
        String adjective = "";
        int tries = 0;
        boolean proper = false;

        if (sex < 0 || sex > 2)
            sex = 0;

        while (!proper) {
            if (plural)
                adjective = myDB.selectPluralAdjective(randomizer.getInt(1, myDB.countPluralAdjectives()));
            else
                adjective = myDB.selectSingularAdjective(randomizer.getInt(1, myDB.countSingularAdjectives()));

            if (sex == 0 && adjective.contains("AQ0C"))
                proper = true;
            else if (sex == 1 && StringUtils.containsAny(adjective, "AQ0C", "AQ0M", "AO0M", "AQSM"))
                proper = true;
            else if (sex == 2 && StringUtils.containsAny(adjective, "AQ0C", "AQ0F", "AO0F", "AQSF"))
                proper = true;
            else if (sex == 1 && StringUtils.endsWith(adjective, "a")) {
                adjective = StringUtils.substringBeforeLast(adjective, "a") + "o";
                proper = true;
            } else if (sex == 2 && StringUtils.endsWith(adjective, "o")) {
                adjective = StringUtils.substringBeforeLast(adjective, "o") + "a";
                proper = true;
            } else {
                if (tries > 9999)
                    proper = true;
            }
            tries++;
        }
        adjective = adjective.replaceFirst("\\s*(A[OQ][S0][CFM][A-Z\\d]{2})", "");
        return adjective;
    }

    private Noun getSpanishNoun() {
        String noun = myDB.selectNoun(randomizer.getInt(1, myDB.countNouns()));
        String temp;
        List<Article> articles = Arrays.asList(Article.values());
        int position = 5;

        for (int n = -1, length = articles.size(); ++n < length - 1; ) {
            if (StringUtils.startsWith(noun, articles.get(n).getArticle() + " ")) {
                position = n;
                temp = RegExUtils.removeFirst(noun, articles.get(n).getArticle() + "\\s+");

                if (n == 0 && StringUtils.startsWithAny(temp, "a", "á") && StringUtils.endsWith(temp, "a"))
                    articles.get(n).setSex(2);
                noun = temp;
            }
        }
        noun = noun.trim();
        return new Noun(articles.get(position), noun);
    }

    /* String methods :: Formatting */

    public String normalize(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String capitalizeFirst(String s) {
        if (StringUtils.isNotEmpty(s)) {
            if (Character.isLowerCase(s.charAt(0))) {
                char c = Character.toUpperCase(s.charAt(0));

                if (Character.isLetter(c))
                    return c + s.substring(1);
            }
            return s;
        }
        return "";
    }

    public String capitalize(String s) {
        if (StringUtils.isNotEmpty(s)) {
            String[] parts = StringUtils.split(s, " ");

            for (int n = 0; n < parts.length; n++) {
                parts[n] = capitalizeFirst(parts[n]);
            }
            s = StringUtils.joinWith(" ", parts);
            return s;
        }
        return s;
    }

    public String swapFirstToLowercase(String s) {
        if (StringUtils.isNotEmpty(s)) {
            if (Character.isUpperCase(s.charAt(0))) {
                char c = Character.toLowerCase(s.charAt(0));

                if (Character.isLetter(c))
                    return c + s.substring(1);
            }
            return s;
        }
        return "";
    }

    public String formatEnglishArticles(String s) {
        if (StringUtils.isNotBlank(s)) {
            Matcher matcher = INDEFINITE_ARTICLE_PATTERN.matcher(s);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String word = matcher.group(3);
                String article = "a/an";
                String script = getEnglishPhoneticScript(word.toUpperCase());

                if (StringUtils.isNotBlank(script) && !script.equals(DatabaseConnection.DEFAULT_VALUE)) {
                    for (char c : script.toCharArray()) {
                        if (c == 'ˌ' || c == 'ˈ')
                            continue;

                        if ("æɑɒʌɛɪiɔʊuəɚɜɝ".indexOf(c) >= 0)
                            article = "an";
                        else
                            article = "a";
                        break;
                    }
                } else
                    article = IndefiniteArticle.get(word);

                if (matcher.group(1).charAt(0) == 'A')
                    article = capitalize(article);
                matcher.appendReplacement(sb, article + matcher.group(2) + matcher.group(3));
            }
            matcher.appendTail(sb);
            s = sb.toString();
        }
        return s;
    }

    public TextComponent replaceTags(String s) {
        return replaceTags(s, -2);
    }

    public TextComponent replaceTags(String s, int predefinedSex) {
        if (StringUtils.isBlank(s))
            return new TextComponent();
        else {
            TextComponent component = new TextComponent();
            Integer sex = null;
            int defaultSex;
            boolean defaulted = false;
            boolean nullified = false;

            if (defaulted = (predefinedSex >= -1))
                defaultSex = predefinedSex;
            else
                defaultSex = randomizer.getInt(-1, 4);

            //Verifies if the text contains curly brackets
            if (StringUtils.containsAny(s, "{", "}")) {
                int pairsOfBrackets = 0;
                int counter = 0;

                //Counts the possible pairs of curly brackets within the text
                for (int n = -1, length = s.length(); ++n < length; ) {
                    boolean concluded = false;

                    if (s.charAt(n) == '{')
                        counter++;
                    else if (s.charAt(n) == '}') {
                        counter--;
                        concluded = true;
                    }

                    if (counter < 0) {
                        pairsOfBrackets = 0;
                        break;
                    } else if (concluded)
                        pairsOfBrackets++;
                }
                Matcher matcher;

                //Replaces simple tags within the text, if there are any
                while (pairsOfBrackets > 0 && (matcher = TAG_PATTERN.matcher(s)).find()) {
                    String replacement = "";
                    String resourceName = matcher.group().startsWith("{method:") ? StringUtils.substringBetween(matcher.group(), ":", "}") : matcher.group(3);
                    String resourceType;
                    sex = getTrueSex(matcher.group(), defaultSex);
                    Integer index = null;
                    boolean empty = false;

                    try {
                        Matcher indexMatcher = Pattern.compile("\\[!?\\d+\\]").matcher(matcher.group());

                        if (indexMatcher.find()) {
                            String match = RegExUtils.removeAll(indexMatcher.group(), "[^\\d]");
                            index = Integer.valueOf(match);
                        }
                    } catch (Exception e) {
                        index = -1;
                    }

                    if (StringUtils.startsWith(matcher.group(), "{string:")) {
                        int resourceId = getArrayResourceId(resourceName);
                        resourceId = (resourceId == 0 ? getStringResourceId(resourceName) : resourceId);

                        if (resourceId != 0) {
                            if ((resourceType = getResources().getResourceTypeName(resourceId)).equals("array"))
                                replacement = index != null && index >= 0 ? getStringFromStringArray(resourceId, index) : getStringFromStringArray(resourceId);
                            else if (resourceType.equals("string"))
                                replacement = index != null && index >= 0 ? getSplitString(resourceId, index) : getSplitString(resourceId);
                        }
                    } else if (StringUtils.startsWith(matcher.group(), "{database:"))
                        replacement = index != null && index >= 0 ? myDB.selectFromTable(resourceName, index) : myDB.selectFromTable(resourceName, randomizer.getInt(1, myDB.countTableRows(resourceName)));
                    else if (StringUtils.startsWith(matcher.group(), "{method:")) {
                        if ((replacement = callMethod(resourceName)) != null) {
                        } else if ((replacement = invokeMethod(resourceName)) != null) {
                        } else
                            replacement = "?";
                    }
                    int openingIndex = StringUtils.indexOf(replacement, '{');
                    int closingIndex;

                    if (openingIndex >= 0 && (closingIndex = StringUtils.indexOf(replacement, '}')) >= 0 && openingIndex < closingIndex) {
                        TextComponent tempComponent;

                        if (defaulted)
                            tempComponent = replaceTags(replacement, defaultSex);
                        else
                            tempComponent = replaceTags(replacement);
                        replacement = tempComponent.getText();
                        sex = tempComponent.getHegemonicSex();
                        empty = replacement.isEmpty() || (tempComponent.isNullified() && StringUtils.isBlank(replacement));
                    }

                    if (empty)
                        s = RegExUtils.replaceFirst(s, "\\s*" + Pattern.quote(matcher.group()), "");
                    else
                        s = StringUtils.replaceOnce(s, matcher.group(), genderify(replacement, sex).getText());
                    pairsOfBrackets--;
                }

                //Replaces ‘word’ tags within the text, if there are any
                while (pairsOfBrackets > 0 && (matcher = WORD_TAG_PATTERN.matcher(s)).find()) {
                    String replacement = matcher.group(1);
                    sex = getTrueSex(matcher.group(), defaultSex);
                    replacement = genderify(replacement, sex).getText();
                    s = StringUtils.replaceOnce(s, matcher.group(), replacement);
                    pairsOfBrackets--;
                }

                //Replaces ‘random’ tags within the text, if there are any
                while (pairsOfBrackets > 0 && (matcher = RANDOM_TAG_PATTERN.matcher(s)).find()) {
                    String substring = StringUtils.removeStart(matcher.group(), "{rand:");
                    substring = StringUtils.removeEnd(substring, "}");
                    List<String> items = Arrays.asList(substring.split("\\s*;\\s*"));

                    if (items != null && items.size() > 0) {
                        String replacement;
                        String regex;

                        if ((replacement = getStringFromList(items)).trim().equals("∅")) {
                            replacement = "";
                            regex = "\\s*" + Pattern.quote(matcher.group());
                            nullified = true;
                        } else
                            regex = Pattern.quote(matcher.group());
                        s = RegExUtils.replaceFirst(s, regex, replacement);
                        pairsOfBrackets--;
                    }
                }
            }
            Pair<String, List<Integer>> pair = replaceDigitTags(s);

            if (pair.getSubValue() != null && pair.getSubValue().size() > 0 && !s.equals(pair.getSubKey()))
                sex = pair.getSubValue().get(pair.getSubValue().size() - 1);
            s = pair.getSubKey();

            //Replaces subtags within the text, if there are any
            if (StringUtils.containsAny(s, "⸠", "⸡")) {
                s = StringUtils.replaceEach(s, new String[]{"⸠", "⸡"}, new String[]{"{", "}"});
                s = replaceTags(s, sex != null ? sex : defaultSex).getText();
            }
            component.setText(s);
            component.setHegemonicSex(sex);
            component.setNullified(nullified);
            return component;
        }
    }

    public Pair<String, List<Integer>> replaceDigitTags(String s) {
        if (s == null)
            return null;
        else {
            if (!s.isEmpty() && StringUtils.containsAny(s, "｢", "｣")) {
                List<Integer> matches = new ArrayList<>();
                Matcher matcher = SEX_PATTERN.matcher(s);

                while (matcher.find()) {
                    try {
                        matches.add(Integer.parseInt(RegExUtils.removeAll(matcher.group(), "[｢｣]")));
                    } catch (IndexOutOfBoundsException e) {
                        matches.add(null);
                    }
                }
                return new Pair<>(RegExUtils.removePattern(s, "｢[0-2]｣"), matches);
            } else return new Pair<>(s, null);
        }
    }

    public String fixDoubleFullStop(String s) {
        Matcher matcher = Pattern.compile(DOUBLE_FULL_STOP_REGEX).matcher(s);
        List<Integer> positions = new ArrayList<>();

        while (matcher.find()) {
            positions.add(matcher.end());
        }

        if (positions.size() > 0) {
            for (int n = positions.size() - 1; n >= 0; n--) {
                char[] charArray = s.toCharArray();
                charArray[positions.get(n) - 1] = '\0';
                s = String.valueOf(charArray);
            }
        }
        return s;
    }

    private int getTrueSex(String s, int defaultSex) {
        int definedSex;

        if (defaultSex < -1 || defaultSex > 2)
            defaultSex = -1;

        if (s.contains("⛌"))
            definedSex = getSex();
        else if (s.contains("⸮"))
            definedSex = randomizer.getInt(1, 2);
        else {
            Matcher matcher = SEX_APPENDIX_PATTERN.matcher(s);

            if (matcher.find()) {
                String match = StringUtils.replace(matcher.group(), "⸻", "");
                definedSex = Integer.parseInt(match);

                if (definedSex < 0 || definedSex > 2)
                    definedSex = 0;
            } else
                definedSex = defaultSex;
        }
        return definedSex;
    }

    public static String formatText(String text, String format) {
        Matcher matcher = FORMAT_PATTERN.matcher(format);

        if (StringUtils.isNotBlank(text) && matcher.find()) {
            List<String> parts;

            if (format.contains(","))
                parts = Arrays.asList(format.split(Pattern.quote(",")));
            else {
                parts = new ArrayList<>();
                parts.add(format);
            }

            if (parts.contains("a"))
                text = "<a>" + text + "</a>";

            if (parts.contains("b"))
                text = "<b>" + text + "</b>";

            if (parts.contains("big"))
                text = "<big>" + text + "</big>";

            if (parts.contains("i"))
                text = "<i>" + text + "</i>";

            if (parts.contains("s"))
                text = "<s>" + text + "</s>";

            if (parts.contains("small"))
                text = "<small>" + text + "</small>";

            if (parts.contains("tt"))
                text = "<tt>" + text + "</tt>";

            if (parts.contains("u"))
                text = "<u>" + text + "</u>";
        }
        return text;
    }

    private String formatNumber(Integer number) {
        if (number == null)
            number = 0;
        String formattedNumber;

        if (number == 0)
            formattedNumber = "<b><font color=#5E84EC>" + number + "</font></b>";
        else if (number < 0)
            formattedNumber = "<b><font color=#F94C4C>" + number + "</font></b>";
        else
            formattedNumber = "<b><font color=#2FCC2F>+" + number + "</font></b>";
        return formattedNumber;
    }

    private String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();

            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public String getColor(String seed) {
        String color;

        if (seed.equals("") || seed.isEmpty())
            color = String.format("#%06X", (0xFFFFFF & Color.GRAY));
        else {
            color = String.format("#%X", seed.hashCode());

            while (color.length() < 7)
                color = "#" + StringUtils.replaceOnce(color, "#", Character.toString(getAChar(HEX_DIGITS)));
            color = color.substring(0, 7);
        }
        return color;
    }

    /* char methods */

    boolean isLetter(char c) {
        return Character.isUpperCase(c) || Character.isLowerCase(c);
    }

    private char getAChar(String s) {
        if (s != null && s.length() > 0)
            return s.charAt(randomizer.getInt(0, s.length()));
        else
            return '\0';
    }

    /* int methods */

    public int getStringResourceId(String s) {
        return getResources().getIdentifier(s, "string", getPackageName());
    }

    public int getArrayResourceId(String s) {
        return getResources().getIdentifier(s, "array", getPackageName());
    }

    public int getSex() {
        return preferences.getInt("temp_sex", randomizer.getInt(0, 3));
    }

    public int getZodiacIndex(int month, int day) {
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18))
            return 1;
        else if ((month == 2 && day >= 19) || (month == 3 && day <= 20))
            return 2;
        else if ((month == 3 && day >= 21) || (month == 4 && day <= 20))
            return 3;
        else if ((month == 4 && day >= 21) || (month == 5 && day <= 21))
            return 4;
        else if ((month == 5 && day >= 22) || (month == 6 && day <= 20))
            return 5;
        else if ((month == 6 && day >= 21) || (month == 7 && day <= 22))
            return 6;
        else if ((month == 7 && day >= 23) || (month == 8 && day <= 22))
            return 7;
        else if ((month == 8 && day >= 23) || (month == 9 && day <= 22))
            return 8;
        else if ((month == 9 && day >= 23) || (month == 10 && day <= 22))
            return 9;
        else if ((month == 10 && day >= 23) || (month == 11 && day <= 21))
            return 10;
        else if ((month == 11 && day >= 22) || (month == 12 && day <= 21))
            return 11;
        else if ((month == 12 && day >= 22) || (month == 1 && day <= 19))
            return 12;
        else
            return 0;
    }

    public int getNewZodiacIndex(int month, int day) {
        if ((month == 2 && day >= 16) || (month == 3 && day <= 11))
            return 1;
        else if ((month == 3 && day >= 12) || (month == 4 && day <= 18))
            return 2;
        else if ((month == 4 && day >= 19) || (month == 5 && day <= 13))
            return 3;
        else if ((month == 5 && day >= 14) || (month == 6 && day <= 20))
            return 4;
        else if ((month == 6 && day >= 21) || (month == 7 && day <= 19))
            return 5;
        else if ((month == 7 && day >= 20) || (month == 8 && day <= 19))
            return 6;
        else if ((month == 8 && day >= 20) || (month == 9 && day <= 15))
            return 7;
        else if ((month == 9 && day >= 16) || (month == 10 && day <= 30))
            return 8;
        else if ((month == 10 && day >= 31) || (month == 11 && day <= 22))
            return 9;
        else if ((month == 11 && day >= 23) || (month == 11 && day <= 29))
            return 10;
        else if ((month == 11 && day >= 30) || (month == 12 && day <= 17))
            return 11;
        else if ((month == 12 && day >= 18) || (month == 1 && day <= 8))
            return 12;
        else if ((month == 1 && day >= 9) || (month == 2 && day <= 15))
            return 13;
        else
            return 0;
    }

    public int getIntFromIntArray(@ArrayRes int id, int index) {
        int[] array = getResources().getIntArray(id);

        if (index < 0 || index >= array.length)
            index = 0;
        return array[index];
    }

    private int getArrayResLength(@ArrayRes int id) {
        try {
            return getResources().getStringArray(id).length;
        } catch (Exception ignored) {
        }

        try {
            return getResources().getIntArray(id).length;
        } catch (Exception ignored) {
        }
        return 0;
    }

    private int getSplitStringLength(@StringRes int id) {
        try {
            return getString(id).split("¶[ ]*").length;
        } catch (Exception e) {
            return 0;
        }
    }

    /* boolean methods */

    private static boolean isVowel(char c) {
        return StringUtils.indexOf(FULL_LOWERCASE_VOWELS, Character.toLowerCase(c)) >= 0;
    }

    private static boolean isNonConsecutiveConsonant(char c) {
        char[] consonants = {'h', 'j', 'q', 'v', 'w', 'x'};
        c = Character.toLowerCase(c);

        for (int i = -1, length = consonants.length; ++i < length; ) {
            if (consonants[i] == c)
                return true;
        }
        return false;
    }

    private static boolean equalsAny(char c, String s) {
        return StringUtils.indexOf(s, c) >= 0;
    }

    private static boolean equalsAnyIgnoreCase(char c, String s) {
        return equalsAny(Character.toLowerCase(c), s);
    }

    private static boolean hasVowels(String s) {
        boolean vowel = false;

        if (s == null || s.length() == 0)
            return false;
        else {
            for (int n = -1; ++n < s.length(); ) {
                if (vowel = isVowel(s.charAt(n)))
                    n = s.length();
            }
            return vowel;
        }
    }

    public boolean isPrintable(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    public boolean isCharacterMissingInFont(char c) {
        if (Character.isWhitespace(c))
            return false;
        String missingCharacter = "\u0978";
        byte[] b1 = getPixels(drawBitmap(Character.toString(c)));
        byte[] b2 = getPixels(drawBitmap(missingCharacter));
        return Arrays.equals(b1, b2);
    }

    public boolean isGlyphDisplayable(char c) {
        return isPrintable(c) && !isCharacterMissingInFont(c);
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isUrlValid(CharSequence url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static boolean isJsonValid(String json) {
        try {
            JsonParser parser = new ObjectMapper().getFactory().createParser(json);

            while (parser.nextToken() != null) {
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    private boolean isLocaleAvailable(String language, String country) {
        if (language == null || language.isEmpty())
            language = "xx";

        if (country == null)
            country = "";
        Locale locale;

        if (country.isEmpty())
            locale = new Locale(language.toLowerCase());
        else
            locale = new Locale(language.toLowerCase(), country.toUpperCase());

        return Arrays.asList(locales).contains(locale);
    }

    private boolean isResource(int id) {
        try {
            return getResources().getResourceName(id) != null;
        } catch (Resources.NotFoundException ignore) {
        }
        return false;
    }

    /* long methods */

    public static long getSeed(String s) {
        long hash = 0;

        if (s == null || s.isEmpty())
            return 0;

        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }

    /* Class-related methods */

    public Entity getEntity() {
        return getEntity(availableNames);
    }

    public Entity getEntity(List<Name> supportedNames) {
        return getEntity(supportedNames, "");
    }

    public Entity getEntity(List<Name> supportedNames, String subtype) {
        Entity entity = new Entity();
        String[] fullName = new String[]{"", "", ""};
        String[] names;
        int sex;
        int iterations = 1;
        Integer[] resArray = null;
        Name name = Name.EMPTY;
        Boolean ending = null;

        if (supportedNames != null && supportedNames.size() > 2) {
            name = supportedNames.get(randomizer.getInt(2, supportedNames.size() - 2));

            if (StringUtils.isNotBlank(subtype))
                name.setSubtype(subtype);

            switch (name) {
                case EMPTY: //Empty name
                    break;
                case TEST_CASE: //Test case
                    fullName[0] = "┤de la ┤Paz";
                    break;
                case NAME:
                    fullName[0] = getStringFromList(commonNames);
                    fullName[1] = (randomizer.getBoolean() ? getStringFromList(commonLastNames) + " " : "") + getStringFromList(commonLastNames);
                    break;
                case FORENAME:
                    fullName[0] = getStringFromList(commonNames);
                    break;
                case SURNAME:
                    fullName[1] = getStringFromList(commonLastNames);
                    entity.setSex(0);
                    break;
                case COMPOUND_SURNAME:
                    fullName[1] = getCompoundSurname();
                    entity.setSex(0);
                    break;
                case DOUBLE_BARRELLED_SURNAME:
                    fullName[1] = getDoubleBarrelledSurname();
                    entity.setSex(0);
                    break;
                case INTERNATIONAL_NAME:
                    fullName[0] = getName() + (randomizer.getFloat() <= 0.25F ? " " + getMiddleName() : "");
                    fullName[1] = getFamilyName();
                    break;
                case INTERNATIONAL_FORENAME:
                    fullName[1] = getName();
                    break;
                case INTERNATIONAL_SURNAME:
                    fullName[1] = getFamilyName();
                    entity.setSex(0);
                    break;
                case ENGLISH_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getEnglishMaleName() : getEnglishFemaleName() + (randomizer.getBoolean() ? " " + getMiddleName() : "");
                    fullName[1] = getEnglishLastName();
                    break;
                case ENGLISH_DOUBLE_BARRELLED_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getEnglishMaleName() : getEnglishFemaleName();
                    fullName[1] = getDoubleBarrelledSurname();
                    break;
                case ENGLISH_FORENAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getEnglishMaleName() : getEnglishFemaleName();
                    break;
                case ENGLISH_SURNAME:
                    fullName[1] = getEnglishLastName();
                    entity.setSex(0);
                    break;
                case SPANISH_COMPOUND_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getMaleName() : getFemaleName();
                    fullName[1] = getCompoundSurname();
                    break;
                case SPANISH_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = getNames(sex);
                    fullName[1] = getLastNames();
                    break;
                case SPANISH_FORENAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = getNames(sex);
                    break;
                case SPANISH_GIVEN_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getMaleName() + " " + getMaleName() : getFemaleName() + " " + getFemaleName();
                    break;
                case SPANISH_SURNAME:
                    fullName[1] = getLastNames();
                    entity.setSex(0);
                    break;
                case JAPANESE_NAME:
                    float probability = randomizer.getFloat();

                    if (probability < 0.2F)
                        ending = true;
                    else if (probability < 0.4F)
                        ending = false;

                    if (ending == null || !ending) {
                        sex = randomizer.getInt(1, 2);
                        entity.setSex(sex);
                        fullName[0] = sex == 1 ? getStringFromStringArray(R.array.japanese_male_names) : getStringFromStringArray(R.array.japanese_female_names);
                    }

                    if (ending == null || ending)
                        fullName[1] = getStringFromStringArray(R.array.japanese_last_names);
                    break;
                case MEXICAN_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getStringFromStringArray(R.array.mexican_male_names) : getStringFromStringArray(R.array.mexican_female_names);
                    fullName[1] = getStringFromStringArray(R.array.mexican_last_names) + " " + getStringFromStringArray(R.array.mexican_last_names);
                    break;
                case RUSSIAN_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);
                    fullName[0] = sex == 1 ? getStringFromStringArray(R.array.russian_male_names) : getStringFromStringArray(R.array.russian_female_names);
                    fullName[1] = getStringFromStringArray(R.array.russian_family_names);
                    break;
                case GENERATED_PATTERN_NAME:
                    fullName[0] = generateName();
                    break;
                case GENERATED_NATURAL_NAME:
                    fullName[0] = generateName(randomizer.getInt(1, 6));
                    break;
                case GENERATED_DEFINED_NAME:
                    int type = randomizer.getInt(0, GENERATED_NAME_FAMILY_NAME_SUFFIX.length);
                    names = generateFullName(type == 1, type == 1, type);
                    fullName[0] = names[0];
                    fullName[1] = names[1];
                    break;
                case GENERATED_FREQUENCY_NAME:
                    fullName[0] = getGeneratedNames(1, name.getSubtype())[0];
                    break;
                case GENERATED_ADJUSTED_NAME:
                    fullName[0] = getGeneratedAdjustedNames(1, name.getSubtype())[0];
                    break;
                case ARABIC_NAME:
                    resArray = new Integer[]{R.array.arabic_male_names, R.array.arabic_female_names, R.array.arabic_surnames};
                case FRENCH_NAME:
                    if (resArray == null)
                        resArray = new Integer[]{R.array.french_male_names, R.array.french_female_names, R.array.french_last_names};
                case GERMAN_NAME:
                    if (resArray == null)
                        resArray = new Integer[]{R.array.german_male_names, R.array.german_female_names, R.array.german_last_names};
                case INDIAN_NAME:
                    if (resArray == null)
                        resArray = new Integer[]{R.array.indian_male_names, R.array.indian_female_names, R.array.indian_surnames};
                case ITALIAN_NAME:
                    if (resArray == null)
                        resArray = new Integer[]{R.array.italian_male_names, R.array.italian_female_names, R.array.italian_last_names};
                case PORTUGUESE_NAME:
                    if (resArray == null) {
                        resArray = new Integer[]{R.array.portuguese_male_names, R.array.portuguese_female_names, R.array.portuguese_last_names};
                        iterations = 2;
                    }
                case SUPPORTED_NAME:
                    sex = randomizer.getInt(1, 2);
                    entity.setSex(sex);

                    for (int n = 0; n < iterations; n++) {
                        fullName[0] += !fullName[0].isEmpty() ? " " : "";
                        fullName[0] += sex == 1 ? getStringFromStringArray(resArray[0]) : getStringFromStringArray(resArray[1]);
                        fullName[1] += !fullName[1].isEmpty() ? " " : "";
                        fullName[1] += getStringFromStringArray(resArray[2]);

                        if (!StringUtils.containsWhitespace(fullName[1]) && randomizer.getInt(0, 100) == 0)
                            fullName[1] += SEPARATOR[0] + getStringFromStringArray(resArray[2]);
                    }
                    break;
                default:
                    break;
            }
        }
        entity.setName(name);

        //Remove unneeded words from name
        if (fullName[0].contains("┤")) {
            if (StringUtils.startsWith(fullName[0], "┤"))
                fullName[0] = RegExUtils.replaceFirst(fullName[0], "┤[^┤]+┤", "");
            fullName[0] = StringUtils.replace(fullName[0], "┤", "");
        }

        //Capitalize only the first letter of forename/surname, if needed
        if (StringUtils.isNotBlank(fullName[0]))
            fullName[0] = capitalizeFirst(fullName[0]);

        if (StringUtils.isNotBlank(fullName[1]) && StringUtils.isNotBlank(fullName[0]))
            fullName[1] = capitalizeFirst(fullName[1]);

        //Set generational title, if possible
        if (randomizer.getInt(0, 10) > 0) {
        } else if (randomizer.getInt(0, 3) == 0)
            fullName[2] = getSplitString(R.string.generational_designation);
        else
            fullName[2] = getStringFromList(romanNumerals);
        entity.setForename(fullName[0]);
        entity.setSurname(fullName[1]);
        entity.setGenerationalTitle(fullName[2]);
        return entity;
    }

    public Person getPerson() {
        Person person = new Person(getEntity(supportedNames)); //Set basic information

        //Set username
        person.setUsername(generateUsername());

        //Set honorific title, if possible
        if (randomizer.getBoolean()) {
            List<HonoraryTitle> titles = new ArrayList<>(Arrays.asList(HonoraryTitle.values()));
            titles.remove(0);

            if (!person.getForename().isEmpty())
                titles.remove(HonoraryTitle.HONORIFIC);
            HonoraryTitle honoraryTitle = titles.get(randomizer.getInt(0, titles.size()));
            person.setHonoraryTitle(getHonoraryTitle(honoraryTitle, person.getSex()));
        }

        //Set japanese honorific, if possible
        if (person.getName() == Name.JAPANESE_NAME && person.getHonoraryTitle().isEmpty() && person.getGenerationalTitle().isEmpty() && (person.getForename().isEmpty() ^ person.getSurname().isEmpty()) && randomizer.getBoolean())
            person.setJapaneseHonorific(SEPARATOR[0] + getSplitString(R.string.japanese_honorifics));

        //Set post-nominal letters, if possible and if the person doesn't have a honorific title
        if (person.getHonoraryTitle().isEmpty() && randomizer.getBoolean())
            person.setPostNominalLetters(getStringFromList(postNominalLetters));

        //Set random birthdate
        person.setBirthdate(randomizer.getDate());
        return person;
    }

    /* Unicode, ASCII (and other related characters) methods */

    public String getIdeogramSet(int repetitionRate, int rateDecay) {
        return getIdeogramSet(repetitionRate, rateDecay, -1);
    }

    public String getIdeogramSet(int repetitionRate, int rateDecay, int possibilityLimit) {
        int limit = 0;

        if (possibilityLimit >= 0)
            limit = possibilityLimit;

        if (limit < 0)
            limit = 0;

        if (limit == 0)
            return generateIdeogramSet(repetitionRate, rateDecay);
        else if (randomizer.getInt(0, limit) == 0)
            return generateIdeogramSet(repetitionRate, rateDecay);
        return "";
    }

    private String generateIdeogramSet(int repetitionRate, int rateDecay) {
        String ideogramSet = "";
        int[] ideograms = getResources().getIntArray(R.array.emojis);
        int ideogram = ideograms[randomizer.getInt(0, ideograms.length)];

        if (repetitionRate < 0)
            repetitionRate = 0;
        else if (repetitionRate > 100)
            repetitionRate = 100;

        if (rateDecay < 1)
            rateDecay = 10;
        else if (rateDecay > 100)
            rateDecay = 100;
        ideogramSet += getIdeogramByUnicode(ideogram);

        while (repetitionRate > 0) {
            if (randomizer.getInt(0, 101) <= repetitionRate) {
                ideogram = ideograms[randomizer.getInt(0, ideograms.length)];
                ideogramSet += getIdeogramByUnicode(ideogram); //Repeat ideogram, if possible
            }

            if (repetitionRate < rateDecay)
                repetitionRate = 0;
            else
                repetitionRate = repetitionRate - rateDecay;
        }
        return ideogramSet;
    }

    public String getIdeogramByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    /* Varargs methods */

    public final boolean verifyVararg(Object... vararg) {
        Object value;

        if (vararg != null && vararg.length > 0) {
            try {
                value = vararg[0];
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /* Date\time methods */

    public static int getHourOfDay() {
        return LocalDateTime.now().getHourOfDay();
    }

    public static int getYear() {
        return LocalDateTime.now().getYear();
    }

    public String getDayOfWeekName() {
        String[] days = getStringAsArray(R.string.days_of_week);
        return days[LocalDateTime.now().getDayOfWeek()];
    }

    public String getCurrentTime(int type) {
        if (type < 0 || type > 3)
            return "";
        else {
            Date now = new Date();
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale.getDefault());

            switch (type) {
                case 0:
                    return new SimpleDateFormat("h:mm a", symbols).format(now);
                case 1:
                    return new SimpleDateFormat("HH:mm", symbols).format(now);
                case 2:
                    return new SimpleDateFormat("HH:mm:ss Z", symbols).format(now);
                case 3:
                    return new SimpleDateFormat("HH:mm:ss", symbols).format(now);
                default:
                    return "";
            }
        }
    }

    public String getDate() {
        return LocalDate.now().toString("yyyy/MM/dd");
    }

    public static String getFormattedDate(int year, int month, int day) {
        return year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day);
    }

    public String getCurrentDate(int type) {
        if (type < 0 || type > 8)
            return "";
        else {
            Date now = new Date();
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale.getDefault());
            symbols.setShortMonths(getResources().getStringArray(R.array.month_short));
            symbols.setMonths(getResources().getStringArray(R.array.month));

            switch (type) {
                case 0:
                    return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(now);
                case 1:
                    return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(now);
                case 2:
                    return new SimpleDateFormat("dd-MMM-yyyy", symbols).format(now);
                case 3:
                    return new SimpleDateFormat("MMMM d", symbols).format(now);
                case 4:
                    return new SimpleDateFormat(getString(R.string.short_date), symbols).format(now);
                case 5:
                    String date = new SimpleDateFormat(getString(R.string.date), symbols).format(now);

                    if (getString(R.string.locale).equals(Locale.ENGLISH.getLanguage())) {
                        String day = new SimpleDateFormat("d").format(now);

                        if (day.endsWith("1") && !day.endsWith("11"))
                            date = StringUtils.replaceOnce(date, "#", "st");
                        else if (day.endsWith("2") && !day.endsWith("12"))
                            date = StringUtils.replaceOnce(date, "#", "nd");
                        else if (day.endsWith("3") && !day.endsWith("13"))
                            date = StringUtils.replaceOnce(date, "#", "rd");
                        else
                            date = StringUtils.replaceOnce(date, "#", "th");
                    }
                    return date;
                case 6:
                    return new SimpleDateFormat(getString(R.string.simple_date), symbols).format(now);
                case 7:
                    return new SimpleDateFormat(getString(R.string.full_date), symbols).format(now);
                case 8:
                    return new SimpleDateFormat("dd-MMM", symbols).format(now);
                case 9:
                    return new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(now);
                default:
                    return "";
            }
        }
    }

    public static SimpleDate getCurrentDate() {
        LocalDate today = LocalDate.now();
        return new SimpleDate(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());
    }

    public static String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }

    public static long getDifferenceInDays(String date, String anotherDate) {
        date = StringUtils.replace(date, "/", "-");
        anotherDate = StringUtils.replace(anotherDate, "/", "-");
        return Math.abs(Days.daysBetween(LocalDate.parse(date), LocalDate.parse(anotherDate)).getDays());
    }

    public String getTimeBetweenDates(String date, String anotherDate) {
        date = StringUtils.replace(date, "/", "-");
        anotherDate = StringUtils.replace(anotherDate, "/", "-");
        Period period = new Period(LocalDate.parse(date), LocalDate.parse(anotherDate), PeriodType.yearMonthDayTime());
        return getString(R.string.time_elapsed, period.getYears(), period.getMonths(), period.getDays());
    }

    public String addDaysToDate(String date, int days) {
        date = StringUtils.replace(date, "/", "-");

        if (days < 0)
            days = 0;
        return LocalDate.parse(date).plusDays(days).toString();
    }

    /* Reflection methods */

    public String callMethod(String methodName) {
        String s = null;

        try {
            Class<?> methodsClass = Class.forName("com.app.memoeslink.adivinador.Methods");
            Method m = methodsClass.getDeclaredMethod(methodName);
            m.setAccessible(true);
            s = (String) m.invoke(new Methods(Methods.this, getRandomizer().getSeed()));
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String invokeMethod(String methodName) {
        String s = (String) AccessController.doPrivileged((PrivilegedAction) () -> {
            String r = null;

            try {
                Class<Methods> methodsClass = Methods.class;
                Constructor<Methods> constructor = methodsClass.getConstructor(Context.class, Long.class);
                Object object = constructor.newInstance(Methods.this, getRandomizer().getSeed());
                Method method = methodsClass.getDeclaredMethod(methodName);
                method.setAccessible(true);
                r = (String) method.invoke(object);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return r;
        });
        return s;
    }

    /* SharedPreferences methods */

    public void deleteTemp() {
        preferences.remove("temp_changeFortuneTeller");
        preferences.remove("temp_busy");
        preferences.remove("temp_enquiryDate");
        preferences.remove("temp_restartActivity");

        if (!defaultPreferences.getBoolean("preference_keepForm"))
            clearForm();
    }

    public void clearForm() {
        preferences.remove("temp_name");
        preferences.remove("temp_formatted_name");
        preferences.remove("temp_sex");
        preferences.remove("temp_date_year");
        preferences.remove("temp_date_month");
        preferences.remove("temp_date_day");
        preferences.remove("temp_user");
        preferences.remove("temp_anonymous");
    }

    /* View-related methods */

    public void vanishAndMaterialize(View v) {
        v.clearAnimation();
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 0.0F);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        v.startAnimation(alphaAnimation);
    }

    /* HTML methods */

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        else
            result = Html.fromHtml(html);
        return result;
    }

    /* Other methods */

    public static int getTextColor(Context context) {
        int[] attrs = new int[]{android.R.attr.textColorPrimary};
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs);
        int color = a.getColor(0, Color.GRAY);
        a.recycle();
        return color;
    }

    private static void cancelToast() {
        if (toast != null && toast.getView().isShown()) {
            toast.cancel();
            toast = null;
        }
    }

    public static void showSimpleToast(Context context, String text) {
        cancelToast();
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        Sound.play(context, "computer_chimes");
        toast.show();
    }

    public static void showFormattedToast(Context context, Spanned spanned) {
        cancelToast();
        toast = Toast.makeText(context, spanned, Toast.LENGTH_SHORT);
        Sound.play(context, "computer_chimes");
        toast.show();
    }

    public static boolean setTTS(TextToSpeech tts, String language) {
        if (tts != null) {
            List<String> regionNames = new ArrayList<>();
            Locale[] locales = Locale.getAvailableLocales();

            for (Locale l : locales) {
                regionNames.add(l.toString());
            }
            Collections.shuffle(regionNames);

            int position = 0;
            boolean anyAvailable = false;

            for (int tries = regionNames.size(); tries > 0; tries--) {
                int result = tts.setLanguage(new Locale(language, regionNames.get(position)));

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                } else {
                    anyAvailable = true;
                    tries = 0;
                }
                position++;
            }
            return anyAvailable;
        } else
            return false;
    }

    public static void restartApplication(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 9999, i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public List<String> getContactNames() {
        List<String> contacts = null;
        boolean proceed = false;
        Cursor c = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Methods.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                proceed = true;
        } else proceed = true;

        try {
            if (proceed) {
                ContentResolver contentResolver = getContentResolver();
                c = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                if ((c != null ? c.getCount() : 0) > 0 && c.moveToFirst()) {
                    contacts = new ArrayList<>();

                    do {
                        contacts.add(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    } while (c.moveToNext());
                }
            }
        } catch (Exception e) {
            contacts = null;
        } finally {
            if (c != null)
                c.close();
        }
        return contacts;
    }

    public List<Name> getPermittedNames(List<Name> permittedNames, boolean complete) {
        if (permittedNames != null)
            permittedNames.clear();
        else
            permittedNames = new ArrayList<>();

        //Add default name types
        permittedNames.add(Name.EMPTY);
        permittedNames.add(Name.TEST_CASE);
        permittedNames.add(Name.NAME);
        permittedNames.add(Name.FORENAME);
        permittedNames.add(Name.SURNAME);
        permittedNames.add(Name.COMPOUND_SURNAME);
        permittedNames.add(Name.DOUBLE_BARRELLED_SURNAME);
        permittedNames.add(Name.INTERNATIONAL_NAME);
        permittedNames.add(Name.INTERNATIONAL_FORENAME);
        permittedNames.add(Name.INTERNATIONAL_SURNAME);

        if (StringUtils.equalsAny(Locale.ENGLISH.getLanguage(), getString(R.string.locale), LanguageHelper.getSystemLanguage()) || complete) {
            for (int n = -1; ++n < 4; ) {
                permittedNames.add(Name.ENGLISH_NAME);
            }
            permittedNames.add(Name.ENGLISH_DOUBLE_BARRELLED_NAME);
            permittedNames.add(Name.ENGLISH_FORENAME);
            permittedNames.add(Name.ENGLISH_SURNAME);
        }

        if (StringUtils.equalsAny("es", getString(R.string.locale), LanguageHelper.getSystemLanguage()) || complete) {
            for (int n = -1; ++n < 4; ) {
                permittedNames.add(Name.SPANISH_NAME);
            }
            permittedNames.add(Name.SPANISH_COMPOUND_NAME);
            permittedNames.add(Name.SPANISH_FORENAME);
            permittedNames.add(Name.SPANISH_GIVEN_NAME);
            permittedNames.add(Name.SPANISH_SURNAME);
        }
        permittedNames.add(Name.JAPANESE_NAME);
        permittedNames.add(Name.MEXICAN_NAME);
        permittedNames.add(Name.RUSSIAN_NAME);
        permittedNames.add(Name.GENERATED_PATTERN_NAME);
        permittedNames.add(Name.GENERATED_NATURAL_NAME);
        permittedNames.add(Name.GENERATED_DEFINED_NAME);
        permittedNames.add(Name.GENERATED_FREQUENCY_NAME);
        permittedNames.add(Name.GENERATED_ADJUSTED_NAME);

        for (int n = -1, length = SUPPORTED_LANGUAGES.length; ++n < length; ) {
            if (isLocaleAvailable(SUPPORTED_LANGUAGES[n], complete ? "" : networkCountry)) {
                Name name = null;

                switch (SUPPORTED_LANGUAGES[n]) {
                    case "ar":
                        name = Name.ARABIC_NAME;
                        break;
                    case "de":
                        name = Name.GERMAN_NAME;
                        break;
                    case "fr":
                        name = Name.FRENCH_NAME;
                        break;
                    case "hi":
                        name = Name.INDIAN_NAME;
                        break;
                    case "it":
                        name = Name.ITALIAN_NAME;
                        break;
                    case "pt":
                        name = Name.PORTUGUESE_NAME;
                        break;
                    default:
                        break;
                }

                if (name != null) {
                    for (int m = -1; ++m < 3; ) {
                        permittedNames.add(name);
                    }
                }
            }
        }
        //permittedNames.add(Name.SUPPORTED_NAME);
        return permittedNames;
    }

    public static int getThemeId(Context context) {
        SharedPreferencesHelper preferences = new SharedPreferencesHelper(context);

        switch (preferences.getStringAsInt("preference_theme")) {
            case 0:
                return R.style.DefaultTheme;
            case 1:
                return R.style.BlackTheme;
            case 2:
                return R.style.GrayTheme;
            case 3:
                return R.style.SlateGrayTheme;
            default:
                return R.style.DefaultTheme;
        }
    }

    public Bitmap drawBitmap(String text) {
        Bitmap b = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(b);
        canvas.drawText(text, 0, 10 / 2, paint);
        return b;
    }

    public byte[] getPixels(Bitmap b) {
        ByteBuffer buffer = ByteBuffer.allocate(b.getByteCount());
        b.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    public void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clipData);
        showSimpleToast(this, getString(R.string.toast_clipboard));
    }

    public boolean isEnquiryDistinct(Enquiry enquiry) {
        if (enquiry == null)
            return false;

        Object[] previousObject = new Object[]{
                preferences.getString("temp_name"),
                preferences.getInt("temp_sex", Entity.DEFAULT_SEX),
                preferences.getInt("temp_date_year", SimpleDate.DEFAULT_YEAR),
                preferences.getInt("temp_date_month", SimpleDate.DEFAULT_MONTH),
                preferences.getInt("temp_date_day", SimpleDate.DEFAULT_DAY)
        };

        Object[] currentObject = new Object[]{
                enquiry.getPerson().getFullName(),
                enquiry.getPerson().getSex(),
                enquiry.getPerson().getBirthdate().getYear(),
                enquiry.getPerson().getBirthdate().getMonth(),
                enquiry.getPerson().getBirthdate().getDay()
        };
        return !Arrays.equals(currentObject, previousObject);
    }

    public boolean isEnquiryDistinct(ArrayList<Enquiry> enquiries, Enquiry enquiry) {
        if (enquiries == null || enquiry == null)
            return false;
        boolean distinct = true;

        for (Enquiry currentEnquiry : enquiries) {
            if (Arrays.equals(
                    new Object[]{
                            currentEnquiry.getPerson().getFullName(),
                            currentEnquiry.getPerson().getSex(),
                            currentEnquiry.getPerson().getBirthdate().getYear(),
                            currentEnquiry.getPerson().getBirthdate().getMonth(),
                            currentEnquiry.getPerson().getBirthdate().getDay()
                    },
                    new Object[]{
                            enquiry.getPerson().getFullName(),
                            enquiry.getPerson().getSex(),
                            enquiry.getPerson().getBirthdate().getYear(),
                            enquiry.getPerson().getBirthdate().getMonth(),
                            enquiry.getPerson().getBirthdate().getDay()
                    }))
                distinct = false;
        }
        return distinct;
    }

    public boolean saveEnquiry(ArrayList<Enquiry> enquiries, Enquiry enquiry) {
        if (isEnquiryDistinct(enquiries, enquiry)) {
            if (enquiries.size() >= 100)
                enquiries.remove(0);
            enquiries.add(enquiry);
            Gson gson = new Gson();
            String json = gson.toJson(enquiries);
            preferences.putStringSafely("enquiryList", json);
            return true;
        }
        return false;
    }
}
