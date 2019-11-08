package dad.test.projectrunner.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;

public class GitUtils {

	public static File clone(String uri) throws Exception {
		System.out.println("Cloning repository " + uri + "...");
		String name = URLUtils.getFile(uri);
		File outputDir = new File(System.getProperty("java.io.tmpdir"), name);
		if (outputDir.exists()) {
			FileUtils.deleteDirectory(outputDir);
		}
		Git repo = Git.cloneRepository()
			.setURI(uri)
			.setDirectory(outputDir)
			.call();
		System.out.println("Repository cloned to " + outputDir);
		repo.getRepository().close();
		return outputDir;
	}

}
