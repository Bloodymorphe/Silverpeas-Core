package com.stratelia.silverpeas.authentication;

import com.stratelia.silverpeas.silvertrace.SilverTrace;

public class AuthenticationEncrypt implements EncryptionInterface {
  public AuthenticationEncrypt() {
  }

  /**
   * Simple encode for cookie value
   */
  public String encode(String stringToEncode) {
    SilverTrace.info("authentication", "AuthenticationEncrypt.encode()",
        "root.MSG_PARAM_ENTER_METHOD", "stringToEncode=" + stringToEncode);
    StringBuffer hashString = new StringBuffer();
    char[] uniqueKey = stringToEncode.toCharArray();
    for (int i = 0; i < uniqueKey.length; ++i) {
      String carInt = new Integer(uniqueKey[i] * 3).toString();
      String lg = new Integer(carInt.length()).toString();
      hashString.append(lg + carInt);
    }
    hashString.reverse();
    SilverTrace.info("authentication", "AuthenticationEncrypt.encode()",
        "root.MSG_PARAM_EXIT_METHOD", "encodedString=" + hashString.toString());
    return hashString.toString();
  }

  /**
   * Simple decode for cookie value
   * 
   * @param key
   *          : la chaine � d�coder
   * 
   */
  public String decode(String encodedText) {
    SilverTrace.info("authentication", "AuthenticationEncrypt.decode()",
        "root.MSG_PARAM_ENTER_METHOD", "encodedText=" + encodedText);
    int lg = 0;
    int pos = 0;
    String reverseEncodedText = new StringBuffer(encodedText).reverse()
        .toString();
    StringBuffer hashString = new StringBuffer();
    for (int i = 0; i + pos < reverseEncodedText.length(); i++) {
      lg = new Integer(reverseEncodedText.substring(i + pos, i + pos + 1))
          .intValue();
      String car = reverseEncodedText.substring(i + pos + 1, i + pos + 1 + lg);
      pos = pos + lg;
      hashString.append((char) (new Integer(car).intValue() / 3));
    }
    SilverTrace.info("authentication", "AuthenticationEncrypt.decode()",
        "root.MSG_PARAM_EXIT_METHOD", "decodedString=" + hashString.toString());
    return hashString.toString();
  }

  /**
   * XOR Decrypt for authent param values
   * 
   * @param str
   *          : String to decode
   * @param key
   *          : key for decoding
   * @param extraCrypt
   *          : add simple encode (see decode(String))
   */
  public String decode(String str, String key, boolean extraCrypt) {
    // SilverTrace.info("authentication", "AuthenticationEncrypt.decode1()",
    // "root.MSG_PARAM_ENTER_VALUE", "str � d�coder="+str+" cl�="+key);
    String decStr = "";
    String prand = "";
    String asciiChar_string = "";
    for (int i = 0; i < key.length(); i++) {
      int asciiCode = key.charAt(i);
      asciiChar_string += asciiCode;
    }
    prand = asciiChar_string;

    int sPos = new Double(Math.floor(prand.length() / 5)).intValue();
    StringBuffer stringMult = new StringBuffer();
    stringMult.append(prand.charAt(sPos)).append(prand.charAt(sPos * 2))
        .append(prand.charAt(sPos * 3)).append(prand.charAt(sPos * 4)).append(
            prand.charAt(sPos * 5));

    int mult = new Integer(stringMult.toString()).intValue();

    int incr = Math.round(key.length() / 2);
    double modu = Math.pow(2, 127) - 1;
    int salt = Integer.parseInt(str.substring(str.length() - 8, str.length()),
        16);

    str = str.substring(0, str.length() - 8);
    prand += salt;
    double prandInt = new Double(prand).doubleValue();
    prandInt = (mult * prandInt + incr) % modu;

    int dec_chrInt;

    StringBuffer hashString = new StringBuffer();
    for (int i = 0; i < str.length(); i += 2) {
      dec_chrInt = Integer.parseInt(str.substring(i, i + 2), 16)
          ^ new Double(Math.floor((prandInt / modu) * 255)).intValue();
      hashString.append((char) dec_chrInt);
      prandInt = (mult * prandInt + incr) % modu;
    }
    decStr = hashString.toString();
    String decStrFinal = decStr;
    if (extraCrypt)
      decStrFinal = decode(decStr);
    return decStrFinal;
  }
}