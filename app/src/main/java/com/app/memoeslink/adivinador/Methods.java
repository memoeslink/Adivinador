package com.app.memoeslink.adivinador;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.AnyRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Patterns;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Memoeslink on 03/08/2017.
 */

class Methods extends ContextWrapper {
    public static final int DEFAULT_DAY = 1;
    public static final int DEFAULT_MONTH = 0;
    public static final int DEFAULT_SEX = 0;
    public static final int DEFAULT_YEAR = 2000;
    public static final String PREFERENCES = "app_prefs";
    public static final String FULL_STOP = ".";
    public static final String ZERO_WIDTH_SPACE = "\u200B";
    public static final String[] SEPARATOR = {"-", "~", ".", "_"};
    public static String language;
    public static String networkCountry = null;
    public static Locale[] locales;
    public static List<NameEnum> availableNames = new ArrayList<>();
    public static List<NameEnum> supportedNames = new ArrayList<>();
    private static final String ACCENTED_CHARACTERS = "àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇØøÅåÆæǼǽǣŒœÐÞαßþðŠšÝýÝÿŽž";
    private static final String HEX_DIGITS = "0123456789ABCDEF";
    private static final String FULL_LOWERCASE_VOWELS = "aàáâãäåāăąǻȁȃạảấầẩẫậắằẳẵặḁæǽeȅȇḕḗḙḛḝẹẻẽếềểễệēĕėęěèéêëiȉȋḭḯỉịĩīĭįiìíîïĳoœøǿȍȏṍṏṑṓọỏốồổỗộớờởỡợōòóŏőôõöuũūŭůűųùúûüȕȗṳṵṷṹṻụủứừửữựyẙỳỵỷỹŷÿý";
    private static final String LOWERCASE_VOWELS = "aeiou";
    private static final String LOWERCASE_CONSONANTS = "bcdfghjklmnpqrstvwxyz";
    private static final String LOWERCASE_ENDING_CONSONANTS = "lmnrsz";
    private static final String LOWERCASE_REPEATED_CONSONANTS = "bbbccccdddfffggghjjjkllllmmmmnnnnpppqrrrrsssssttttvwxyz";
    private static final String ROMANIZATION_CONSONANTS = "bcdfghjkmnprstvwyz";
    private static final String UPPERCASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String UNKNOWN_DATE = "????/??/??";
    private static final String DOUBLE_DOT_REGEX = "\\.(\\s*</?\\w+>\\s*)*\\.";
    private static final String RANDOM_TAG_REGEX = "\\{rand:[^{}⸠⸡;]+(;[^{}⸠⸡;]+)*\\}";
    private static final String TAG_COMPONENT_REGEX = "(\\{(string|database|method):|\\}|\\s+|⦗\\d+⦘|⸻(\\⛌|\\⸮|\\d+))";
    private static final String TAG_REGEX = "\\{((string|database):[^{}⦗⦘⸡:⸻⛌⸮]+(⦗[\\d]+⦘)?(\\s*⸻(⛌|⸮|\\d+))?|method:[a-zA-Z0-9_$]+)\\s*\\}";
    private static final String WORD_APPENDIX_REGEX = "\\[[\\w" + ACCENTED_CHARACTERS + "⁞∅'\\s]*(,[\\w" + ACCENTED_CHARACTERS + "⁞∅'\\s]*)*\\]";
    private static final String WORD_COMPONENT_REGEX = "(\\{|\\}|(⸻(\\⛌|\\⸮|\\d+)))";
    private static final String WORD_TAG_REGEX = "\\{([\\w" + ACCENTED_CHARACTERS + "⁞∅'\\s]*" + WORD_APPENDIX_REGEX + ")(⸻(⛌|⸮|\\d+))?\\}";
    private static final int[] PROBABILITY_DISTRIBUTION = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};
    private static final char[] REPLACEMENTS = {'⚨', '⚩', '⁇', '⍰', '�', '□', '?'};
    private static final String[] COMMON_COLORS = {"#FEFF5B", "#6ABB6A", "#E55B5B", "#5B72E5", "#925BFF"};
    private static final String[][] GENERATED_NAME_START = {
            {"a", "an", "as", "bra", "ce", "cen", "den", "e", "el", "en", "ghal", "gra", "i", "in", "is", "ka", "kan", "ken", "kha", "kra", "li", "me", "o", "os", "ren", "rha", "se", "sen", "te", "tra", "u", "ul", "un", "ze", "æ"},
            {"a", "a", "a", "a", "a", "ae", "ae", "ae", "ba", "ba", "ba", "be", "be", "be", "bi", "bi", "bi", "bla", "ble", "bli", "blo", "blu", "bo", "bo", "bo", "bra", "bre", "bri", "bro", "bru", "bu", "bu", "bu", "ca", "ca", "ca", "ce", "ce", "ce", "cha", "che", "chi", "cho", "chu", "ci", "ci", "ci", "cla", "cle", "cli", "clo", "clu", "co", "co", "co", "cra", "cre", "cri", "cro", "cru", "cu", "cu", "cu", "da", "da", "da", "de", "de", "de", "di", "di", "di", "dla", "dle", "dli", "dlo", "dlu", "do", "do", "do", "dra", "dre", "dri", "dro", "dru", "du", "du", "du", "e", "e", "e", "e", "e", "fa", "fa", "fa", "fe", "fe", "fe", "fi", "fi", "fi", "fla", "fle", "fli", "flo", "flu", "fo", "fo", "fo", "fra", "fre", "fri", "fro", "fru", "fu", "fu", "fu", "ga", "ga", "ga", "ge", "ge", "ge", "gi", "gi", "gi", "gla", "gle", "gli", "glo", "glu", "go", "go", "go", "gra", "gre", "gri", "gro", "gru", "gu", "gu", "gu", "gue", "gui", "güe", "güi", "ha", "he", "hi", "ho", "hu", "i", "i", "i", "i", "i", "ja", "ja", "ja", "je", "je", "je", "ji", "ji", "ji", "jo", "jo", "jo", "ju", "ju", "ju", "ka", "ka", "ke", "ke", "ki", "ki", "ko", "ko", "ku", "ku", "la", "la", "la", "le", "le", "le", "li", "li", "li", "li", "lla", "lle", "lli", "llo", "llu", "lo", "lo", "lo", "lu", "lu", "lu", "ma", "ma", "ma", "me", "me", "me", "mi", "mi", "mi", "mo", "mo", "mo", "mu", "mu", "mu", "na", "na", "na", "ne", "ne", "ne", "ni", "ni", "ni", "no", "no", "no", "nu", "nu", "nu", "o", "o", "o", "o", "o", "ou", "ou", "ou", "pa", "pa", "pa", "pe", "pe", "pe", "pi", "pi", "pi", "pla", "ple", "pli", "plo", "plu", "po", "po", "po", "pra", "pre", "pri", "pro", "pru", "pu", "pu", "pu", "que", "qui", "ra", "ra", "ra", "re", "re", "re", "ri", "ri", "ri", "ro", "ro", "ro", "ru", "ru", "ru", "sa", "sa", "sa", "se", "se", "se", "sha", "she", "shi", "sho", "shu", "si", "si", "si", "so", "so", "so", "su", "su", "su", "ta", "ta", "ta", "te", "te", "te", "ti", "ti", "ti", "tla", "tle", "tli", "tlo", "tlu", "to", "to", "to", "tra", "tre", "tri", "tro", "tru", "tu", "tu", "tu", "u", "u", "u", "u", "u", "va", "va", "va", "ve", "ve", "ve", "vi", "vi", "vi", "vo", "vo", "vo", "vu", "vu", "vu", "wa", "wa", "we", "we", "wi", "wi", "wo", "wo", "wu", "wu", "xa", "xe", "xi", "xo", "xu", "ya", "ya", "ye", "ye", "yi", "yi", "yo", "yo", "yu", "yu", "za", "za", "ze", "ze", "zi", "zi", "zo", "zo", "zu", "zu"},
            {"a", "au", "be", "ba", "da", "do", "dra", "dul", "e", "el", "gan", "gyl", "ha", "i", "il", "ki", "kin", "la", "le", "li", "lo", "ma", "mae", "mal", "mir", "mla", "nae", "ne", "ni", "nu", "ny", "o", "rau", "sa", "sae", "sal", "san", "sil", "syl", "ta", "tho", "ti", "ty", "u", "ua", "va", "vi", "vyr"}};
    private static final String[][] GENERATED_NAME_MIDDLE = {
            {"ba", "ce", "da", "de", "dho", "dra", "ga", "ge", "gen", "gha", "gi", "hla", "hlo", "ka", "kar", "ko", "ma", "na", "pa", "par", "ta", "tha", "va", "ve", "ze", "zhe"},
            {"", "", "", "", "", "", "", "ba", "ba", "ba", "be", "be", "be", "bi", "bi", "bi", "bla", "ble", "bli", "blo", "blu", "bo", "bo", "bo", "bra", "bre", "bri", "bro", "bru", "bu", "bu", "bu", "ca", "ca", "ca", "ce", "ce", "ce", "cha", "che", "chi", "cho", "chu", "ci", "ci", "ci", "cia", "cie", "cio", "ciu", "cla", "cle", "cli", "clo", "clu", "co", "co", "co", "cra", "cre", "cri", "cro", "cru", "cu", "cu", "cu", "da", "da", "da", "de", "de", "de", "di", "di", "di", "dia", "die", "dio", "diu", "dla", "dle", "dli", "dlo", "dlu", "do", "do", "do", "dra", "dre", "dri", "dro", "dru", "du", "du", "du", "fa", "fa", "fa", "fe", "fe", "fe", "fi", "fi", "fi", "fla", "fle", "fli", "flo", "flu", "fo", "fo", "fo", "fra", "fre", "fri", "fro", "fru", "fu", "fu", "fu", "ga", "ga", "ga", "ge", "ge", "ge", "gi", "gi", "gi", "gla", "gle", "gli", "glo", "glu", "gna", "gno", "go", "go", "go", "gra", "gre", "gri", "gro", "gru", "gu", "gu", "gu", "gue", "gui", "güe", "güi", "ha", "he", "hi", "ho", "hu", "ja", "ja", "ja", "je", "je", "je", "ji", "ji", "ji", "jo", "jo", "jo", "ju", "ju", "ju", "ka", "ka", "ke", "ke", "ki", "ki", "ko", "ko", "ku", "ku", "la", "la", "la", "lba", "lbo", "le", "le", "le", "li", "li", "li", "lla", "lle", "lli", "llo", "llu", "lma", "lmo", "lo", "lo", "lo", "lu", "lu", "lu", "ma", "ma", "ma", "me", "me", "me", "mi", "mi", "mi", "mo", "mo", "mo", "mu", "mu", "mu", "na", "na", "na", "nae", "nai", "nao", "nau", "ne", "ne", "ne", "ni", "ni", "ni", "nia", "nie", "nio", "niu", "no", "no", "no", "nu", "nu", "nu", "pa", "pa", "pa", "pe", "pe", "pe", "pi", "pi", "pi", "pla", "ple", "pli", "plo", "plu", "po", "po", "po", "pra", "pre", "pri", "pro", "pru", "pu", "pu", "pu", "que", "qui", "ra", "ra", "ra", "re", "re", "re", "ri", "ri", "ri", "ro", "ro", "ro", "rra", "rre", "rri", "rro", "rru", "ru", "ru", "ru", "sa", "sa", "sa", "se", "se", "se", "sha", "she", "shi", "sho", "shu", "si", "si", "si", "so", "so", "so", "su", "su", "su", "ta", "ta", "ta", "te", "te", "te", "ti", "ti", "ti", "tla", "tle", "tli", "tlo", "tlu", "to", "to", "to", "tra", "tre", "tri", "tro", "tru", "tu", "tu", "tu", "va", "va", "va", "ve", "ve", "ve", "vi", "vi", "vi", "vo", "vo", "vo", "vu", "vu", "vu", "wa", "wa", "we", "we", "wi", "wi", "wo", "wo", "wu", "wu", "xa", "xe", "xi", "xo", "xu", "ya", "ya", "ye", "ye", "yi", "yi", "yo", "yo", "yu", "yu", "za", "za", "ze", "ze", "zi", "zi", "zo", "zo", "zu", "zu", "ña", "ñe", "ñi", "ño", "ñu"},
            {"bri", "ci", "cia", "da", "di", "dil", "dre", "dri", "dy", "dyr", "fyr", "la", "lan", "li", "lin", "lir", "los", "lu", "ma", "mi", "mil", "mir", "na", "nae", "nim", "nya", "ra", "re", "rea", "ri", "rina", "rio", "ryn", "sa", "sar", "sil", "sur", "tar", "tau", "to", "tou", "vha", "vi", "vil", "zi", "zi", "zur"}};
    private static final String[][] GENERATED_NAME_ENDING = {
            {"drin", "gen", "ghar", "gra", "kan", "ken", "kin", "ko", "kyo", "ma", "na", "nen", "nia", "nin", "rar", "ria", "rin", "rio", "rion", "ryo", "ryon", "til", "vka", "vkin", "vko", "vrin", "vyon", "zen", "zin"},
            {"ba", "ba", "ba", "be", "be", "be", "bel", "bela", "bi", "bi", "bi", "bia", "bio", "bla", "ble", "bli", "blo", "blu", "bo", "bo", "bo", "bra", "bre", "bri", "bro", "bru", "bu", "bu", "bu", "ca", "ca", "ca", "ce", "ce", "ce", "cha", "che", "chi", "cho", "chu", "ci", "ci", "ci", "cia", "ciana", "ciano", "cio", "cion", "cla", "cle", "cli", "clo", "clu", "co", "co", "co", "cra", "cre", "cri", "crita", "crito", "cro", "cru", "cta", "cto", "cu", "cu", "cu", "da", "da", "da", "de", "de", "de", "dea", "deo", "des", "di", "di", "di", "dia", "dio", "dios", "dla", "dle", "dli", "dlo", "dlu", "dna", "dno", "do", "do", "do", "don", "dona", "dor", "dora", "dra", "dra", "dre", "dri", "dro", "dro", "dru", "du", "du", "du", "fa", "fa", "fa", "fas", "fe", "fe", "fe", "fi", "fi", "fi", "fla", "fle", "fli", "flo", "flu", "fo", "fo", "fo", "fra", "fre", "fri", "fro", "fru", "fu", "fu", "fu", "ga", "ga", "ga", "ge", "ge", "ge", "gi", "gi", "gi", "gla", "gle", "gli", "glo", "glu", "gna", "gno", "go", "go", "go", "gra", "gre", "gri", "gro", "gru", "gu", "gu", "gu", "gue", "gui", "güe", "güi", "ha", "he", "hi", "ho", "hu", "ja", "ja", "ja", "je", "je", "je", "ji", "ji", "ji", "jo", "jo", "jo", "ju", "ju", "ju", "ka", "ka", "ke", "ke", "ki", "ki", "ko", "ko", "ku", "ku", "l", "l", "l", "la", "la", "la", "lba", "lbo", "lda", "ldo", "le", "le", "le", "lea", "leo", "li", "li", "li", "lia", "liano", "lina", "lino", "lio", "lla", "lle", "lli", "llo", "llu", "lma", "lmo", "lo", "lo", "lo", "lon", "lona", "lu", "lu", "lu", "ma", "ma", "ma", "me", "me", "me", "mi", "mi", "mi", "mia", "min", "mina", "mio", "mo", "mo", "mo", "mu", "mu", "mu", "n", "n", "n", "n", "na", "na", "na", "nca", "ncia", "ncio", "nco", "nda", "ndo", "ne", "ne", "ne", "ni", "ni", "ni", "nia", "nio", "no", "no", "no", "nsa", "nso", "nta", "nto", "nu", "nu", "nu", "pa", "pa", "pa", "pe", "pe", "pe", "pi", "pi", "pi", "pla", "ple", "pli", "plo", "plu", "po", "po", "po", "pra", "pre", "pri", "pro", "pru", "pu", "pu", "pu", "que", "qui", "r", "r", "r", "r", "ra", "ra", "ra", "rda", "rda", "rdo", "rdo", "re", "re", "re", "rea", "reo", "res", "ri", "ri", "ri", "ria", "riana", "riano", "rio", "ro", "ro", "ro", "rra", "rrat", "rre", "rri", "rro", "rru", "rta", "rta", "rto", "rto", "ru", "ru", "ru", "s", "s", "s", "s", "sa", "sa", "sa", "sar", "sara", "sca", "sco", "se", "se", "se", "sha", "she", "shi", "sho", "shu", "si", "si", "si", "sia", "sio", "sme", "so", "so", "so", "su", "su", "su", "ta", "ta", "ta", "tan", "tano", "te", "te", "te", "tea", "teo", "ti", "ti", "ti", "tian", "tiana", "tilde", "tin", "tina", "tla", "tle", "tli", "tlo", "tlu", "to", "to", "to", "tra", "tre", "tri", "triz", "tro", "tru", "tu", "tu", "tu", "va", "va", "va", "ve", "ve", "ve", "vi", "vi", "vi", "via", "vio", "vo", "vo", "vo", "vu", "vu", "vu", "wa", "wa", "we", "we", "wi", "wi", "wo", "wo", "wu", "wu", "xa", "xe", "xi", "xo", "xu", "ya", "ya", "ye", "ye", "yi", "yi", "yo", "yo", "yu", "yu", "z", "z", "z", "z", "za", "za", "ze", "ze", "zi", "zi", "zo", "zo", "zon", "zona", "zu", "zu", "ña", "ñe", "ñi", "ño", "ñu"},
            {"baus", "dam", "dar", "dha", "dhae", "dho", "dia", "dil", "dio", "dor", "dra", "driel", "druth", "dur", "dyl", "ema", "la", "lae", "len", "lis", "llien", "mir", "mor", "myr", "na", "nae", "nar", "nia", "nil", "nio", "nna", "nni", "nor", "nya", "ra", "rae", "rail", "ran", "rea", "rean", "reon", "reus", "rhial", "ria", "riel", "ril", "rio", "ris", "ron", "ryn", "sa", "sil", "sila", "sin", "tael", "tha", "thaus", "the", "thur", "tra", "tur", "via", "vil", "vio", "vir", "vis", "vlis"}};
    private static final String[][] GENERATED_NAME_FAMILY_NAME_SUFFIX = {
            {"gha", "gho", "kem", "kema", "ken", "kenna", "ma", "mi", "n", "na", "nem", "nema", "ni", "nma", "ra", "re", "rha", "rhin", "rho", "rin", "ten", "tenna", "zen", "zenna", "zha", "zho", "zya", "zya"},
            {"aba", "abe", "abi", "aca", "ach", "aco", "ada", "ade", "adi", "ado", "aga", "ago", "ahi", "ain", "aiz", "aja", "ajo", "ala", "ale", "ali", "all", "alo", "ama", "ami", "amo", "ana", "ane", "ani", "ano", "ans", "anu", "any", "anz", "ara", "ard", "ari", "aro", "art", "aru", "asa", "aso", "ata", "ate", "ati", "ato", "aujo", "ave", "aya", "ayo", "aza", "azo", "bal", "ban", "bar", "bas", "bel", "bes", "bez", "bia", "bon", "bra", "cal", "can", "cas", "cea", "ces", "cha", "che", "chez", "chi", "cho", "cia", "cio", "ciu", "con", "cos", "dad", "dal", "dan", "dar", "das", "dea", "der", "des", "dez", "dia", "din", "dina", "dino", "dio", "don", "dor", "dos", "dra", "dro", "ean", "eca", "ech", "eco", "eda", "edo", "ega", "egi", "ego", "eja", "ejo", "ela", "ell", "elo", "ena", "eno", "ens", "ent", "er", "era", "eri", "ero", "ert", "esa", "eso", "eta", "ete", "eto", "eva", "ez", "eza", "gal", "gan", "gas", "ger", "ges", "gil", "gon", "gos", "gua", "gue", "guer", "guez", "gui", "hal", "han", "har", "hea", "her", "hir", "hou", "ia", "ian", "ias", "ibi", "ica", "ich", "ico", "ida", "ide", "idi", "ido", "iel", "ier", "ies", "iga", "igo", "ijo", "ila", "ili", "imi", "ina", "ine", "ini", "ino", "ion", "ios", "ira", "ire", "iri", "iro", "is", "isa", "iso", "ita", "iti", "ito", "iuc", "iva", "iz", "iza", "izo", "jar", "jas", "jon", "jos", "la", "lah", "lal", "lan", "lar", "las", "lat", "lda", "lde", "ldo", "lea", "ler", "les", "let", "lez", "li", "lia", "lin", "lio", "lis", "lla", "lle", "lli", "llo", "lls", "lo", "lon", "los", "lta", "lva", "man", "mar", "mas", "med", "mes", "mil", "min", "mon", "mpa", "na", "nal", "nas", "nau", "nca", "nce", "nco", "nda", "nde", "ndi", "ndo", "nea", "ner", "nes", "net", "nez", "nga", "ngo", "nis", "niz", "no", "nos", "nov", "nta", "nte", "nto", "nza", "nzo", "oba", "oca", "oiu", "ola", "oli", "olo", "ols", "ona", "oni", "ons", "ora", "oro", "ort", "osa", "oso", "ota", "ote", "oto", "oud", "ouh", "oui", "ouk", "oul", "oun", "our", "out", "ova", "oya", "oyo", "oza", "pez", "que", "qui", "ra", "ral", "ran", "ras", "rat", "ray", "raz", "rca", "rda", "rdi", "rdo", "rea", "ren", "res", "ret", "rey", "rez", "rga", "ria", "rin", "rio", "ris", "riu", "riz", "rna", "ron", "ronda", "rondo", "ros", "rra", "rre", "rri", "rro", "rta", "rte", "rto", "rza", "san", "sar", "sas", "sca", "sco", "scu", "sen", "ses", "sio", "son", "ssa", "ssi", "sta", "ste", "sti", "sto", "tal", "tan", "tar", "tas", "tea", "tel", "ter", "tes", "tia", "tin", "to", "ton", "tor", "tos", "tra", "tre", "tro", "tti", "uan", "ubi", "uca", "uch", "udi", "udo", "uel", "uer", "ues", "uet", "uez", "uin", "ula", "umi", "una", "uni", "ura", "uri", "uro", "uru", "usa", "uta", "uti", "uza", "val", "van", "vas", "ver", "ves", "via", "ya", "yan", "yes", "yo", "zan", "zar", "zas", "zo", "zon"},
            {"udaeus", "udalis", "udeus", "udhil", "udhur", "udyr", "udur", "udyl", "ufur", "ulden", "uldor", "uldur", "ulen", "ulenyr", "ulinor", "ulnur", "ulond", "ulur", "ulthur", "um", "umus", "umyr", "unden", "unor", "unor", "urde", "ureus", "urin", "uris", "urus", "uryn", "us", "ustur", "utur", "uvaeus", "uvaerus", "uvir", "uvur", "uvurus"}};
    private static final String[] MIDDLE_CONSONANTS = {"bd", "bn", "bs", "cc", "ct", "dj", "ds", "gn", "lf", "lm", "lp", "ls", "lt", "mb", "mn", "mp", "ms", "nc", "nf", "ng", "nj", "nk", "nl", "nm", "nn", "nr", "ns", "nz", "nt", "nv", "nz", "pc", "ps", "pt", "rb", "rc", "rd", "rg", "rj", "rl", "rm", "rn", "rp", "rr", "rs", "rt", "sb", "sc", "sc", "sf", "sl", "sn", "sp", "ss", "st"};
    private static final String[] PAIR_OF_CONSONANTS = {"bl", "br", "ch", "cl", "cr", "dr", "fl", "fr", "gl", "gr", "kh", "kl", "kr", "ll", "pl", "pr", "rh", "sh", "tl", "tr", "vl"};
    private static final String[] PAIR_OF_ROMANIZATION_CONSONANTS = {"by", "ch", "gy", "hy", "jy", "ky", "my", "ny", "py", "ry", "sh", "ts"};
    private static final String[] PAIR_OF_VOWELS = {"ae", "ai", "ao", "au", "ea", "ei", "eo", "eu", "ia", "ie", "io", "iu", "oa", "oe", "oi", "ou", "ua", "ue", "ui", "uo", "æ", "œ"};
    private static final String[] SUPPORTED_LANGUAGES = {"ar", "de", "fr", "hi", "it", "pt"};
    private static final Pattern DOUBLE_CONSONANT_START_PATTERN = Pattern.compile("^[" + LOWERCASE_CONSONANTS + "]{2}.+");
    private static final Pattern DOUBLE_CONSONANT_AND_VOWEL_START_PATTERN = Pattern.compile("^[" + LOWERCASE_CONSONANTS + "]{2}[" + FULL_LOWERCASE_VOWELS + "](.+)");
    private static final Pattern DOUBLE_DOT_PATTERN = Pattern.compile(DOUBLE_DOT_REGEX);
    private static final Pattern SEX_PATTERN = Pattern.compile("(｢[0-2]｣)");
    private static final Pattern SEX_APPENDIX_PATTERN = Pattern.compile("⸻\\d+");
    private static final Pattern RANDOM_TAG_PATTERN = Pattern.compile(RANDOM_TAG_REGEX);
    private static final Pattern MULTIPLE_CONSONANT_PATTERN = Pattern.compile("([" + LOWERCASE_CONSONANTS + "]{2})([" + LOWERCASE_CONSONANTS + "]{1,})");
    private static final Pattern MULTIPLE_VOWEL_PATTERN = Pattern.compile("([" + FULL_LOWERCASE_VOWELS + "])[" + FULL_LOWERCASE_VOWELS + "]{1,}([" + FULL_LOWERCASE_VOWELS + "])");
    private static final Pattern TAG_COMPONENT_PATTERN = Pattern.compile(TAG_COMPONENT_REGEX);
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final Pattern WORD_APPENDIX_PATTERN = Pattern.compile(WORD_APPENDIX_REGEX);
    private static final Pattern WORD_COMPONENT_PATTERN = Pattern.compile(WORD_COMPONENT_REGEX);
    private static final Pattern WORD_TAG_PATTERN = Pattern.compile(WORD_TAG_REGEX);
    private static final Pattern THREE_CONSONANT_PATTERN = Pattern.compile("([" + LOWERCASE_CONSONANTS + "])[" + LOWERCASE_CONSONANTS + "]([" + LOWERCASE_CONSONANTS + "])");
    private static Field[] versionCodes;
    private static Toast toast;
    private static MediaPlayer mediaPlayer;
    private static Paint paint = new Paint();
    private static List<String> commonNames;
    private static List<String> commonLastNames;
    private static List<String> compoundLastNames;
    private static List<String> adjectives;
    private static List<String> spanishSingularAdjectives;
    private static List<String> spanishPluralAdjectives;
    private static List<String> nouns;
    private static List<String> spanishNouns;
    private static List<String> englishNouns;
    private static List<String> interjections;
    private static List<String> royalTitles;
    private static List<String> romanNumerals;
    private static List<String> postNominalLetters;
    private static List<String> opinions;
    private static List<String> colors;
    private static List<String> contactNames;
    private static boolean initialized = false;
    private static boolean retrieved = false;
    private static char[] sexes = {'⁇', '⚲', '♂', '♀'};
    private int themeId = 0;
    private SharedPreferences preferences;
    private SharedPreferences defaultPreferences;
    private DatabaseConnection myDB;
    private Randomizer randomizer;

    static {
        locales = Locale.getAvailableLocales();
        language = getDefaultLanguage();
        versionCodes = Build.VERSION_CODES.class.getFields();
    }

    public Methods(Context context) {
        super(context);
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myDB = new DatabaseConnection(context);
        randomizer = new Randomizer();

        if (!initialized) {
            commonNames = getStringAsList(R.string.common_names);
            commonLastNames = getStringAsList(R.string.common_last_names);
            compoundLastNames = getStringAsList(R.string.compound_last_names);
            adjectives = getStringAsList(R.string.adjectives);
            spanishSingularAdjectives = myDB.selectSingularAdjectives();
            spanishPluralAdjectives = myDB.selectPluralAdjectives();
            nouns = getStringAsList(R.string.nouns);
            spanishNouns = myDB.selectNouns();
            englishNouns = myDB.selectEnglishNouns();
            interjections = getStringAsList(R.string.interjections);
            royalTitles = getStringAsList(R.string.royal_titles);
            romanNumerals = getStringAsList(R.string.roman_numerals);
            postNominalLetters = getStringAsList(R.string.post_nominal_letters);
            opinions = getStringAsList(R.string.opinions);

            if ((themeId = getTheme(context)) != 0 && themeId == R.style.SlateGrayTheme)
                colors = getStringAsList(R.string.bright_colors);
            else
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

    public Randomizer getRandomizer() {
        return randomizer;
    }

    /* String methods :: Common */

    public String getInterjection() {
        return getStringFromList(interjections);
    }

    public List<String> getOpinions() {
        return opinions;
    }

    public String getOpinion() {
        return getStringFromList(opinions);
    }

    public String getDeviceCommonUser() {
        switch (randomizer.getInt(9, 0)) {
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
                return String.format(getResources().getString(R.string.device_version_user), genderify(getString(R.string.default_user), getSex()).getText(), name + " " + "(" + Build.VERSION.RELEASE + ")");
            case 1:
                return String.format(getResources().getString(R.string.device_user), genderify(getString(R.string.default_user), getSex()).getText(), (Build.MANUFACTURER.isEmpty() ? "?" : Build.MANUFACTURER) + (Build.MODEL.isEmpty() ? "" : " ") + Build.MODEL);
            case 2:
                return String.format(getResources().getString(R.string.device_user), genderify(getString(R.string.default_user), getSex()).getText(), (Build.BRAND.isEmpty() ? "?" : Build.BRAND) + (Build.MODEL.isEmpty() ? "" : " ") + Build.MODEL);
            case 3:
                return String.format(getResources().getString(R.string.device_user), genderify(getString(R.string.default_user), getSex()).getText(), Build.PRODUCT.isEmpty() ? "?" : Build.PRODUCT);
            case 4:
                return String.format(getResources().getString(R.string.android_device_user), genderify(getString(R.string.default_user), getSex()).getText(), getDeviceId());
            case 5:
                return String.format(getResources().getString(R.string.device_brand_user), genderify(getString(R.string.default_user), getSex()).getText(), Build.BRAND.isEmpty() ? "?" : Build.BRAND);
            case 6:
                if (isNetworkAvailable()) {
                    String networkName = getNetworkName().trim();
                    return String.format(getResources().getString(R.string.network_user), networkName.isEmpty() ? "?" : networkName);
                } else
                    return getStringFromRes(R.string.no_network_user);
            case 7:
                String networkOperator = getNetworkOperator();

                if (networkOperator.trim().isEmpty())
                    return getStringFromRes(R.string.no_network_operator_user);
                else
                    return String.format(getResources().getString(R.string.network_operator_user), networkOperator);
            case 8:
                String ipAddress = getLocalIpAddress();

                if (ipAddress == null || ipAddress.isEmpty())
                    return getStringFromRes(R.string.disconnected_device_user);
                else
                    return String.format(getResources().getString(R.string.connected_device_user), ipAddress);
            default:
                return "";
        }
    }

    public String getSubject(int sex, Boolean toggled) {
        if (sex < -1 || sex > 2)
            sex = -1;

        if (defaultPreferences.getString("preference_language", "es").equals("es")) {
            String subject;
            String noun = "";
            String adjective;
            int nounSex = -1;

            if (toggled == null || toggled) {
                boolean ready = false;

                while (!ready) {
                    noun = getStringFromList(nouns);
                    Matcher matcher = SEX_PATTERN.matcher(noun);

                    if (matcher.find()) {
                        if (sex == -1 || sex == 0) {
                            String substring = noun.substring(noun.indexOf("｢") + 1, noun.indexOf("｣"));
                            nounSex = Integer.parseInt(substring);
                            noun = noun.replaceAll("｢[0-2]｣", "");
                            ready = true;
                        }
                    } else {
                        TextComponent tempText = genderify(noun, sex);
                        noun = tempText.getText();
                        nounSex = tempText.getHegemonicSex();
                        ready = true;
                    }
                }
            } else
                nounSex = sex;
            subject = noun;

            if (toggled == null)
                subject += " ";

            if (toggled == null || !toggled) {
                adjective = getStringFromList(adjectives);
                adjective = genderify(adjective, nounSex).getText();
                subject += adjective;
            }
            return subject;
        } else {
            if (toggled == null)
                return getStringFromList(adjectives) + " " + genderify(getStringFromList(nouns), sex).getText();
            else if (toggled)
                return genderify(getStringFromList(nouns), sex).getText();
            else
                return getStringFromList(adjectives) + " " + getStringFromRes(R.string.default_person);
        }
    }

    public TextComponent genderify(String s, Integer sex) {
        TextComponent component = new TextComponent();
        component.setText(s);
        String result = "";
        List<String> parts;

        if (sex == null)
            return component;
        else if (sex < -1 || sex > 2)
            sex = -1;

        if (s.contains("|"))
            parts = Arrays.asList(s.split(Pattern.quote("|")));
        else {
            parts = new ArrayList<>();
            parts.add(s);
        }

        for (int p = 0; p < parts.size(); p++) {
            Matcher matcher = WORD_APPENDIX_PATTERN.matcher(parts.get(p));

            if (matcher.find()) {
                String substring = parts.get(p).substring(parts.get(p).indexOf("[") + 1, parts.get(p).indexOf("]"));
                List<String> items = Arrays.asList(substring.split(","));
                List<String> sortedItems = new ArrayList<>(items);
                boolean shortened = false;

                Collections.sort(sortedItems, (item, otherItem) -> {
                    if (item.length() > otherItem.length())
                        return 1;
                    else
                        return item.compareTo(otherItem);
                });

                if (sortedItems != null && sortedItems.size() >= 2) {
                    if (sortedItems.get(0).length() <= 1 && sortedItems.get(sortedItems.size() - 1).length() <= 1)
                        shortened = true;
                }

                if (sex == -1 || sex == 0) {
                    String appendix = "";

                    for (int n = 0; n < items.size(); n++) {
                        if (items.get(n) != null) {
                            if (shortened)
                                appendix += (appendix.isEmpty() ? parts.get(p).replaceAll(WORD_APPENDIX_REGEX, items.get(n)) : items.get(n)) + (appendix.isEmpty() ? "(" : "") + (n >= items.size() - 1 ? ")" : "");
                            else
                                appendix += parts.get(p).replaceAll(WORD_APPENDIX_REGEX, items.get(n)) + (n < items.size() - 1 ? "/" : "");
                        }
                    }
                    result += appendix;
                } else {
                    substring = sex == 1 ? items.get(0) : items.get(1);
                    result += parts.get(p).replaceAll(WORD_APPENDIX_REGEX, substring);
                }
            } else
                result += parts.get(p);
            result += (p < parts.size() - 1 ? " " : "");
        }

        if (!result.isEmpty())
            component.setText(result);
        component.setHegemonicSex(sex);
        return component;
    }

    public String getNames(int sex) {
        float[] probability = {1.0F, 0.8F, 0.2F, 0.05F, 0.0125F};
        String s = "";

        for (int n = -1, length = probability.length; ++n < length; ) {
            if (randomizer.getFloat() <= probability[n]) {
                if (!s.isEmpty())
                    s = s + " ";
                s = s + (sex == 1 ? getMaleName() : getFemaleName());
            } else
                n = length - 1;
        }
        return s;
    }

    public String getLastNames() {
        float probability = randomizer.getFloat();

        if (probability <= 0.075F) {
            return getCompoundSurname() + " " + getLastName();
        } else if (probability <= 0.15F) {
            return getLastName() + " " + getCompoundSurname();
        } else {
            return getLastName() + " " + getLastName();
        }
    }

    public String[] getGeneratedNames(int length, String subtype) {
        if (length <= 0)
            length = 1;
        int index;

        if (subtype != null && !subtype.isEmpty()) {
            String[] subtypes = {"english", "hawaiian", "japanese", "spanish"};
            index = 0;

            for (int n = 0; n < subtypes.length; n++) {
                if (subtypes[n].equals(subtype))
                    n = subtypes.length;
                else
                    index++;
            }

            if (index == subtypes.length)
                index = randomizer.getInt(index, 0);
        } else
            index = randomizer.getInt(getResources().getStringArray(R.array.mystic_strings).length, 0);
        String[] names = new String[length];
        int minLength = 4, extraLength = getResources().getIntArray(R.array.name_extra_length)[index];
        String letters = getResources().getStringArray(R.array.mystic_strings)[index];
        boolean romanized = false, fixed = false;
        CharEnum bannedDuplicate = CharEnum.NULL;

        if (index == 2)
            romanized = true;
        else if (index == 3) {
            bannedDuplicate = CharEnum.VOWEL;
            fixed = true;
        } else {
            bannedDuplicate = CharEnum.ANY_CHARACTER;
            fixed = true;
        }

        if (letters != null && !letters.isEmpty()) {
            for (int n = 0; n < length; n++) {
                names[n] = generateName(letters, randomizer.getInt(extraLength + 1, minLength), bannedDuplicate, fixed, romanized);
            }
        } else
            names = new String[]{"?", "?"};
        return names;
    }

    public String getCompoundSurname() {
        return getStringFromList(compoundLastNames);
    }

    public String getDoubleBarrelledSurname() {
        return getEnglishLastName() + SEPARATOR[0] + getEnglishLastName();
    }

    public String getContactName(boolean formatted) {
        String contactName = null;

        try {
            if (contactNames != null && contactNames.size() > 0) {
                contactName = contactNames.get(randomizer.getInt(contactNames.size(), 0)).trim();

                if (contactName == null || contactName.isEmpty())
                    return "";
                else {
                    if (!formatted)
                        return contactName;
                    else {
                        if (isEmailValid(contactName))
                            return "<font color=#FFFFC6>" + contactName + "</font>";
                        else if (isUrlValid(contactName))
                            contactName = formatText(new String[]{contactName}, "", "a");
                        else {
                            if (!Pattern.compile("\\s").matcher(contactName).find())
                                contactName = formatText(new String[]{contactName}, "", "b,tt");
                            else
                                contactName = formatText(new String[]{contactName}, "", "b");
                        }
                        return "<font color=" + getColorAsString() + ">" + contactName + "</font>";
                    }
                }
            } else
                return contactName;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> extractMatches(String s, Pattern p) {
        List<String> matches = new ArrayList<>();

        try {
            Matcher m = p.matcher(s);

            while (m.find()) {
                matches.add(m.group());
            }
            return matches;
        } catch (Exception e) {
            matches.add(s);
            return matches;
        }
    }

    public String generateGibberish(int minLength) {
        if (minLength < 0)
            minLength = 0;
        int length = randomizer.getInt(13, minLength);
        int spaceIndex = 0;
        int index = 0;
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        boolean defined = false;

        for (int i = 0; i < length; i++) {
            if (!defined) {
                if (randomizer.getBoolean())
                    spaceIndex = randomizer.getInt(7, 0) + randomizer.getInt(6, 0) + 1;
                else
                    spaceIndex = randomizer.getInt(12, 1);
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

    private String generateName(String letters, int approximateLength, CharEnum bannedDuplicate, boolean dropped, boolean romanized) {
        String s;
        StringBuilder stringBuilder = new StringBuilder();
        char previousLetter = '\0';
        char currentLetter = getAChar(letters);
        boolean sameType = false;
        boolean allowed;
        boolean equal;
        Boolean vowelFound, anotherVowelFound;
        float[] approvalRate = {0.5F, 0.25F, 0F};

        int count = 0;

        if (letters == null || letters.length() == 0)
            letters = LOWERCASE_VOWELS + LOWERCASE_CONSONANTS;

        if (approximateLength < 1 || approximateLength > 9999)
            approximateLength = 1;

        for (int n = -1; ++n < approximateLength; ) {
            if (stringBuilder.length() >= 1) {
                previousLetter = stringBuilder.charAt(stringBuilder.length() - 1);

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
                    while ((vowelFound = isVowel(previousLetter)) != isVowel(currentLetter) || (!vowelFound && equalsAny(currentLetter, "ñç")) || (equal && isNonConsecutiveConsonant(currentLetter)) || !allowed);
                } else {
                    do {
                        currentLetter = getAChar(letters);
                    }
                    while ((vowelFound = isVowel(previousLetter)) == (anotherVowelFound = isVowel(currentLetter)) || (!vowelFound && equalsAny(currentLetter, "ñç")) || (equalsAny(previousLetter, "ñç") && !anotherVowelFound));
                }
            }
            stringBuilder.append(currentLetter);

            if (randomizer.getFloat() <= approvalRate[count] && currentLetter != 'ñ') {
                count++;
                sameType = true;
            } else {
                count = 0;
                sameType = false;
            }
        }
        s = stringBuilder.toString();

        if (!hasVowels(s)) {
            char[] c = s.toCharArray();
            c[randomizer.getInt(approximateLength, 0)] = getAChar(LOWERCASE_VOWELS);
            s = String.valueOf(c);
        }

        if (s.length() > 1 && s.endsWith("ñ")) {
            switch (randomizer.getInt(4, 0)) {
                case 0:
                    s = StringUtils.substring(s, 0, s.length() - 1) + StringUtils.stripAccents(Character.toString(s.charAt(s.length() - 1)));
                    break;
                case 1:
                    s = StringUtils.substring(s, 0, s.length() - 1) + getAChar(LOWERCASE_CONSONANTS);
                    break;
                case 2:
                    s = StringUtils.substring(s, 0, s.length() - 1);
                    break;
                case 3:
                    s += getAChar(LOWERCASE_VOWELS);
                    break;
            }
        }

        if (dropped)
            s = dropLetters(s);
        s = fixLetters(s, bannedDuplicate, romanized);
        return capitalizeFirst(s);
    }

    public String generateName(int iterations) {
        String s = "";

        if (iterations < 1 || iterations > 100)
            iterations = 1;

        if (randomizer.getInt(3, 0) == 0)
            s = getVowels();
        s = s + (randomizer.getBoolean() ? getStringFromStringArray(PAIR_OF_CONSONANTS) : getAChar(LOWERCASE_REPEATED_CONSONANTS));
        s = s + getVowels();

        for (int i = 1; i < iterations; i++) {
            float probability = randomizer.getFloat();

            if (probability <= 0.7F)
                s = s + getAChar(LOWERCASE_REPEATED_CONSONANTS);
            else if (probability <= 0.85F)
                s = s + getStringFromStringArray(PAIR_OF_CONSONANTS);
            else
                s = s + getStringFromStringArray(MIDDLE_CONSONANTS);
            s = s + getVowels();
        }

        if (randomizer.getInt(5, 0) == 0)
            s = s + getAChar(LOWERCASE_ENDING_CONSONANTS);
        return capitalizeFirst(s);
    }

    private String generateName(boolean different, boolean uppercase, boolean dropped, int... type) {
        String name;
        int definedType;

        if (verifyIntVararg(type)) {
            if (type[0] < 0 || type[0] > GENERATED_NAME_FAMILY_NAME_SUFFIX.length - 1)
                definedType = 0;
            else
                definedType = type[0];
        } else
            definedType = randomizer.getInt(GENERATED_NAME_FAMILY_NAME_SUFFIX.length, 0);
        name = getStringFromStringArray(GENERATED_NAME_START[definedType]) + getStringFromStringArray(GENERATED_NAME_MIDDLE[definedType]) + getStringFromStringArray(GENERATED_NAME_ENDING[definedType]);

        if (different)
            name = removeDuplicates(name, CharEnum.VOWEL);

        if (dropped)
            name = dropLetters(name);
        return uppercase ? capitalizeFirst(name) : name;
    }

    private String generateLastName(boolean different, boolean uppercase, boolean dropped, int... type) {
        String lastName;
        String suffix;
        int definedType;

        if (verifyIntVararg(type)) {
            if (type[0] < 0 || type[0] > GENERATED_NAME_FAMILY_NAME_SUFFIX.length - 1)
                definedType = 0;
            else
                definedType = type[0];
        } else
            definedType = randomizer.getInt(GENERATED_NAME_FAMILY_NAME_SUFFIX.length, 0);
        lastName = generateName(different, false, dropped, definedType);
        suffix = GENERATED_NAME_FAMILY_NAME_SUFFIX[definedType][randomizer.getInt(GENERATED_NAME_FAMILY_NAME_SUFFIX[definedType].length, 0)];

        switch (definedType) {
            case 1:
                if (lastName.matches("^.*[aeiou]$") && suffix.matches("^[aeiou].*$"))
                    lastName = lastName.substring(0, lastName.length() - 1) + suffix;
                else
                    lastName = lastName + suffix;
                lastName = removeDuplicates(lastName, CharEnum.VOWEL);
                break;
            default:
                lastName = lastName + suffix;
                break;
        }

        if (dropped)
            lastName = dropLetters(lastName);
        return uppercase ? capitalizeFirst(lastName) : lastName;
    }

    private String[] generateFullName(boolean different, boolean fixed, int... type) {
        int definedType;

        if (verifyIntVararg(type)) {
            if (type[0] < 0 || type[0] > GENERATED_NAME_FAMILY_NAME_SUFFIX.length - 1)
                definedType = 0;
            else
                definedType = type[0];
        } else
            definedType = randomizer.getInt(GENERATED_NAME_FAMILY_NAME_SUFFIX.length, 0);
        return new String[]{generateName(different, true, fixed, definedType), generateLastName(different, true, fixed, definedType)};
    }

    public String generateUsername() {
        String username;
        float probability = randomizer.getFloat();

        if (probability <= 0.4F) {
            int separatorType = randomizer.getInt(SEPARATOR.length - 1, 1);

            if (randomizer.getBoolean()) {
                Object[] noun = getSpanishNoun(null, false);
                noun[0] = StringUtils.replace(noun[0].toString(), " ", SEPARATOR[separatorType]);
                username = noun[0].toString() + SEPARATOR[separatorType] + getSpanishAdjective((Integer) noun[1], ((Boolean) noun[2]).booleanValue());
                username = StringUtils.replace(username, "ñ", "ny");
                username = normalize(username);
            } else
                username = getEnglishAdjective() + SEPARATOR[separatorType] + getCommonNoun();

            if (randomizer.getBoolean()) {
                username = username + SEPARATOR[separatorType];

                if (randomizer.getBoolean())
                    username = username + randomizer.getInt(1000, 0);
                else {
                    int year = getYear();
                    int difference = randomizer.getInt(201, 0);

                    if (difference < 0)
                        year = year - difference;
                    else
                        year = year + difference;

                    if (year < 1)
                        year = 2000;
                    username = username + year;
                }
            }
            username = username.toLowerCase();
        } else if (probability <= 0.8F)
            username = getUsername();
        else {
            String appendix = getFamilyName();
            appendix = normalize(appendix);
            appendix = RegExUtils.replaceAll(appendix, "[^a-zA-Z]", "");
            username = Character.toString(getAChar(UPPERCASE_ALPHABET));

            if (appendix.length() > 4)
                username += StringUtils.substring(appendix, 0, 5);
            else
                username += appendix;
            username += randomizer.getInt(101, 0);
        }
        return username;
    }

    private String getVowels() {
        if (randomizer.getInt(10, 0) > 0)
            return Character.toString(getAChar(LOWERCASE_VOWELS));
        else {
            String vowels = getStringFromStringArray(PAIR_OF_VOWELS);

            if (vowels.length() == 1 && !isGlyphDisplayable(vowels.charAt(0)))
                vowels = StringUtils.replaceEach(vowels, new String[]{"æ", "œ",}, new String[]{"ae", "oe"});
            return vowels;
        }
    }

    private String dropLetters(String s) {
        s = MULTIPLE_CONSONANT_PATTERN.matcher(s).replaceAll("$2");
        return s;
    }

    private String fixLetters(String s, CharEnum bannedDuplicate, boolean romanized) {
        String consonants = romanized ? ROMANIZATION_CONSONANTS : LOWERCASE_CONSONANTS;
        s = THREE_CONSONANT_PATTERN.matcher(s).replaceAll("$1" + getAChar(LOWERCASE_VOWELS) + "$2");

        if (romanized) {
            s = MULTIPLE_VOWEL_PATTERN.matcher(s).replaceAll("$1" + getAChar(consonants) + "$2");
            s = DOUBLE_CONSONANT_AND_VOWEL_START_PATTERN.matcher(s).replaceFirst(PAIR_OF_ROMANIZATION_CONSONANTS[randomizer.getInt(PAIR_OF_ROMANIZATION_CONSONANTS.length, 0)] + getAChar("aou") + "$1");
            s = StringUtils.replaceEach(s, new String[]{"aa", "ii", "uu", "ei", "ee", "ou", "oo"}, new String[]{"ā", "ī", "ū", "ē", "ē", "ō", "ō"});
        } else {
            if (DOUBLE_CONSONANT_START_PATTERN.matcher(s).matches()) {
                boolean matching = false;

                for (int n = 0; n < PAIR_OF_CONSONANTS.length; n++) {
                    if (s.startsWith(PAIR_OF_CONSONANTS[n]))
                        matching = true;
                }

                if (!matching)
                    s = PAIR_OF_CONSONANTS[randomizer.getInt(PAIR_OF_CONSONANTS.length, 0)] + s.substring(2);
            }
        }
        s = removeDuplicates(s, bannedDuplicate);
        return s;
    }

    public String removeDuplicates(String s, CharEnum bannedDuplicate) {
        switch (bannedDuplicate) {
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

    private String getTitleOfHonor(int sex, boolean... prefixEnabled) {
        boolean enabled;
        String[] titles;
        String temp = "";

        if (sex < -1 || sex > 2)
            sex = randomizer.getInt(4, 0) - 1;

        if (verifyBooleanVararg(prefixEnabled))
            enabled = prefixEnabled[0];
        else
            enabled = true;

        if (sex == -1 || sex == 0) {
            if (enabled)
                titles = new String[]{"title", "class", "prefix", "occupation", "royal_title"};
            else
                titles = new String[]{"title", "class", "occupation"};
        } else if (sex == 1)
            titles = new String[]{"title", "class", "male_honorific", "occupation"};
        else if (sex == 2)
            titles = new String[]{"title", "class", "female_honorific", "occupation"};
        else
            titles = new String[0];

        if (titles.length > 0) {
            switch (titles[randomizer.getInt(titles.length, 0)]) {
                case "title":
                    return String.format(getResources().getString(R.string.title), genderify(getSplitString(R.string.title_descriptor), sex).getText(), getSplitString(R.string.title_level), genderify(getSplitString(R.string.title_job), sex).getText());
                case "class":
                    return genderify(String.format(getResources().getString(R.string.class_component), getSplitString(R.string.classes), randomizer.getInt(99, 1)), sex).getText();
                case "prefix":
                    return temp = getSplitString(R.string.honorifics) == "∅" ? "" : temp;
                case "male_honorific":
                    return temp = getSplitString(R.string.male_honorifics) == "∅" ? "" : temp;
                case "female_honorific":
                    return temp = getSplitString(R.string.female_honorifics) == "∅" ? "" : temp;
                case "royal_title":
                    return getStringFromList(royalTitles);
                case "occupation":
                    return replaceTags(getString(R.string.method_occupation), sex).getText();
                default:
                    return "";
            }
        } else
            return "";
    }

    @SuppressWarnings("deprecation")
    public static String getSystemLanguage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        else
            return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    public static String getDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getLanguage() {
        return getLanguage(Methods.this);
    }

    public static String getLanguage(Context context) {
        String language;
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!defaultPreferences.contains("preference_language")) {
            language = getDefaultLanguage();

            if (!language.equals("en") && !language.equals("es"))
                language = "en";
            defaultPreferences.edit().putString("preference_language", language).apply();
        } else
            language = defaultPreferences.getString("preference_language", "es");
        return language;
    }

    private String getNetworkName() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED)
            return wifiInfo.getSSID();
        else
            return "";
    }

    public String getNetworkOperator() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if ((manager != null ? manager.getSimState() : 0) == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkOperatorName();
        else
            return "";
    }

    public String getNetworkCountry() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (manager.getSimState() == TelephonyManager.SIM_STATE_READY)
            return manager.getNetworkCountryIso();
        else
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
        String android_id;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            android_id = Settings.Secure.ANDROID_ID;
        else
            android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (android_id == null || android_id.trim().isEmpty())
            return getDevicePseudoId();
        else
            return android_id;
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
        else {
            return Integer.toHexString(value % 16);
        }
    }

    public String getChineseZodiacSign(int year) {
        return getStringFromStringArray(R.array.chinese_zodiac_sign, year % 12);
    }

    public String getChainOfEvents(String name, boolean user) {
        String chain = "";
        List<Object[]> people = new ArrayList<>();
        int listSize;
        int arrayLength = getArrayLength(R.array.person);
        int typeOfSecondPerson = randomizer.getInt(arrayLength - 1, 1);
        int typeOfThirdPerson = typeOfSecondPerson <= 9 ? randomizer.getInt(arrayLength - 11, 11) : randomizer.getInt(arrayLength - 1, 1);
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

            if (closestPerson == null)
                closestPerson.setText("?");
        } else {
            closestPerson = replaceTags("{string:person⦗" + typeOfSecondPerson + "⦘⸻⸮}");
            closestPerson.setText(StringUtils.replace(closestPerson.getText(), "%%", name));
        }
        thirdParty = replaceTags("{string:person⦗" + typeOfThirdPerson + "⦘⸻⸮}");
        thirdParty.setText(StringUtils.replace(thirdParty.getText(), "%%", closestPerson.getText()));
        thirdParty.setText(StringUtils.replace(thirdParty.getText(), " de el ", " del "));

        //Add unknown people
        for (int n = -1, limit = PROBABILITY_DISTRIBUTION[randomizer.getInt(PROBABILITY_DISTRIBUTION.length, 0)]; ++n < limit; ) {
            people.add(new Object[]{
                    getStringFromStringArray(R.array.person, 0) + " " + "<font color=" + COMMON_COLORS[n] + ">" + formatText(new String[]{Character.toString(letter)}, "", "b,i,tt") + "</font>",
                    getStringFromStringArray(R.array.probability) + " " + genderify(getStringFromStringArray(R.array.individual), randomizer.getInt(2, 1)).getText(),
                    true
            });
            letter++;
        }

        //Add identified people
        for (int n = -1, limit = randomizer.getInt(8, 3); ++n < limit; ) {
            if (randomizer.getInt(4, 0) > 0) {
                Entity entity = getEntity(supportedNames);
                people.add(new Object[]{
                        "<font color=" + getColorAsString() + ">" + formatText(new String[]{entity.getPrimitiveName()[0], entity.getPrimitiveName()[1]}, entity.getPrimitiveName()[2], "b") + "</font>",
                        sexes[entity.getSex() + 1],
                        false
                });
            } else
                people.add(new Object[]{
                        "<font color=" + getColorAsString() + ">" + formatText(new String[]{generateUsername()}, "", "b,tt") + "</font>",
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
                    descriptions[n] = "<font color=#B599FC>" + formatText(new String[]{descriptions[n]}, "", "b") + "</font>";
                descriptions[n] = " " + "(" + descriptions[n] + ")";
            }
        }

        //Define chains
        for (int n = 0; n < people.size() - 1; n++) {
            int karma = randomizer.getInt(21, -10);
            totalKarma = totalKarma + karma;

            if (((Boolean) people.get(n)[2]).booleanValue())
                chain += String.format(getResources().getString(R.string.chain_link), n + 1, String.valueOf(people.get(n)[0]) + descriptions[n], ",", getStringFromStringArray(R.array.uncertainty), String.valueOf(people.get(n + 1)[0]) + descriptions[n + 1], formatNumber(karma), formatNumber(totalKarma));
            else
                chain += String.format(getResources().getString(R.string.chain_link), n + 1, String.valueOf(people.get(n)[0]) + descriptions[n], "", "(" + getAction() + ")", String.valueOf(people.get(n + 1)[0]) + descriptions[n + 1], formatNumber(karma), formatNumber(totalKarma));

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
        chain += String.format(getResources().getString(R.string.chain_effect), "<b><font color=#FFF870>" + (totalKarma == 0 ? effect : String.format("%.2f", percentage) + "% " + effect) + "</font></b>", name);
        chain = StringUtils.replace(chain, " a el ", " al ");
        return String.format(getResources().getString(R.string.html_format), chain);
    }

    /* String methods :: String retrieval */

    public String getStringFromRes(@StringRes int id) {
        if (id != 0)
            try {
                return getString(id);
            } catch (Exception e) {
                return "";
            }
        else
            return "";
    }

    public String getStringFromResByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                String packageName = getPackageName();
                int resourceId = getResources().getIdentifier(s, "string", packageName);
                return getString(resourceId);
            } catch (Exception e) {
                return "";
            }
        } else
            return "";
    }

    public String getSplitString(@StringRes int id, int... index) {
        List<String> stringList = getStringAsList(id);

        try {
            if (verifyIntVararg(index)) {
                if (index[0] < 0 || index[0] >= stringList.size())
                    return stringList.get(0);
                else
                    return stringList.get(index[0]);
            } else {
                int position = randomizer.getInt(stringList.size(), 0);
                return stringList.get(position);
            }
        } catch (Exception e) {
            return "##[ERROR]";
        }
    }

    public String[] getStringAsArray() {
        String string = getStringFromRes(R.string.days_of_week);
        return string.split("¶[ ]*");
    }

    public List<String> getStringAsList(@StringRes int id) {
        String string = getStringFromRes(id);
        return Arrays.asList(string.split("¶[ ]*"));
    }

    public String getStringFromList(List<String> stringList, int... index) {
        if (stringList.size() > 0) {
            if (verifyIntVararg(index)) {
                if (index[0] < 0 || index[0] >= stringList.size())
                    return stringList.get(0);
                else
                    return stringList.get(index[0]);
            } else {
                int position = randomizer.getInt(stringList.size(), 0);
                return stringList.get(position);
            }
        } else
            return "";
    }

    public String getStringFromStringArray(String[] array) {
        if (array != null && array.length > 0) {
            int index = randomizer.getInt(array.length, 0);
            return array[index];
        } else
            return "";
    }

    public String getStringFromStringArray(@ArrayRes int id) {
        int length = getArrayLength(id);
        return getStringFromStringArray(id, randomizer.getInt(length, 0));
    }

    public String getStringFromStringArray(@ArrayRes int id, int index) {
        if (getResourceName(id) == null)
            return null;
        else {
            String[] stringArray = getResources().getStringArray(id);

            if (index < 0 || index >= stringArray.length)
                index = 0;
            return stringArray[index];
        }
    }

    public String getResourceName(@AnyRes int id) {
        return getResources().getResourceName(id);
    }

    public String getColorAsString() {
        return getStringFromList(colors);
    }

    /* String methods :: List or database retrieval */

    public String getPhrase() {
        if (defaultPreferences.getString("preference_language", "es").equals("es"))
            return myDB.selectPhrase(randomizer.getInt(myDB.countPhrases(), 1));
        else
            return myDB.selectEnglishPhrase(randomizer.getInt(myDB.countEnglishPhrases(), 1));
    }

    public String getAbstractNoun() {
        if (defaultPreferences.getString("preference_language", "es").equals("es"))
            return myDB.selectAbstractNoun(randomizer.getInt(myDB.countAbstractNouns(), 1));
        else
            return myDB.selectEnglishAbstractNoun(randomizer.getInt(myDB.countEnglishAbstractNouns(), 1));
    }

    private String getAction() {
        if (defaultPreferences.getString("preference_language", "es").equals("es"))
            return myDB.selectAction(randomizer.getInt(myDB.countActions(), 1));
        else
            return myDB.selectEnglishAction(randomizer.getInt(myDB.countEnglishActions(), 1));
    }

    private String getName() {
        return myDB.selectName(randomizer.getInt(myDB.countNames(), 1));
    }

    private String getMiddleName() {
        return getAChar(UPPERCASE_ALPHABET) + FULL_STOP;
    }

    private String getFamilyName() {
        return myDB.selectFamilyName(randomizer.getInt(myDB.countFamilyNames(), 1));
    }

    private String getMaleName() {
        return myDB.selectMaleName(randomizer.getInt(myDB.countMaleNames(), 1));
    }

    private String getFemaleName() {
        return myDB.selectFemaleName(randomizer.getInt(myDB.countFemaleNames(), 1));
    }

    private String getLastName() {
        return myDB.selectSurname(randomizer.getInt(myDB.countSurnames(), 1));
    }

    private String getEnglishMaleName() {
        return myDB.selectEnglishMaleName(randomizer.getInt(myDB.countEnglishMaleNames(), 1));
    }

    private String getEnglishFemaleName() {
        return myDB.selectEnglishFemaleName(randomizer.getInt(myDB.countEnglishFemaleNames(), 1));
    }

    private String getEnglishLastName() {
        return myDB.selectEnglishSurname(randomizer.getInt(myDB.countEnglishSurnames(), 1));
    }

    private String getEnglishAdjective() {
        return myDB.selectEnglishAdjective(randomizer.getInt(myDB.countEnglishAdjectives(), 1));
    }

    private String getEnglishNoun() {
        return englishNouns.get(randomizer.getInt(englishNouns.size() - 1, 0));
    }

    private String getEnglishOccupation() {
        return myDB.selectEnglishOccupation(randomizer.getInt(myDB.countEnglishOccupations(), 1));
    }

    private String getOccupation() {
        return myDB.selectOccupation(randomizer.getInt(myDB.countOccupations(), 1));
    }

    private String getCommonNoun() {
        return myDB.selectCommonNoun(randomizer.getInt(myDB.countCommonNouns(), 1));
    }

    private String getUsername() {
        return myDB.selectUsername(randomizer.getInt(myDB.countUsernames(), 1));
    }

    private String getSpanishNoun() {
        return String.valueOf(getSpanishNoun(randomizer.getInt(spanishNouns.size() - 1, 0), randomizer.getBoolean() ? null : true)[0]);
    }

    private Object[] getSpanishNoun(Integer index, Boolean complete) {
        if (index != null && (index < 0 || index >= spanishNouns.size()))
            index = 0;

        String noun = spanishNouns.get(index == null ? randomizer.getInt(spanishNouns.size(), 0) : index), temp;
        int position = 4;
        String[] indefinite_articles = {"un", "unos", "una", "unas", "uno", ""};
        Object[][] articles = {
                {"el", 1, false},
                {"los", 1, true},
                {"la", 2, false},
                {"las", 2, true},
                {"lo", 0, false},
                {"", 0, false}
        };

        for (int n = -1, length = articles.length; ++n < length - 1; ) {
            if (noun.startsWith(String.valueOf(articles[n][0]) + " ")) {
                position = n;
                temp = noun.replaceFirst(articles[n][0] + "\\s+", "");

                if (n == 0 && (temp.startsWith("a") || temp.startsWith("á")) && temp.endsWith("a"))
                    articles[n][1] = 2;

                if (complete == null)
                    noun = StringUtils.replaceOnce(noun, String.valueOf(articles[position][0]), indefinite_articles[position]);
                else if (!complete)
                    noun = temp;
                n = length;
            }
        }
        noun = noun.trim();
        return new Object[]{noun, articles[position][1], articles[position][2]};
    }

    private String getSpanishAdjective(int sex, boolean plural) {
        String adjective = "";
        int tries = 0;
        boolean proper = false;

        if (sex < 0 || sex > 2)
            sex = 0;

        while (!proper) {
            if (plural)
                adjective = spanishPluralAdjectives.get(randomizer.getInt(spanishPluralAdjectives.size(), 0));
            else
                adjective = spanishSingularAdjectives.get(randomizer.getInt(spanishSingularAdjectives.size(), 0));

            if (sex == 0 && adjective.contains("AQ0C"))
                proper = true;
            else if (sex == 1 && (adjective.contains("AQ0C") || adjective.contains("AQ0M")))
                proper = true;
            else if (sex == 2 && (adjective.contains("AQ0C") || adjective.contains("AQ0F")))
                proper = true;
            else {
                if (tries > 9999)
                    proper = true;
            }
            tries++;
        }
        adjective = adjective.replaceFirst("\\s*(AQ0(C|F|M)[A-Z\\d]{2})", "");
        return adjective;
    }

    public String getFortuneCookie() {
        if (defaultPreferences.getString("preference_language", "es").equals("es"))
            return myDB.selectFortuneCookie(randomizer.getInt(myDB.countFortuneCookies(), 1));
        else
            return myDB.selectEnglishFortuneCookie(randomizer.getInt(myDB.countEnglishFortuneCookies(), 1));
    }

    public String getDivination() {
        String divination = "?";
        float probability = randomizer.getFloat();
        int sex = -1;

        if (probability <= 0.45F) {
            if (defaultPreferences.getString("preference_language", "es").equals("es"))
                divination = myDB.selectDivination(randomizer.getInt(myDB.countDivinations(), 1));
            else
                divination = myDB.selectEnglishDivination(randomizer.getInt(myDB.countEnglishDivinations(), 1));
            divination = replaceTags(divination).getText();
        } else if (probability <= 0.9F) {
            int days = 0;
            boolean plural = false;
            boolean done = false;
            TextComponent component;
            String segment;

            //Define indices
            List<Integer> indices = new ArrayList<>();
            indices.add(randomizer.getInt(15, 0) > 0 ? randomizer.getInt(getArrayLength(R.array.divination_start) - 1, 1) : 0);
            indices.add(randomizer.getInt(getArrayLength(R.array.divination_middle), 0));
            indices.add(randomizer.getInt(getArrayLength(R.array.divination_end) - (indices.get(1) > 0 ? 0 : 10), 0));
            indices.add(getArrayLength(R.array.divination_cause) - 1 - indices.get(2) <= 1 ? 0 : randomizer.getInt(getArrayLength(R.array.divination_cause), 0));

            //Get segments for the divination
            List<String> segments = new ArrayList<>();
            segments.add(getStringFromStringArray(R.array.divination_start, indices.get(0)));
            segments.add(getStringFromStringArray(R.array.divination_middle, indices.get(1)));
            segments.add(getStringFromStringArray(R.array.divination_end, indices.get(2)));
            segments.add(getStringFromStringArray(R.array.divination_cause, indices.get(3)));

            //Format start of divination
            segment = StringUtils.replaceOnce(segments.get(0), "‽1", String.valueOf(days = randomizer.getGaussianInt(5, 7, 1)));
            segment = StringUtils.replaceOnce(segment, "‽2", addDaysToDate(preferences.getString("temp_enquiryDate", UNKNOWN_DATE), days));
            segments.set(0, segment);
            preferences.edit().remove("temp_enquiryDate").commit();

            //Format middle of divination
            segment = (component = replaceTags(segments.get(1))).getText();

            if (component.getHegemonicSex() != null && component.getHegemonicSex() != -1)
                sex = component.getHegemonicSex();

            if (!segment.equals(segment = RegExUtils.replaceAll(segment, "\\s*˧˟\\s*", "")))
                plural = true;
            segments.set(1, segment);

            //Format end of divination
            segment = segments.get(2);
            segment = StringUtils.replace(segment, "＃", String.valueOf(sex));
            segment = replaceTags(segment).getText();

            if (defaultPreferences.getString("preference_language", "es").equals("es")) {
                Character c = '\0';

                if (!plural)
                    segment = segment.replaceAll("¦\\p{L}+¦", "");
                else {
                    segment = StringUtils.replace(segment, "¦", "");
                    c = 'e';
                }
                segment = StringUtils.replaceOnce(segment, "⁞", c.toString());
            }
            segments.set(2, segment);

            //Format cause of divination
            segment = segments.get(3);

            if (!segment.isEmpty()) {
                segment = StringUtils.replaceOnce(segment, "{}", getStringFromStringArray(new String[]{"{string:unspecific_person⸻⸮}", "{string:person⸻⸮}", "{string:individual⸻⸮}", "{string:individual⸻⸮}", "˧*", "˧*", "{string:explicit_cause⸻⸮}", "{string:explicit_cause⸻⸮}", "{string:explicit_cause⸻⸮}", "{string:explicit_cause⸻⸮}"}));
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

                    if (StringUtils.contains(segment, "˧*")) {
                        String replacement;
                        int generated;

                        while (indices.get(1) == (generated = randomizer.getInt(getArrayLength(R.array.divination_middle) - 1, 1))) {
                        }
                        replacement = replaceTags(getStringFromStringArray(R.array.divination_middle, generated)).getText();
                        replacement = RegExUtils.replaceFirst(replacement, "\\s*˧˟\\s*", "");
                        segment = RegExUtils.replaceFirst(segment, "\\s*˧\\*\\s*", " " + replacement);
                    }

                    if (segment.endsWith("꘎")) {
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
            divination = RegExUtils.replaceAll(divination, "\\s*˧˟\\s*", " ");
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
            points = randomizer.getInt(remainingPoints + 1, 0);
            remainingPoints -= points;
            pair = hashMap.get(currentEmotion = emotions[randomizer.getInt(emotions.length, 0)]);
            pair.setSubValue(pair.getSubValue() + points);
            hashMap.put(currentEmotion, pair);
        }

        for (Map.Entry<String, Pair<Integer, Integer>> entry : hashMap.entrySet()) {
            if (entry.getValue().getSubValue() > 0)
                s += (s.isEmpty() ? "" : "<br>") + getEmojiByUnicode(getIntFromIntArray(R.array.feelings, entry.getValue().getSubKey())) + " " + entry.getKey() + " (" + entry.getValue().getSubValue() + "%)";
        }
        return s;
    }

    /* String methods :: Formatting */

    public String normalize(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String capitalizeFirst(String s) {
        if (!s.isEmpty()) {
            if (Character.isLowerCase(s.charAt(0)))
                return s.substring(0, 1).toUpperCase() + s.substring(1);
            else
                return s;
        } else return "";
    }

    public String swapFirstToLowercase(String s) {
        if (!s.isEmpty()) {
            if (Character.isUpperCase(s.charAt(0)))
                return s.substring(0, 1).toLowerCase() + s.substring(1);
            else
                return s;
        } else return "";
    }

    public TextComponent replaceTags(String s, int... predefinedSex) {
        if (s == null)
            return new TextComponent();
        else {
            TextComponent component = new TextComponent();
            Integer sex = null;
            int defaultSex;
            boolean defaulted = false;
            boolean nullified = false;

            if (defaulted = verifyIntVararg(predefinedSex))
                defaultSex = predefinedSex[0];
            else
                defaultSex = randomizer.getInt(4, -1);

            //Verifies if the text contains curly brackets
            if (s.contains("{") && s.contains("}")) {
                int pairsOfBrackets = 0;
                int counter = 0;
                boolean interrupted = false;

                //Counts the possible pairs of curly brackets within the text
                for (int n = -1, length = s.length(); ++n < length && !interrupted; ) {
                    boolean concluded = false;

                    if (s.charAt(n) == '{')
                        counter++;
                    else if (s.charAt(n) == '}') {
                        counter--;
                        concluded = true;
                    }

                    if (counter < 0)
                        interrupted = true;
                    else if (concluded)
                        pairsOfBrackets++;
                }

                if (interrupted) {
                    int openingBrackets = StringUtils.countMatches(s, "{");
                    int closingBrackets = StringUtils.countMatches(s, "}");
                    pairsOfBrackets = openingBrackets < closingBrackets ? openingBrackets : closingBrackets;
                }

                //Replaces simple tags within the text, if there are any
                if (pairsOfBrackets > 0 && TAG_PATTERN.matcher(s).find()) {
                    List<String> matches = extractMatches(s, TAG_PATTERN);

                    for (int n = 0; n < matches.size(); n++) {
                        String replacement = "";
                        String resourceName = RegExUtils.replaceAll(matches.get(n), TAG_COMPONENT_PATTERN, "");
                        String resourceType;
                        sex = getTrueSex(matches.get(n), defaultSex);
                        Integer index = null;
                        boolean empty = false;

                        try {
                            Matcher m = Pattern.compile("⦗\\d+⦘").matcher(matches.get(n));

                            while (m.find()) {
                                String match = m.group();
                                match = RegExUtils.replaceAll(match, "\\D+", "");
                                index = Integer.valueOf(match);
                            }
                        } catch (Exception e) {
                            index = -1;
                        }

                        if (StringUtils.startsWith(matches.get(n), "{string:")) {
                            int resourceId = getArrayResourceId(resourceName);
                            resourceId = (resourceId == 0 ? getStringResourceId(resourceName) : resourceId);

                            if (resourceId != 0) {
                                if ((resourceType = getResources().getResourceTypeName(resourceId)).equals("array"))
                                    replacement = index != null && index >= 0 ? getStringFromStringArray(resourceId, index) : getStringFromStringArray(resourceId);
                                else if (resourceType.equals("string"))
                                    replacement = index != null && index >= 0 ? getSplitString(resourceId, index) : getSplitString(resourceId);
                            }
                        } else if (StringUtils.startsWith(matches.get(n), "{database:"))
                            replacement = index != null && index >= 0 ? myDB.selectUnknownRow(resourceName, index) : myDB.selectUnknownRow(resourceName, randomizer.getInt(myDB.countUnknownRow(resourceName), 1));
                        else if (StringUtils.startsWith(matches.get(n), "{method:"))
                            replacement = callMethod(resourceName);
                        int openingIndex = StringUtils.indexOf(replacement, '{');
                        int closingIndex;

                        if (openingIndex >= 0 && (closingIndex = StringUtils.indexOf(replacement, '}')) >= 0 && openingIndex < closingIndex) {
                            TextComponent tempWord;

                            if (defaulted)
                                tempWord = replaceTags(replacement, defaultSex);
                            else
                                tempWord = replaceTags(replacement);
                            replacement = tempWord.getText();
                            sex = tempWord.getHegemonicSex();
                            empty = replacement.isEmpty() || tempWord.isNullified();
                        }

                        if (empty)
                            s = RegExUtils.replaceFirst(s, "\\s*" + Pattern.quote(matches.get(n)), "");
                        else
                            s = StringUtils.replaceOnce(s, matches.get(n), genderify(replacement, sex).getText());
                        pairsOfBrackets--;
                    }
                }

                //Replaces ‘word’ tags within the text, if there are any
                if (pairsOfBrackets > 0 && WORD_TAG_PATTERN.matcher(s).find()) {
                    List<String> matches = extractMatches(s, WORD_TAG_PATTERN);

                    for (int n = 0; n < matches.size(); n++) {
                        String replacement = RegExUtils.replaceAll(matches.get(n), WORD_COMPONENT_PATTERN, "");
                        sex = getTrueSex(matches.get(n), defaultSex);
                        replacement = genderify(replacement, sex).getText();
                        s = StringUtils.replaceOnce(s, matches.get(n), replacement);
                        pairsOfBrackets--;
                    }
                }

                //Replaces ‘random’ tags within the text, if there are any
                if (pairsOfBrackets > 0 && RANDOM_TAG_PATTERN.matcher(s).find()) {
                    List<String> matches = extractMatches(s, RANDOM_TAG_PATTERN);

                    for (int n = 0; n < matches.size(); n++) {
                        String substring = StringUtils.removeStart(matches.get(n), "{rand:");
                        substring = StringUtils.removeEnd(substring, "}");
                        List<String> items = Arrays.asList(substring.split("\\s*;\\s*"));

                        if (items != null && items.size() > 0) {
                            String replacement;
                            String regex;

                            if ((replacement = getStringFromList(items)).trim().equals("∅")) {
                                replacement = "";
                                regex = "\\s*" + Pattern.quote(matches.get(n));
                                nullified = true;
                            } else
                                regex = Pattern.quote(matches.get(n));
                            s = RegExUtils.replaceFirst(s, regex, replacement);
                            pairsOfBrackets--;
                        }
                    }
                }
            }
            Pair<String, List<Integer>> pair = replaceDigitTags(s);

            if (pair.getSubValue() != null && pair.getSubValue().size() > 0 && !s.equals(pair.getSubKey()))
                sex = pair.getSubValue().get(pair.getSubValue().size() - 1);
            s = pair.getSubKey();

            //Replaces subtags within the text, if there are any
            if (s.contains("⸠") && s.contains("⸡")) {
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
            if (!s.isEmpty() && s.contains("｢") && s.contains("｣")) {
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

    public String fixDoubleDot(String s) {
        if (Pattern.compile(DOUBLE_DOT_REGEX).matcher(s).find()) {
            ArrayList<Integer> positions = new ArrayList<>();
            Matcher m = DOUBLE_DOT_PATTERN.matcher(s);

            while (m.find()) {
                positions.add(m.end());
            }

            if (positions.size() > 0) {
                for (int n = positions.size() - 1; n >= 0; n--) {
                    char[] charArray = s.toCharArray();
                    charArray[positions.get(n) - 1] = '\0';
                    s = String.valueOf(charArray);
                }
            }
            return s;
        } else
            return s;
    }

    private int getTrueSex(String s, int defaultSex) {
        int definedSex;

        if (defaultSex < -1 || defaultSex > 2)
            defaultSex = -1;

        if (StringUtils.contains(s, "⛌"))
            definedSex = getSex();
        else if (StringUtils.contains(s, "⸮"))
            definedSex = randomizer.getInt(2, 1);
        else {
            List<String> matches = extractMatches(s, SEX_APPENDIX_PATTERN);

            if (matches != null && matches.size() > 0) {
                matches.set(0, StringUtils.replace(matches.get(0), "⸻", ""));
                definedSex = Integer.parseInt(matches.get(0));

                if (definedSex < 0 || definedSex > 2)
                    definedSex = 0;
            } else
                definedSex = defaultSex;
        }
        return definedSex;
    }

    public static String formatText(String[] array, String ending, String format) {
        if (array == null)
            array = new String[]{""};

        if (ending == null)
            ending = "";
        String formattedName = "";

        for (int n = 0; n < array.length; n++) {
            formattedName += array[n];

            if (n + 1 < array.length) {
                if (!array[n].isEmpty() && !array[n + 1].isEmpty())
                    formattedName += " ";
            }
        }
        formattedName += !ending.isEmpty() ? " " + "<u>" + ending + "</u>" : "";

        if (format.matches("^((a|b|big|i|s|small|tt|u),)*(a|b|big|i|s|small|tt|u)$")) {
            List<String> parts;

            if (format.contains(","))
                parts = Arrays.asList(format.split(Pattern.quote(",")));
            else {
                parts = new ArrayList<>();
                parts.add(format);
            }

            if (parts.contains("a"))
                formattedName = "<a>" + formattedName + "</a>";

            if (parts.contains("b"))
                formattedName = "<b>" + formattedName + "</b>";

            if (parts.contains("big"))
                formattedName = "<big>" + formattedName + "</big>";

            if (parts.contains("i"))
                formattedName = "<i>" + formattedName + "</i>";

            if (parts.contains("s"))
                formattedName = "<s>" + formattedName + "</s>";

            if (parts.contains("small"))
                formattedName = "<small>" + formattedName + "</small>";

            if (parts.contains("tt"))
                formattedName = "<tt>" + formattedName + "</tt>";

            if (parts.contains("u"))
                formattedName = "<u>" + formattedName + "</u>";
        }
        return formattedName;
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

            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }

    public String getColor(String string) {
        String color;

        if (string.equals("") || string.isEmpty())
            color = String.format("#%06X", (0xFFFFFF & Color.GRAY));
        else {
            color = String.format("#%X", string.hashCode());

            while (color.length() < 7)
                color = "#" + StringUtils.replaceOnce(color, "#", Character.toString(getAChar(HEX_DIGITS)));
            color = color.substring(0, 7);
        }
        return color;
    }

    /* char methods */

    private char getAChar(String s) {
        if (s != null && s.length() > 0)
            return s.charAt(randomizer.getInt(s.length(), 0));
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
        if (!preferences.contains("temp_sex"))
            return randomizer.getInt(3, 0);
        else
            return preferences.getInt("temp_sex", -1);
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

    private int getArrayLength(@ArrayRes int id) {
        try {
            return getResources().getStringArray(id).length;
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

        if (Arrays.asList(locales).contains(locale))
            return true;
        else
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

    public Entity getEntity(List<NameEnum> supportedNames, String... subtype) {
        Entity entity = new Entity();
        String[] primitiveName = new String[]{"", "", ""};
        String[] names;
        int sex;
        int iterations = 1;
        Integer[] resArray = null;
        NameEnum nameType = NameEnum.EMPTY;
        Boolean ending = null;

        if (supportedNames != null && supportedNames.size() > 2) {
            nameType = supportedNames.get(randomizer.getInt(supportedNames.size() - 2, 2));

            if (verifyVararg(subtype))
                nameType.setSubtype(subtype[0]);

            switch (nameType) {
                case EMPTY: //Empty name
                    break;
                case TEST_CASE: //Test case
                    primitiveName[0] = "┤de la ┤Paz";
                    break;
                case NAME:
                    primitiveName[0] = getStringFromList(commonNames);
                    primitiveName[1] = (randomizer.getBoolean() ? getStringFromList(commonLastNames) + " " : "") + getStringFromList(commonLastNames);
                    break;
                case FORENAME:
                    primitiveName[0] = getStringFromList(commonNames);
                    break;
                case SURNAME:
                    primitiveName[1] = getStringFromList(commonLastNames);
                    entity.setSex(0);
                    break;
                case COMPOUND_SURNAME:
                    primitiveName[1] = getCompoundSurname();
                    entity.setSex(0);
                    break;
                case DOUBLE_BARRELLED_SURNAME:
                    primitiveName[1] = getDoubleBarrelledSurname();
                    entity.setSex(0);
                    break;
                case INTERNATIONAL_NAME:
                    primitiveName[0] = getName() + (randomizer.getFloat() <= 0.25F ? " " + getMiddleName() : "");
                    primitiveName[1] = getFamilyName();
                    break;
                case INTERNATIONAL_FORENAME:
                    primitiveName[1] = getName();
                    break;
                case INTERNATIONAL_SURNAME:
                    primitiveName[1] = getFamilyName();
                    entity.setSex(0);
                    break;
                case ENGLISH_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getEnglishMaleName() : getEnglishFemaleName() + (randomizer.getBoolean() ? " " + getMiddleName() : "");
                    primitiveName[1] = getEnglishLastName();
                    break;
                case ENGLISH_DOUBLE_BARRELLED_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getEnglishMaleName() : getEnglishFemaleName();
                    primitiveName[1] = getDoubleBarrelledSurname();
                    break;
                case ENGLISH_FORENAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getEnglishMaleName() : getEnglishFemaleName();
                    break;
                case ENGLISH_SURNAME:
                    primitiveName[1] = getEnglishLastName();
                    entity.setSex(0);
                    break;
                case SPANISH_COMPOUND_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getMaleName() : getFemaleName();
                    primitiveName[1] = getCompoundSurname();
                    break;
                case SPANISH_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = getNames(sex);
                    primitiveName[1] = getLastNames();
                    break;
                case SPANISH_FORENAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = getNames(sex);
                    break;
                case SPANISH_GIVEN_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getMaleName() + " " + getMaleName() : getFemaleName() + " " + getFemaleName();
                    break;
                case SPANISH_SURNAME:
                    primitiveName[1] = getLastNames();
                    entity.setSex(0);
                    break;
                case JAPANESE_NAME:
                    float probability = randomizer.getFloat();

                    if (probability < 0.2F)
                        ending = true;
                    else if (probability < 0.4F)
                        ending = false;

                    if (ending == null || !ending) {
                        sex = randomizer.getInt(2, 1);
                        entity.setSex(sex);
                        primitiveName[0] = sex == 1 ? getStringFromStringArray(R.array.japanese_male_names) : getStringFromStringArray(R.array.japanese_female_names);
                    }

                    if (ending == null || ending)
                        primitiveName[1] = getStringFromStringArray(R.array.japanese_last_names);
                    break;
                case MEXICAN_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getStringFromStringArray(R.array.mexican_male_names) : getStringFromStringArray(R.array.mexican_female_names);
                    primitiveName[1] = getStringFromStringArray(R.array.mexican_last_names) + " " + getStringFromStringArray(R.array.mexican_last_names);
                    break;
                case RUSSIAN_NAME:
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);
                    primitiveName[0] = sex == 1 ? getStringFromStringArray(R.array.russian_male_names) : getStringFromStringArray(R.array.russian_female_names);
                    primitiveName[1] = getStringFromStringArray(R.array.russian_family_names);
                    break;
                case GENERATED_NATURAL_NAME:
                    primitiveName[0] = generateName(randomizer.getInt(6, 1));
                    break;
                case GENERATED_DEFINED_NAME:
                    int type = randomizer.getInt(GENERATED_NAME_FAMILY_NAME_SUFFIX.length, 0);
                    names = generateFullName(type == 1, type == 1, type);
                    primitiveName[0] = names[0];
                    primitiveName[1] = names[1];
                    break;
                case GENERATED_MYSTIC_NAME:
                    names = getGeneratedNames(randomizer.getInt(2, 1), nameType.getSubtype());
                    primitiveName[0] = names[0];

                    if (names.length == 2)
                        primitiveName[1] = names[1];
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
                    sex = randomizer.getInt(2, 1);
                    entity.setSex(sex);

                    for (int n = 0; n < iterations; n++) {
                        primitiveName[0] += !primitiveName[0].isEmpty() ? " " : "";
                        primitiveName[0] += sex == 1 ? getStringFromStringArray(resArray[0]) : getStringFromStringArray(resArray[1]);
                        primitiveName[1] += !primitiveName[1].isEmpty() ? " " : "";
                        primitiveName[1] += getStringFromStringArray(resArray[2]);

                        if (!StringUtils.containsWhitespace(primitiveName[1]) && randomizer.getInt(100, 0) == 0)
                            primitiveName[1] += SEPARATOR[0] + getStringFromStringArray(resArray[2]);
                    }
                    break;
                default:
                    break;
            }
        }
        entity.setNameType(nameType);

        //Remove unneeded words from name
        if (primitiveName[0].contains("┤")) {
            if (primitiveName[0].startsWith("┤"))
                primitiveName[0] = RegExUtils.replaceFirst(primitiveName[0], "┤[^┤]+┤", "");
            primitiveName[0] = StringUtils.replace(primitiveName[0], "┤", "");
        }

        //Capitalize only the first letter of forename/surname, if needed
        if (primitiveName[0] != null && !primitiveName[0].isEmpty())
            primitiveName[0] = capitalizeFirst(primitiveName[0]);

        if (primitiveName[1] != null && !primitiveName[1].isEmpty() && primitiveName[0].isEmpty())
            primitiveName[1] = capitalizeFirst(primitiveName[1]);

        //Set suffix, if possible
        if (randomizer.getInt(10, 0) == 0) {
            if (randomizer.getInt(3, 0) == 0)
                primitiveName[2] = getSplitString(R.string.generational_designation);
            else
                primitiveName[2] = getStringFromList(romanNumerals);
        }
        entity.setPrimitiveName(primitiveName);
        return entity;
    }

    public Person getPerson() {
        Person person = new Person();

        //Set basic information
        Entity entity = getEntity(supportedNames);
        person.setSex(entity.getSex());
        person.setNameType(entity.getNameType());
        person.setForename(entity.getPrimitiveName()[0]);
        person.setLastName(entity.getPrimitiveName()[1]);
        person.setSuffix(entity.getPrimitiveName()[2]);

        //Set username
        person.setUsername(generateUsername());

        //Set title of honor, if possible
        if (randomizer.getBoolean()) {
            if (person.getSex() == 1 || person.getSex() == 2)
                person.setTitleOfHonor(getTitleOfHonor(person.getSex()));
            else if (person.getForename().isEmpty())
                person.setTitleOfHonor(getTitleOfHonor(person.getSex(), true));
            else
                person.setTitleOfHonor(getTitleOfHonor(person.getSex(), false));
        }

        //Set japanese honorific, if possible
        if (person.getTitleOfHonor().isEmpty() && randomizer.getBoolean() && person.getSuffix().isEmpty() && (person.getForename().isEmpty() ^ person.getLastName().isEmpty()) && person.getNameType() == NameEnum.JAPANESE_NAME)
            person.setJapaneseHonorific(SEPARATOR[0] + getSplitString(R.string.japanese_honorifics));

        //Set post-nominal letters, if possible and if the person doesn't have a title of honor
        if (person.getTitleOfHonor().isEmpty() && randomizer.getBoolean())
            person.setPostNominalLetters(getStringFromList(postNominalLetters));

        //Set random birthdate
        person.setBirthdate(randomizer.getDate());
        return person;
    }

    /* Unicode, ASCII (and other related characters) methods */

    public String getRandomEmoji(short repetitionRate, short rateDecay, int... possibilityLimit) {
        int limit = 0;

        if (verifyIntVararg(possibilityLimit))
            limit = possibilityLimit[0];

        if (limit < 0)
            limit = 0;

        if (limit == 0) {
            return getEmojiSet(repetitionRate, rateDecay);
        } else {
            int possibility = randomizer.getInt(limit, 0);

            if (possibility == 0)
                return getEmojiSet(repetitionRate, rateDecay);
            else
                return "";
        }
    }

    private String getEmojiSet(short repetitionRate, short rateDecay) {
        Resources r = getResources();
        int[] emojis = r.getIntArray(R.array.emojis);
        int emoji = emojis[randomizer.getInt(emojis.length, 0)];
        short rate = repetitionRate;
        short decay = rateDecay;
        String emojiString = "";

        if (rate < 0)
            rate = 0;
        else if (rate > 100)
            rate = (short) 100;

        if (decay < 1)
            decay = (short) 10;
        else if (decay > 100)
            decay = (short) 100;

        emojiString += getEmojiByUnicode(emoji);

        while (rate > 0) {
            int repetition = randomizer.getInt(101, 0);

            if (repetition <= rate) {
                emoji = emojis[randomizer.getInt(emojis.length, 0)];
                emojiString += getEmojiByUnicode(emoji); //Repeat emoji, if possible
            }

            if (rate < decay)
                rate = (short) 0;
            else
                rate = (short) (rate - decay);
        }
        return emojiString;
    }

    public String getEmojiByUnicode(int unicode) {
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
                return false;
            }
        } else
            return false;
    }

    public final boolean verifyIntVararg(int... vararg) {
        int value;

        if (vararg != null) {
            try {
                value = vararg[0];
                return true;
            } catch (Exception e) {
                return false;
            }
        } else
            return false;
    }

    public final boolean verifyBooleanVararg(boolean... vararg) {
        boolean value;

        if (vararg != null) {
            try {
                value = vararg[0];
                return true;
            } catch (Exception e) {
                return false;
            }
        } else
            return false;
    }

    public final boolean verifyCharVararg(char... vararg) {
        char value;

        if (vararg != null) {
            try {
                value = vararg[0];
                return true;
            } catch (Exception e) {
                return false;
            }
        } else
            return false;
    }

    /* Date\time methods */

    public static Calendar getCalendarInstance() {
        return Calendar.getInstance();
    }

    public static int getHourOfDay() {
        Calendar c = getCalendarInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getYear() {
        Calendar c = getCalendarInstance();
        return c.get(Calendar.YEAR);
    }

    public String getDayOfWeekName() {
        Calendar c = getCalendarInstance();
        String[] days = getStringAsArray();
        return days[c.get(Calendar.DAY_OF_WEEK)];
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
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return simpleDateFormat.format(c.getTime());
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

            if (defaultPreferences.getString("preference_language", "es").equals("es")) {
                symbols.setShortMonths(new String[]{"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"});
                symbols.setMonths(new String[]{"enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"});
            }

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

                    if (defaultPreferences.getString("preference_language", "es").equals("en")) {
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

    public static int[] getCurrentDate() {
        int[] date = new int[]{0, 0, 0};
        Calendar c = getCalendarInstance();
        date[0] = c.get(Calendar.YEAR);
        date[1] = c.get(Calendar.MONTH);
        date[2] = c.get(Calendar.DAY_OF_MONTH);
        return date;
    }

    public static String getCurrentDateTime() {
        return java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }

    public static long getDifferenceInDays(String date, String anotherDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date1 = simpleDateFormat.parse(date);
            Date date2 = simpleDateFormat.parse(anotherDate);
            long difference = Math.abs(date1.getTime() - date2.getTime());
            return TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            return 0L;
        }
    }

    public String getTimeBetweenDates(String date, String anotherDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date1 = simpleDateFormat.parse(date);
            Date date2 = simpleDateFormat.parse(anotherDate);
            Date tempDate;

            if (date2.getTime() < date1.getTime()) {
                tempDate = date2;
                date2 = date1;
                date1 = tempDate;
            }
            Calendar fromDate = Calendar.getInstance();
            Calendar toDate = Calendar.getInstance();
            fromDate.setTime(date1);
            toDate.setTime(date2);
            int increment = 0;
            int year, month, day;

            if (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)) {
                increment = fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            }

            //Day calculation
            if (increment != 0) {
                day = (toDate.get(Calendar.DAY_OF_MONTH) + increment) - fromDate.get(Calendar.DAY_OF_MONTH);
                increment = 1;
            } else {
                day = toDate.get(Calendar.DAY_OF_MONTH) - fromDate.get(Calendar.DAY_OF_MONTH);
            }

            //Month calculation
            if ((fromDate.get(Calendar.MONTH) + increment) > toDate.get(Calendar.MONTH)) {
                month = (toDate.get(Calendar.MONTH) + 12) - (fromDate.get(Calendar.MONTH) + increment);
                increment = 1;
            } else {
                month = (toDate.get(Calendar.MONTH)) - (fromDate.get(Calendar.MONTH) + increment);
                increment = 0;
            }

            //Year calculation
            year = toDate.get(Calendar.YEAR) - (fromDate.get(Calendar.YEAR) + increment);
            return year + " " + getString(R.string.time_years) + "," + " " + month + " " + getString(R.string.time_months) + "," + " " + day + " " + getString(R.string.time_days);
        } catch (ParseException e) {
            return "?";
        }
    }

    public String addDaysToDate(String date, int days) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();

        if (days < 0)
            days = 0;

        try {
            c.setTime(simpleDateFormat.parse(date));
            c.add(Calendar.DATE, days);
            return simpleDateFormat.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return UNKNOWN_DATE;
        }
    }

    /* Reflection methods */

    public String callMethod(String methodName) {
        String s = "?";

        try {
            Class methodsClass = Class.forName("com.app.memoeslink.adivinador.Methods");
            Constructor constructor = methodsClass.getConstructor(Context.class);
            Object object = constructor.newInstance(getBaseContext());
            Method method = methodsClass.getDeclaredMethod(methodName);
            method.setAccessible(true);
            s = (String) method.invoke(object);
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return s;
    }

    /* SharedPreferences methods */

    public void deleteTemp() {
        if (preferences.contains("temp_changeFortuneTeller"))
            preferences.edit().remove("temp_changeFortuneTeller").commit();

        if (preferences.contains("temp_busy"))
            preferences.edit().remove("temp_busy").commit();

        if (preferences.contains("temp_enquiryDate"))
            preferences.edit().remove("temp_enquiryDate").commit();

        if (preferences.contains("temp_restartActivity"))
            preferences.edit().remove("temp_restartActivity").commit();

        if (!defaultPreferences.getBoolean("preference_keepForm", false))
            clearForm();
    }

    public void clearForm() {
        if (preferences.contains("temp_name"))
            preferences.edit().remove("temp_name").commit();

        if (preferences.contains("temp_formatted_name"))
            preferences.edit().remove("temp_formatted_name").commit();

        if (preferences.contains("temp_sex"))
            preferences.edit().remove("temp_sex").commit();

        if (preferences.contains("temp_date_year"))
            preferences.edit().remove("temp_date_year").commit();

        if (preferences.contains("temp_date_month"))
            preferences.edit().remove("temp_date_month").commit();

        if (preferences.contains("temp_date_day"))
            preferences.edit().remove("temp_date_day").commit();

        if (preferences.contains("temp_user"))
            preferences.edit().remove("temp_user").commit();

        if (preferences.contains("temp_anonymous"))
            preferences.edit().remove("temp_anonymous").commit();
    }

    /* Activity-related methods */

    public static void lockScreenOrientation(Activity activity) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        // Search for the natural position of the device
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) || configuration.orientation == Configuration.ORIENTATION_PORTRAIT && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
            switch (rotation) { //Natural position is Landscape
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        } else {
            switch (rotation) { //Natural position is Portrait
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
            }
        }
    }

    public static void unlockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static void setScreenVisibility(Activity activity, boolean alwaysActive) {
        if (alwaysActive)
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            activity.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    /* Sound methods */

    public boolean playSound(String soundResource) {
        return playSound(Methods.this, defaultPreferences, soundResource);
    }

    public static boolean playSound(Context context, SharedPreferences defaultPreferences, String soundResource) {
        if (defaultPreferences == null)
            defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (defaultPreferences.getBoolean("preference_audioEnabled", false) && defaultPreferences.getBoolean("preference_soundsEnabled", false)) {
            if (soundResource != null) {
                try {
                    int soundID = context.getResources().getIdentifier(soundResource, "raw", context.getPackageName());
                    mediaPlayer = MediaPlayer.create(context, soundID);

                    try {
                        mediaPlayer.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            mp.release();
                        }
                    });
                    mediaPlayer.start();
                    return false;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            } else
                return false;
        } else
            return false;
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

    public static void showSimpleToast(Context context, String text) {
        if (toast != null) {
            if (toast.getView().isShown()) {
                toast.cancel();
                toast = null;
            }
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        playSound(context, null, "computer_chimes");
        toast.show();
    }

    public static boolean setTTS(TextToSpeech tts, String language) {
        if (tts != null) {
            List<String> regionNames = new ArrayList<>();
            Locale locale = new Locale(language);
            Locale[] locales = locale.getAvailableLocales();

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
                    } while (c != null && c.moveToNext());
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

    public List<NameEnum> getPermittedNames(List<NameEnum> permittedNames, boolean complete) {
        if (permittedNames != null)
            permittedNames.clear();
        else
            permittedNames = new ArrayList<>();

        //Add default name types
        permittedNames.add(NameEnum.EMPTY);
        permittedNames.add(NameEnum.TEST_CASE);
        permittedNames.add(NameEnum.NAME);
        permittedNames.add(NameEnum.FORENAME);
        permittedNames.add(NameEnum.SURNAME);
        permittedNames.add(NameEnum.COMPOUND_SURNAME);
        permittedNames.add(NameEnum.DOUBLE_BARRELLED_SURNAME);
        permittedNames.add(NameEnum.INTERNATIONAL_NAME);
        permittedNames.add(NameEnum.INTERNATIONAL_FORENAME);
        permittedNames.add(NameEnum.INTERNATIONAL_SURNAME);

        if (getLanguage().equals("en") || getSystemLanguage().equals("en") || complete) {
            for (int n = -1; ++n < 4; ) {
                permittedNames.add(NameEnum.ENGLISH_NAME);
            }
            permittedNames.add(NameEnum.ENGLISH_DOUBLE_BARRELLED_NAME);
            permittedNames.add(NameEnum.ENGLISH_FORENAME);
            permittedNames.add(NameEnum.ENGLISH_SURNAME);
        }

        if (getLanguage().equals("es") || getSystemLanguage().equals("es") || complete) {
            for (int n = -1; ++n < 4; ) {
                permittedNames.add(NameEnum.SPANISH_NAME);
            }
            permittedNames.add(NameEnum.SPANISH_COMPOUND_NAME);
            permittedNames.add(NameEnum.SPANISH_FORENAME);
            permittedNames.add(NameEnum.SPANISH_GIVEN_NAME);
            permittedNames.add(NameEnum.SPANISH_SURNAME);
        }
        permittedNames.add(NameEnum.JAPANESE_NAME);
        permittedNames.add(NameEnum.MEXICAN_NAME);
        permittedNames.add(NameEnum.RUSSIAN_NAME);
        permittedNames.add(NameEnum.GENERATED_NATURAL_NAME);
        permittedNames.add(NameEnum.GENERATED_DEFINED_NAME);
        permittedNames.add(NameEnum.GENERATED_MYSTIC_NAME);

        for (int n = -1, length = SUPPORTED_LANGUAGES.length; ++n < length; ) {
            if (isLocaleAvailable(SUPPORTED_LANGUAGES[n], complete ? "" : networkCountry)) {
                NameEnum nameEnum = null;

                switch (SUPPORTED_LANGUAGES[n]) {
                    case "ar":
                        nameEnum = NameEnum.ARABIC_NAME;
                        break;
                    case "de":
                        nameEnum = NameEnum.GERMAN_NAME;
                        break;
                    case "fr":
                        nameEnum = NameEnum.FRENCH_NAME;
                        break;
                    case "hi":
                        nameEnum = NameEnum.INDIAN_NAME;
                        break;
                    case "it":
                        nameEnum = NameEnum.ITALIAN_NAME;
                        break;
                    case "pt":
                        nameEnum = NameEnum.PORTUGUESE_NAME;
                        break;
                    default:
                        break;
                }

                if (nameEnum != null) {
                    for (int m = -1; ++m < 3; ) {
                        permittedNames.add(nameEnum);
                    }
                }
            }
        }
        //permittedNames.add(NameEnum.SUPPORTED_NAME);
        return permittedNames;
    }

    public static int getTheme(Context context) {
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        switch (Integer.parseInt(defaultPreferences.getString("preference_theme", "0"))) {
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

    public boolean saveEnquiry(ArrayList<Enquiry> enquiryList, Enquiry enquiry) {
        boolean contained = false;

        for (int n = 0; n < enquiryList.size(); n++) {
            if (Arrays.equals(new Object[]{enquiryList.get(n).getName(), enquiryList.get(n).getSex(), enquiryList.get(n).getDay(), enquiryList.get(n).getMonth(), enquiryList.get(n).getYear()},
                    new Object[]{enquiry.getName(), enquiry.getSex(), enquiry.getDay(), enquiry.getMonth(), enquiry.getYear()}))
                contained = true;
        }

        if (!contained) {
            if (enquiryList.size() >= 100)
                enquiryList.remove(0);
            enquiryList.add(enquiry);
            Gson gson = new Gson();
            String json = gson.toJson(enquiryList);
            preferences.edit().putString("enquiryList", json).apply();
            return true;
        } else
            return false;
    }
}
