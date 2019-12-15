package fvarrui.batchtesting.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;

public class GitUtils {

	public static void pull(File repoDir) throws Exception {
		
		Git repo = Git.open(repoDir);
		repo.pull();
		repo.getRepository().close();
		
	}
	
	public static File clone(String uri, File outputDir) throws Exception {
		
		String name = URLUtils.getFile(uri);
		outputDir = new File(outputDir, name);
		if (outputDir.exists()) {
			FileUtils.deleteDirectory(outputDir);
		}
		Git repo = Git.cloneRepository()
			.setURI(uri)
			.setDirectory(outputDir)
			.call();
		repo.getRepository().close();
		return outputDir;
		
	}
	
	public static File clone(String uri) throws Exception {
		File outputDir = new File(System.getProperty("java.io.tmpdir"));
		return clone(uri, outputDir);
	}

}
