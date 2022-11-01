package io.github.fvarrui.reviser.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FileUtils {
	
	/**
	 * Finds a file by name recursively
	 * @param file Base search directory 
	 * @param name Searched filename
	 * @return
	 */
	public static File findFile(File file, String name) {
		return findFile(file, f -> f.getName().equals(name));
	}
	
	public static File findFile(File file, Predicate<File> predicate) {
		if (predicate.test(file)) {
			return file;
		} else if (file.isDirectory()) {
			File [] list = file.listFiles();
			if (list == null) return null;
			List<File> files = Arrays.asList(list);
			for (File subfile : files) {
				File found = findFile(subfile, predicate);
				if (found != null) return found;
			}
		}
		return null;
	}

	public static File rename(File file, String newName) {
		File newFile = new File(file.getParent(), newName); 
		file.renameTo(newFile);
		return newFile;
	}

}
