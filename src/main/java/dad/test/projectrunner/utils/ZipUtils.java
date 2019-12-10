package dad.test.projectrunner.utils;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ZipUtils {
	
	public static File unzip(File zipFile, boolean createParentFolder) throws ZipException {
		File destination = createParentFolder ? new File(zipFile.getParentFile(), FilenameUtils.getBaseName(zipFile.getName())) : zipFile.getParentFile();
		if (!destination.exists()) {
			ZipFile zip = new ZipFile(zipFile);
			zip.extractAll(destination.getAbsolutePath());
			System.out.println(zipFile.getAbsolutePath() + " file unzipped on " + destination.getAbsolutePath());
		} else {
			System.out.println("Unzip " + zipFile.getAbsolutePath() + " skipped!");
		}
		return destination;
	}

}
