package io.github.fvarrui.reviser.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

public class ZipUtils {
	
	private static String extract(File source, File destination) throws IOException, CommandLineException {
		return CommandUtils.execute("7z", "x", "-aoa", "-o" + destination.getAbsolutePath(), source);		
	}

	public static File uncompress(File compressedFile, File destination) throws IOException, CommandLineException {
		destination = new File(destination, FilenameUtils.getBaseName(compressedFile.getName()));
		extract(compressedFile, destination);
		return destination;
	}
	
	public static File uncompress(File compressedFile, boolean createParentFolder) throws IOException, CommandLineException {
		File destination = createParentFolder ? new File(compressedFile.getParentFile(), FilenameUtils.getBaseName(compressedFile.getName())) : compressedFile.getParentFile();
		extract(compressedFile, destination);
		return destination;
	}
	
	public static File uncompress(File compressedFile) throws IOException, CommandLineException {
		return uncompress(compressedFile, false);
	}
	
}
