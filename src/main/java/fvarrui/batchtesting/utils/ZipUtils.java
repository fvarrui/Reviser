package fvarrui.batchtesting.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

public class ZipUtils {

	public static File uncompress(File compressedFile, boolean createParentFolder) throws IOException, CommandLineException {
		File destination = createParentFolder ? new File(compressedFile.getParentFile(), FilenameUtils.getBaseName(compressedFile.getName())) : compressedFile.getParentFile();
		if (!destination.exists()) {
			CommandUtils.execute("7z", "x", "-aoa", "-o" + destination.getAbsolutePath(), compressedFile);
		}
		return destination;
	}
	
	public static File uncompress(File compressedFile) throws IOException, CommandLineException {
		return uncompress(compressedFile, false);
	}
	
}
