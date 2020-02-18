package fvarrui.reviser.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
	
	/**
	 * Finds a file by name recursively
	 * @param file Base search directory 
	 * @param name Searched filename
	 * @return
	 */
	public static File find(File file, String name) {
		if (file.isFile() && file.getName().equals(name)) {
			return file;
		} else if (file.isDirectory()) {
			File [] list = file.listFiles();
			if (list == null) return null;
			List<File> files = Arrays.asList(list);
			for (File subfile : files) {
				File found = find(subfile, name);
				if (found != null) return found;
			}
		}
		return null;
		
	}

}
