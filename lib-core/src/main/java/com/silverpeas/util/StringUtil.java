/**
 * Copyright (C) 2000 - 2013 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of the GPL, you may
 * redistribute this Program in connection with Free/Libre Open Source Software ("FLOSS")
 * applications as described in Silverpeas's FLOSS exception. You should have received a copy of the
 * text describing the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.silverpeas.util;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.silverpeas.util.i18n.I18NHelper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class StringUtil extends StringUtils {

  public static String newline = System.getProperty("line.separator");

  private static final char[] PUNCTUATION
      = new char[]{'&', '\"', '\'', '{', '(', '[', '-', '|', '`',
        '_', '\\', '^', '@', ')', ']', '=', '+', '}', '?', ',', '.', ';', '/', ':', '!', '§',
        '%', '*', '$', '£', '€', '©', '²', '°', '¤'};
  private static final String PATTERN_START = "{";
  private static final String PATTERN_END = "}";
  private static final String TRUNCATED_TEXT_SUFFIX = "...";
  private static final String EMAIL_PATTERN
      = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
  private static final String HOUR_PATTERN = "^([0-1]?[0-9]|2[0-4]):([0-5][0-9])(:[0-5][0-9])?$";

  public static boolean isDefined(String parameter) {
    return (parameter != null && !parameter.trim().isEmpty() && !"null".equalsIgnoreCase(parameter));
  }

  public static boolean isNotDefined(String parameter) {
    return !isDefined(parameter);
  }


  /**
   * <p>Returns either the passed in String, or if the String is
   * {@code not defined}, an empty String ("").</p>
   * <p/>
   * <pre>
   * StringUtil.defaultStringIfNotDefined(null)   = ""
   * StringUtil.defaultStringIfNotDefined("")     = ""
   * StringUtil.defaultStringIfNotDefined("    ") = ""
   * StringUtil.defaultStringIfNotDefined("bat")  = "bat"
   * </pre>
   * @param string the String to check, may be null, blank or filled by spaces
   * if the input is {@code not defined}, may be null, blank or filled by spaces
   * @return the passed in String, or the default if it was {@code null}
   * @see StringUtil#isNotDefined(String)
   * @see StringUtils#defaultString(String, String)
   */
  public static String defaultStringIfNotDefined(String string) {
    return defaultStringIfNotDefined(string, EMPTY);
  }


  /**
   * <p>Returns either the passed in String, or if the String is
   * {@code not defined}, the value of {@code defaultString}.</p>
   * <p/>
   * <pre>
   * StringUtil.defaultStringIfNotDefined(null, "NULL")   = "NULL"
   * StringUtil.defaultStringIfNotDefined("", "NULL")     = "NULL"
   * StringUtil.defaultStringIfNotDefined("    ", "NULL") = "NULL"
   * StringUtil.defaultStringIfNotDefined("bat", "NULL")  = "bat"
   * </pre>
   * @param string the String to check, may be null, blank or filled by spaces
   * @param defaultString the default String to return
   * if the input is {@code not defined}, may be null, blank or filled by spaces
   * @return the passed in String, or the default if it was {@code null}
   * @see StringUtil#isNotDefined(String)
   * @see StringUtils#defaultString(String, String)
   */
  public static String defaultStringIfNotDefined(String string, String defaultString) {
    return defaultString((isDefined(string) ? string : null), defaultString);
  }

  public static boolean isInteger(String id) {
    try {
      Integer.parseInt(id);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static boolean isLong(String id) {
    try {
      Long.parseLong(id);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static float convertFloat(String value) {
    if (StringUtil.isFloat(value)) {
      return Float.valueOf(value);
    } else if (value != null) {
      String charge = value.replace(',', '.');
      if (StringUtil.isFloat(charge)) {
        return Float.valueOf(charge);
      }
    }
    return 0f;
  }

  public static boolean isFloat(String id) {
    try {
      Float.parseFloat(id);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * @param text
   * @return a String with all quotes replaced by spaces
   */
  public static String escapeQuote(String text) {
    return text.replaceAll("'", " ");
  }

  /**
   * Replaces
   *
   * @param name
   * @return a String with all quotes replaced by spaces
   */
  public static String toAcceptableFilename(String name) {
    String fileName = name;
    fileName = fileName.replace('\\', '_');
    fileName = fileName.replace('/', '_');
    fileName = fileName.replace('$', '_');
    fileName = fileName.replace('%', '_');
    fileName = fileName.replace('?', '_');
    fileName = fileName.replace(':', '_');
    fileName = fileName.replace('*', '_');
    fileName = fileName.replace('"', '_');
    fileName = fileName.replace('<', '_');
    fileName = fileName.replace('>', '_');
    fileName = fileName.replace('|', '_');
    return fileName;
  }

  /**
   * Format a string by extending the principle of the the method format() of the class
   * java.text.MessageFormat to string arguments. For instance, the string '{key}' contained in the
   * original string to format will be replaced by the value corresponding to this key contained
   * into the values map.
   *
   * @param label The string to format
   * @param values The values to insert into the string
   * @return The formatted string, filled with values of the map.
   */
  public static String format(String label, Map<String, ?> values) {
    StringBuilder sb = new StringBuilder();
    int startIndex = label.indexOf(PATTERN_START);
    int endIndex;
    String patternKey;
    Object value;
    while (startIndex != -1) {
      endIndex = label.indexOf(PATTERN_END, startIndex);
      if (endIndex != -1) {
        patternKey = label.substring(startIndex + 1, endIndex);
        if (values.containsKey(patternKey)) {
          value = values.get(patternKey);
          sb.append(label.substring(0, startIndex)).append(
              value != null ? value.toString() : "");
        } else {
          sb.append(label.substring(0, endIndex + 1));
        }
        label = label.substring(endIndex + 1);
        startIndex = label.indexOf(PATTERN_START);
      } else {
        sb.append(label);
        label = "";
        startIndex = -1;
      }
    }
    sb.append(label);
    return sb.toString();
  }

  /**
   * @param text The string to truncate if its size is greater than the maximum length given as
   * parameter.
   * @param maxLength The maximum length required.
   * @return The truncated string followed by '...' if needed. Returns the string itself if its
   * length is smaller than the required maximum length.
   */
  public static String truncate(String text, int maxLength) {
    if (text == null || text.length() <= maxLength) {
      return text;
    } else if (maxLength <= 3) {
      return TRUNCATED_TEXT_SUFFIX;
    } else {
      return text.substring(0, maxLength - 3) + TRUNCATED_TEXT_SUFFIX;
    }
  }

  /**
   * Replace parts of a text by an one replacement string. The text to replace is specified by a
   * regex.
   * @param source the original text
   * @param regex the regex that permits to identify parts of text to replace
   * @param replacement the replacement text
   * @return The source text modified
   */
  public static String regexReplace(String source, String regex, String replacement) {
    if (StringUtil.isNotDefined(source) || StringUtil.isNotDefined(regex)) {
      return source;
    }
    return source.replaceAll(regex, replacement);
  }

  /**
   *
   * Validate the form of an email address. <P> Return <tt>true</tt> only if <ul> <li>
   * <tt>aEmailAddress</tt> can successfully construct an
   * {@link javax.mail.internet.InternetAddress} <li>when parsed with "
   *
   * @" as delimiter, <tt>aEmailAddress</tt> contains two tokens which satisfy
   * {@link hirondelle.web4j.util.Util#textHasContent}. </ul> <P> The second condition arises since
   * local email addresses, simply of the form "<tt>albert</tt>", for example, are valid for
   * {@link javax.mail.internet.InternetAddress}, but almost always undesired.
   *
   * @param aEmailAddress the address to be validated
   * @return true is the address is a valid email address - false otherwise.
   */
  public static boolean isValidEmailAddress(String aEmailAddress) {
    if (aEmailAddress == null) {
      return false;
    }
    boolean result;
    try {
      new InternetAddress(aEmailAddress);
      result = Pattern.matches(EMAIL_PATTERN, aEmailAddress);
    } catch (AddressException ex) {
      result = false;
    }
    return result;
  }

  public static boolean isValidHour(final String time) {
    if (!isDefined(time)) {
      return false;
    }
    return Pattern.matches(HOUR_PATTERN, time);
  }
  
  public static String convertToEncoding(String toConvert, String encoding) {
    try {
      return new String(toConvert.getBytes(Charset.defaultCharset()), encoding);
    } catch (UnsupportedEncodingException ex) {
      return toConvert;
    }
  }

  /**
   * Evaluate the expression and return true if expression equals "true", "yes", "y", "1" or "oui".
   *
   * @param expression the expression to be evaluated
   * @return true if expression equals "true", "yes", "y", "1" or "oui".
   */
  public static boolean getBooleanValue(final String expression) {
    return "true".equalsIgnoreCase(expression) || "yes".equalsIgnoreCase(expression)
        || "y".equalsIgnoreCase(expression) || "oui".equalsIgnoreCase(expression)
        || "1".equalsIgnoreCase(expression);
  }

  /**
   * Method for trying to detect encoding
   *
   * @param data some data to try to detect the encoding.
   * @param declaredEncoding expected encoding.
   * @return
   */
  public static String detectEncoding(byte[] data, String declaredEncoding) {
    CharsetDetector detector = new CharsetDetector();
    if (!StringUtil.isDefined(declaredEncoding)) {
      detector.setDeclaredEncoding(CharEncoding.ISO_8859_1);
    } else {
      detector.setDeclaredEncoding(declaredEncoding);
    }
    detector.setText(data);
    CharsetMatch detectedEnc = detector.detect();
    return detectedEnc.getName();
  }

  /**
   * Method for trying to detect encoding
   *
   * @param data some data to try to detect the encoding.
   * @param declaredEncoding expected encoding.
   * @return
   */
  public static String detectStringEncoding(byte[] data, String declaredEncoding) throws
      UnsupportedEncodingException {
    if (data != null) {
      String value = new String(data, declaredEncoding);
      if (!checkEncoding(value)) {
        Set<String> supportedEncodings;
        if (CharEncoding.UTF_8.equals(declaredEncoding)) {
          supportedEncodings = StringUtil.detectMaybeEncoding(data, CharEncoding.ISO_8859_1);
        } else {
          supportedEncodings = StringUtil.detectMaybeEncoding(data, CharEncoding.UTF_8);
        }
        return reencode(data, supportedEncodings, declaredEncoding);
      }
    }
    return declaredEncoding;
  }

  private static boolean checkEncoding(String value) {
    if (value != null) {
      char[] chars = value.toCharArray();
      for (char currentChar : chars) {
        if (!Character.isLetterOrDigit(currentChar) && !Character.isWhitespace(currentChar)
            && !ArrayUtil.contains(PUNCTUATION, currentChar)) {
          return false;
        }
      }
    }
    return true;

  }

  private static String reencode(byte[] data, Set<String> encodings, String declaredEncoding) throws
      UnsupportedEncodingException {
    if (!encodings.isEmpty()) {
      String encoding = encodings.iterator().next();
      String value = new String(data, encoding);
      if (!checkEncoding(value)) {
        encodings.remove(encoding);
        return reencode(data, encodings, declaredEncoding);
      }
      return encoding;
    }
    return declaredEncoding;
  }

  /**
   * Method for trying to detect encoding
   *
   * @param data some data to try to detect the encoding.
   * @param declaredEncoding expected encoding.
   * @return
   */
  public static Set<String> detectMaybeEncoding(byte[] data, String declaredEncoding) {
    CharsetDetector detector = new CharsetDetector();
    if (!StringUtil.isDefined(declaredEncoding)) {
      detector.setDeclaredEncoding(CharEncoding.ISO_8859_1);
    } else {
      detector.setDeclaredEncoding(declaredEncoding);
    }
    detector.setText(data);
    CharsetMatch[] detectedEnc = detector.detectAll();
    Set<String> encodings = new LinkedHashSet<String>(detectedEnc.length);
    for (CharsetMatch detectedEncoding : detectedEnc) {
      encodings.add(detectedEncoding.getName());
    }
    return encodings;
  }

  /**
   * Indicates if two Strings are equals, managing null.
   *
   * @param s1 the first String.
   * @param s2 the second String.
   * @return true ifthe two Strings are equals.
   */
  public static boolean areStringEquals(String s1, String s2) {
    if (s1 == null) {
      return s2 == null;
    }
    return s1.equals(s2);
  }

  /**
   * Parse a String into a float using the default locale.
   *
   * @param value the string to be parsed into a float.
   * @return the float value.
   * @throws ParseException
   */
  public static float floatValue(String value) throws ParseException {
    return floatValue(value, I18NHelper.defaultLanguage);
  }

  /**
   * Parse a String into a float using the specified locale.
   *
   * @param value the string to be parsed into a float
   * @param language the language for defining the locale
   * @return the float value.
   * @throws ParseException
   */
  public static float floatValue(String value, String language) throws ParseException {
    String lang = language;
    if (!StringUtil.isDefined(language)) {
      lang = I18NHelper.defaultLanguage;
    }
    NumberFormat numberFormat = NumberFormat.getInstance(new Locale(lang));
    return numberFormat.parse(value).floatValue();
  }

  /**
   * Encodes the specified binary data into a text of Base64 characters.
   *
   * @param binaryData the binary data to convert in Base64-based String.
   * @return a String representation of the binary data in Base64 characters.
   */
  public static String asBase64(byte[] binaryData) {
    return Base64.encodeBase64String(binaryData);
  }

  /**
   * Decodes the specified text with Base64 characters in binary.
   *
   * @param base64Text the text in Base64.
   * @return the binary representation of the text.
   */
  public static byte[] fromBase64(String base64Text) {
    return Base64.decodeBase64(base64Text);
  }

  /**
   * Encodes the specified binary data into a String representing the hexadecimal values of each
   * byte in order. The String is in the UTF-8 charset.
   *
   * @param binaryData the binary data to concert in hexadecimal-based String.
   * @return a String representation of the binary data in Hexadecimal characters.
   */
  public static String asHex(byte[] binaryData) {
    return Hex.encodeHexString(binaryData);
  }

  /**
   * Decodes the specified text with hexadecimal values in bytes of those same values. The text is
   * considered to be in the UTF-8 charset.
   *
   * @param hexText the text with hexadecimal-based characters.
   * @return the binary representation of the text.
   * @throws ParseException if an odd number or illegal of characters is supplied.
   */
  public static byte[] fromHex(String hexText) throws ParseException {
    try {
      return Hex.decodeHex(hexText.toCharArray());
    } catch (DecoderException ex) {
      throw new ParseException(ex.getMessage(), -1);
    }
  }

  /**
   * <p>Splits the provided text into an array, using whitespace as the separator. Whitespace is
   * defined by {@link Character#isWhitespace(char)}.</p>
   *
   * <p>The separator is not included in the returned String array. Adjacent separators are treated
   * as one separator. For more control over the split use the StrTokenizer class.</p>
   *
   * <p>A {@code null} input String returns {@code null}.</p>
   *
   * <pre>
   * StringUtils.split(null)       = null
   * StringUtils.split("")         = empty list
   * StringUtils.split("abc def")  = ["abc", "def"]
   * StringUtils.split("abc  def") = ["abc", "def"]
   * StringUtils.split(" abc ")    = ["abc"]
   * </pre>
   *
   * @param str the String to parse, may be null
   * @return an array of parsed Strings, {@code null} if null String input
   */
  public static Iterable<String> splitString(String str) {
    return Arrays.asList(split(str));
  }

  /**
   * <p>Splits the provided text into an array, separator specified. This is an alternative to using
   * StringTokenizer.</p>
   *
   * <p>The separator is not included in the returned String array. Adjacent separators are treated
   * as one separator. For more control over the split use the StrTokenizer class.</p>
   *
   * <p>A {@code null} input String returns {@code null}.</p>
   *
   * <pre>
   * StringUtils.split(null, *)         = null
   * StringUtils.split("", *)           = empty list
   * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
   * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
   * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
   * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
   * </pre>
   *
   * @param str the String to parse, may be null
   * @param separatorChar the character used as the delimiter
   * @return an array of parsed Strings, {@code null} if null String input
   */
  public static Iterable<String> splitString(String str, char separatorChar) {
    return Arrays.asList(split(str, separatorChar));
  }

  private StringUtil() {
  }
}
