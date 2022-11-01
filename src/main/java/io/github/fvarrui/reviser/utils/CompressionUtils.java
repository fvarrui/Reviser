package io.github.fvarrui.reviser.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FilenameUtils;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class CompressionUtils {
	
	private static final List<String> COMPRESSED_FORMATS = Arrays.asList("zip", "7z", "rar");

	public static boolean isCompressedFile(File file) {
		return COMPRESSED_FORMATS.contains(FilenameUtils.getExtension(file.getName().toLowerCase()));
	}

	public static File decompress(File compressedFile, File destination) throws IOException {
		destination = new File(destination, FilenameUtils.getBaseName(compressedFile.getName()));
		extract(compressedFile, destination);
		return destination;
	}
	
	public static File decompress(File compressedFile, boolean createParentFolder) throws IOException {
		File destination = createParentFolder ? new File(compressedFile.getParentFile(), FilenameUtils.getBaseName(compressedFile.getName())) : compressedFile.getParentFile();
		extract(compressedFile, destination);
		return destination;
	}
	
	public static File decompress(File compressedFile) throws IOException {
		return decompress(compressedFile, false);
	}

	private static void extract(File source, File destination) throws IOException, IllegalArgumentException {
		String ext = FilenameUtils.getExtension(source.getName());
		switch (ext) {
		case "7z": unzip7z(source, destination); break;
		case "rar": unrar(source, destination); break;
		case "zip": unzip(source, destination); break;
		default: throw new IllegalArgumentException("Unrecognized source file compression format (" + ext + "): " + source);  
		}
	}

	private static void unrar(File source, File destination) throws IOException {
		org.apache.commons.io.FileUtils.forceMkdir(destination);
		try {
			Junrar.extract(source, destination);
		} catch (RarException e) {
			throw new IOException(e);
		}
	}

	private static void unzip7z(File source, File destination) throws IOException {
        SevenZFile sevenZFile = new SevenZFile(source);
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null){
            if (entry.isDirectory()){
                continue;
            }
            File curfile = new File(destination, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(curfile);
            byte[] content = new byte[(int) entry.getSize()];
            sevenZFile.read(content, 0, content.length);
            out.write(content);
            out.close();
        }
        sevenZFile.close();
	}

	private static void unzip(File source, File destination) throws IOException {
		try {
			ZipFile zipFile = new ZipFile(source);
			zipFile.extractAll(destination.getAbsolutePath());
			zipFile.close();
		} catch (ZipException e) {
			throw new IOException(e);
		}
	}

}
