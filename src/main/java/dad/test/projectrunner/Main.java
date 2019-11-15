package dad.test.projectrunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.MavenInvocationException;

import dad.test.projectrunner.utils.GitUtils;
import dad.test.projectrunner.utils.GsonUtils;
import dad.test.projectrunner.utils.MavenUtils;
import dad.test.projectrunner.utils.URLUtils;

public class Main {
	
	public static File configFile = new File("assets/GsonSample/config.json");
	
	public static void main(String [] args) throws Exception {
 
		Config config = GsonUtils.loadFromJson(configFile, Config.class); 
		
		runProjects(config);
		
	}
	
	private static void runProjects(Config config) {
		File submissionsDir = new File(config.getSubmissionsDir());
		
		System.out.println(submissionsDir.getAbsolutePath());
		
		List<File> submissions = Arrays.asList(submissionsDir.listFiles());
		submissions.stream().forEach(f -> {
			String student = f.getName().split("_")[0];
			String repoUrl = extractUrl(new File(f, "onlinetext.html"));
			File input = config.getInput() != null ? new File(configFile.getParentFile(), config.getInput()) : null;
			File output = new File(configFile.getParentFile(), config.getOutput());
			test(student, repoUrl, input, output);
		});
		
	}
	
	private static String extractUrl(File submittedFile) {
		try {
			String content = FileUtils.readFileToString(submittedFile, Charset.defaultCharset());
			return URLUtils.findUrl(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;		
	}

	private static void test(String name, String repoUrl, File input, File output) {
		
		System.out.println("--------------------------------------");
		System.out.println("Student: " + name);
		System.out.println();
		
		// clone git repo
		File projectDir = null;
		try {
			projectDir = GitUtils.clone(repoUrl);
		} catch (Exception e) {
			System.out.println("Error cloning project: " + e.getMessage());
			return;
		}

		// compile project
		try {
			MavenUtils.compile(projectDir);
		} catch (MavenInvocationException e) {
			System.out.println("Error compiling project: " + e.getMessage());
			return;
		}

		// execute project
		try { 
			InputStream in = input != null ? new FileInputStream(input) : null;
			MavenUtils.exec(projectDir, in);
		} catch (MavenInvocationException e) {
			System.out.println("Error executing project: " + e.getMessage());
			return;
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		
	}

}
