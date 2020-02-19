package fvarrui.reviser.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;

public class GitUtils {

	/**
	 * Runs "git pull" on a specified repo
	 * @param repoDir Repo project directory
	 * @throws Exception
	 */
	public static void pull(File repoDir) throws Exception {
				
		Git repo = Git.open(repoDir);
		repo.pull();
		repo.getRepository().close();
		
	}
	
	/**
	 * Runs "git clone" on output directory
	 * @param uri URI to clone
	 * @param outputDir Directory where repository is going to be cloned
	 * @return File pointing to cloned repo
	 * @throws Exception
	 */
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
	
	/**
	 * Runs "git clone" on temporary directory
	 * @param uri URI to clone
	 * @return File pointing to cloned repo on temp dir 
	 * @throws Exception
	 */
	public static File clone(String uri) throws Exception {
		File outputDir = new File(System.getProperty("java.io.tmpdir"));
		return clone(uri, outputDir);
	}

}
