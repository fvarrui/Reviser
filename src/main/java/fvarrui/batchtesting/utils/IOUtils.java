package fvarrui.batchtesting.utils;

import java.util.Scanner;

public class IOUtils {
	
	private static final Scanner scanner = new Scanner(System.in);
	
	public static String readString(String text, Object oldValue) {
		System.out.print(text + (oldValue == null ? "" : " (" + oldValue + ")") + ": ");
		String value = scanner.nextLine();
		if (value.isEmpty()) return "" + (oldValue == null ? "" : oldValue);
		return value;
	}

	public static int readInteger(String text, Object oldValue) {
		try { 
			return Integer.parseInt(readString(text, oldValue));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
