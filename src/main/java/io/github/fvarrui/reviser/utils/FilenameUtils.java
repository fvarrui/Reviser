package io.github.fvarrui.reviser.utils;

import java.io.File;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class FilenameUtils {
	
	public static boolean equalExtensions(File file1, File file2) {
		String ext1 = getExtension(file1.getName());
		String ext2 = getExtension(file2.getName());
		return ext1.equalsIgnoreCase(ext2);
	}

}
