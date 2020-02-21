package fvarrui.reviser.utils;

import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.rightPad;

public class StringUtils {
	
	public static String adjust(String str, int maxWidth) {
		if (str.length() > maxWidth)
			return abbreviate(str, maxWidth);
		else
			return rightPad(str, maxWidth);
	}

}
