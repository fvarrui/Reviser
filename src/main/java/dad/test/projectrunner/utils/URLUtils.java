package dad.test.projectrunner.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {
	
	private static final Pattern URL_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

	public static String getFile(String uri) {
		String [] fragments = uri.split("/");
		return fragments[fragments.length - 1];		
	}
	
	public static String findUrl(String text) {
		Matcher m = URL_PATTERN.matcher(text);
		if (m.find()) {
			return m.group();
		}
		return null;
	}

}
