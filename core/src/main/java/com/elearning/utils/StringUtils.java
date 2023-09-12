package com.elearning.utils;


import lombok.experimental.ExtensionMethod;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@ExtensionMethod(Extensions.class)
public class StringUtils {
    private static final char[] CHARS_LOW = "zxcvbnmasdfghjklqwertyuiop".toCharArray();
    private static final char[] CHARS_UP = "ZXCVBNMASDFGHJKLQWERTYUIOP".toCharArray();
    private static final char[] CHARS_NUM = "0123456789".toCharArray();
    private static char[] SPECIAL_CHARACTERS = new char[]{' ', '̀', '̣', '̃', '́', '~', '`', '!', '@', '#', '%', '^', '&', '*', '(', ')', '+', '=', '{', '}', '[', ']', '|', '\\', ':', ';', '"', '\'', '"', '<', '>', ',', '.', '?', '/', 'á', 'à', 'ả', 'ã', 'ạ', 'Á', 'À', 'Ả', 'Ã', 'Ạ', 'ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', 'Ă', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ', 'â', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', 'Â', 'Ấ', 'Ẩ', 'Ẩ', 'Ẫ', 'Ậ', 'đ', 'Đ', 'é', 'è', 'ẻ', 'ẽ', 'ẹ', 'É', 'È', 'Ẻ', 'Ẽ', 'Ẹ', 'ê', 'ế', 'ề', 'ể', 'ễ', 'ệ', 'Ê', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ', 'ý', 'ỳ', 'ỷ', 'ỹ', 'ỵ', 'Ý', 'Ỳ', 'Ỷ', 'Ỹ', 'Ỵ', 'ú', 'ù', 'ủ', 'ũ', 'ụ', 'Ú', 'Ù', 'Ủ', 'Ũ', 'Ụ', 'ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự', 'Ư', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự', 'í', 'ì', 'ỉ', 'ĩ', 'ị', 'Í', 'Ì', 'Ỉ', 'Ĩ', 'Ị', 'ó', 'ò', 'ỏ', 'õ', 'ọ', 'Ó', 'Ò', 'Ỏ', 'Õ', 'Ọ', 'ô', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', 'Ô', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ', 'ơ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ', 'Ơ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ'};
    private static char[] DIACRITICAL_CHARACTERS = new char[]{'á', 'à', 'ả', 'ã', 'ạ', 'Á', 'À', 'Ả', 'Ã', 'Ạ', 'ă', 'ắ', 'ằ', 'ẳ', 'ẵ', 'ặ', 'Ă', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ', 'Ặ', 'â', 'ấ', 'ầ', 'ẩ', 'ẫ', 'ậ', 'Â', 'Ấ', 'Ẩ', 'Ẩ', 'Ẫ', 'Ậ', 'đ', 'Đ', 'é', 'è', 'ẻ', 'ẽ', 'ẹ', 'É', 'È', 'Ẻ', 'Ẽ', 'Ẹ', 'ê', 'ế', 'ề', 'ể', 'ễ', 'ệ', 'Ê', 'Ế', 'Ề', 'Ể', 'Ễ', 'Ệ', 'ý', 'ỳ', 'ỷ', 'ỹ', 'ỵ', 'Ý', 'Ỳ', 'Ỷ', 'Ỹ', 'Ỵ', 'ú', 'ù', 'ủ', 'ũ', 'ụ', 'Ú', 'Ù', 'Ủ', 'Ũ', 'Ụ', 'ư', 'ứ', 'ừ', 'ử', 'ữ', 'ự', 'Ư', 'Ứ', 'Ừ', 'Ử', 'Ữ', 'Ự', 'í', 'ì', 'ỉ', 'ĩ', 'ị', 'Í', 'Ì', 'Ỉ', 'Ĩ', 'Ị', 'ó', 'ò', 'ỏ', 'õ', 'ọ', 'Ó', 'Ò', 'Ỏ', 'Õ', 'Ọ', 'ô', 'ố', 'ồ', 'ổ', 'ỗ', 'ộ', 'Ô', 'Ố', 'Ồ', 'Ổ', 'Ỗ', 'Ộ', 'ơ', 'ớ', 'ờ', 'ở', 'ỡ', 'ợ', 'Ơ', 'Ớ', 'Ờ', 'Ở', 'Ỡ', 'Ợ'};

    public StringUtils() {
    }

    public static String replaceChar(String str) {
        if (null != str) {
            for(int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (ch != '_' && (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') && !Character.isDigit(ch) && Character.isSpaceChar(ch)) {
                    str = str.replace(ch, '_');
                }
            }
        }

        return str;
    }

    public static boolean isStringNumber(String value) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static String removeSpecialCharacter(String string, String replaceString) {
        return string.trim().replaceAll("[^\\p{L}\\p{Nd}]", replaceString);
    }

    public static String removeDotZero(double val) {
        DecimalFormat dfRemoveDotZero = new DecimalFormat("###.#");
        return dfRemoveDotZero.format(Math.abs(val));
    }

    public static String formatCurrency(double value) {
        DecimalFormat df = new DecimalFormat("###,###.###");
        return df.format(value);
    }

    public static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "0";
        } else {
            DecimalFormat df = new DecimalFormat("###,###.###");
            return df.format(value);
        }
    }

    public static String formatCurrency2(double value) {
        DecimalFormat df = new DecimalFormat("###,###.###");
        return df.format(value).replace(".", ",");
    }

    public static String formatCurrency2(BigDecimal value) {
        DecimalFormat df = new DecimalFormat("###,###.###");
        return df.format(value).replace(".", ",");
    }

    public static String randomNumber(int n) {
        Random ran = new Random();

        StringBuilder result;
        for(result = new StringBuilder(); n > 0; --n) {
            int x = ran.nextInt(9);
            result.append(x);
        }

        return result.toString();
    }

    public static String stripAccents(String str) {
        if (null == str || str.length() == 0) return "";
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        str = pattern.matcher(temp).replaceAll("")
                .replaceAll("Đ", "D")
                .replace("đ", "d");
        str = str.toLowerCase();
        str = URLEncoder.encode(str, StandardCharsets.UTF_8);
        str = str.replace("+", " ");
        return str.trim();
    }

    public static String randomString(int countChar) {
        return randomString(countChar, new ArrayList(Arrays.asList(CHARS_LOW, CHARS_UP, CHARS_NUM)));
    }

    private static String randomString(int countChar, List<char[]> array) {
        Random randomGenerator = new Random();
        List<Integer> list = new ArrayList();
        int count = array.size();

        do {
            int randomInt = randomGenerator.nextInt(count);
            boolean flag = true;
            if (list.size() < count) {
                Iterator var7 = list.iterator();

                while(var7.hasNext()) {
                    Integer i = (Integer)var7.next();
                    if (i == randomInt) {
                        flag = false;
                        break;
                    }
                }
            }

            if (flag) {
                if (list.size() == 0) {
                    randomInt = 0;
                }

                list.add(randomInt);
            }
        } while(list.size() < countChar);

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        Iterator var15 = list.iterator();

        while(var15.hasNext()) {
            Integer i = (Integer)var15.next();
            char[] chars = (char[])array.get(i);
            int stop = 0;

            char c;
            do {
                c = chars[random.nextInt(chars.length)];
                ++stop;
            } while(sb.toString().contains(String.valueOf(c)) && stop > countChar);

            sb.append(c);
        }

        return sb.toString();
    }

    public static String getSlug(String s) {
        s = s.trim();
        s = StringUtils.removeSpecialCharacter(s, " ");
        s = UnicodeUtils.unicode2NoSignOriginalToLowerCase(s);
        String[] arr = s.split(" ");
        StringBuilder result = new StringBuilder();
        for (String item : arr) {
            if (!item.isBlankOrNull()) {
                if (result.length() > 0) {
                    result.append("-");
                }
                result.append(item.trim());
            }
        }
        return result.toString();
    }

    public static boolean checkExistCharacterSpecial(String str) {
        StringBuilder sb = new StringBuilder(str);

        for(int i = 0; i < sb.length(); ++i) {
            char[] var3 = SPECIAL_CHARACTERS;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                char c = var3[var5];
                if (c == sb.charAt(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isContainDiacritical(String str) {
        StringBuilder sb = new StringBuilder(str);

        for(int i = 0; i < sb.length(); ++i) {
            char[] var3 = DIACRITICAL_CHARACTERS;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                char c = var3[var5];
                if (c == sb.charAt(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String toTitleCase(String givenString) {
        if (null != givenString && givenString.length() != 0) {
            String[] arr = givenString.split(" ");
            StringBuilder sb = new StringBuilder();
            String[] var3 = arr;
            int var4 = arr.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String s = var3[var5];
                s = s.trim();
                if (s.length() > 0) {
                    sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
                }
            }

            return sb.toString().trim();
        } else {
            return null;
        }
    }
    public static String removeApostrophe(String value) {
        return !isBlankOrNull(value) ? value.replaceAll("’", "").replaceAll("‘", "").replaceAll("'", "").replaceAll(":", "").replaceAll("`", "").trim() : "";
    }

    public static List<String> removeCommaSeparated(String str) {
        return (List)(isBlankOrNull(str) ? new ArrayList() : (List)Stream.of(str.split(",")).collect(Collectors.toList()));
    }

    public static String getDeliveryId(String deliveryIdItem) {
        if (!isBlankOrNull(deliveryIdItem)) {
            int index = deliveryIdItem.lastIndexOf("-");
            return deliveryIdItem.substring(0, index);
        } else {
            return "";
        }
    }

    public static boolean compareString(String val1, String val2) throws Exception {
        val1 = val1.trim().toLowerCase();
        val1 = URLEncoder.encode(val1, "UTF-8");
        val2 = val2.trim().toLowerCase();
        val2 = URLEncoder.encode(val2, "UTF-8");
        return val1.equalsIgnoreCase(val2);
    }

    public static boolean isBlankOrNull(String text) {
        return null == text || text.isBlank();
    }

    public static boolean isNullOrEmptyString(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$");
    }

    public static boolean isPhoneVietnamValid(String phone) {
        return phone.matches("^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$");
    }

    public static boolean isIdentityNumberVietnamValid(String identityNumber) {
        return (identityNumber.length() == 9 || identityNumber.length() == 12) && isStringNumber(identityNumber);
    }
}