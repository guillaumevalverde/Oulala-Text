package com.foxycode.testapp.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * This class provides utilised methods regarding with String class.
 * 
 * @author <a href="mailto:kikyoung.kwon@gmail.com">Kikyoung Kwon</a>
 */
public class XString {

	private static final String PARSE_START_CHAR = "{";
	private static final String PARSE_END_CHAR = "}";	

	/**
	 * This method checks Whether the string value is null or not.
	 * 
	 * @param value
	 *            String value to check.
	 * @return Whether the string value is null or not.
	 */
	public static boolean isNull(String value) {
		if (value == null || value.equals("") || value.equals("null"))
			return true;
		else
			return false;
	}
	
	/**
	 * This method removes any '"' in string
	 * 
	 * @param value
	 *            String value to check.
	 * @return the string value without '"'
	 */
	public static String parseStringForPrefixAndEnd(String value, String stringToCompare) {
		if (value.startsWith(stringToCompare)) {
			value = value.substring(stringToCompare.length());
		}
		if (value.endsWith(stringToCompare)) {
			value = value.substring(0, value.length() - stringToCompare.length());
		}
		return value;
	} // parseStringForPrefixAndEnd

	public static String parseString(String content, Map<String, String> replaceList) {
		Iterator<Entry<String, String>> i = replaceList.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, String> curEntry = i.next();
			String key = curEntry.getKey();
			String value = curEntry.getValue();
			String replacement = PARSE_START_CHAR + key + PARSE_END_CHAR;
			content = content.replace(replacement, value);
		}
		return content;
	} // parseString

    /**
     * Convert an Input Stream to a String for test purpose.
     * Note: The Input Stream can not be used further after reading by this method.
     * @param is
     * @return
     * @throws java.io.IOException
     */
    public static String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
} 