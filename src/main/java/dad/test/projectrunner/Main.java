package dad.test.projectrunner;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.MavenInvocationException;

import dad.test.projectrunner.utils.GitUtils;
import dad.test.projectrunner.utils.MavenUtils;
import dad.test.projectrunner.utils.URLUtils;

public class Main {
	
	public static final File submissionsDir = new File("D:\\Users\\fvarrui\\Downloads\\DAD-GsonSample-90960");

	public static void main(String [] args) throws Exception {

		List<File> submissions = Arrays.asList(submissionsDir.listFiles());
		submissions.stream().forEach(f -> {
			String student = f.getName().split("_")[0];
			String url = extractUrl(new File(f, "onlinetext.html"));
			test(student, url);
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

	private static void test(String name, String uri) {
		
		System.out.println("--------------------------------------");
		System.out.println("Student: " + name);
		System.out.println();
		
		// clone git repo
		File projectDir = null;
		try {
			projectDir = GitUtils.clone(uri);
		} catch (Exception e) {
			System.err.println("Error cloning project");
			e.printStackTrace();
			return;
		}

		// compile project
		try {
			MavenUtils.compile(projectDir);
		} catch (MavenInvocationException e) {
			System.err.println("Error compiling project");
			e.printStackTrace();
			return;
		}

		// execute project
		try { 
			MavenUtils.exec(projectDir);
		} catch (MavenInvocationException e) {
			System.err.println("Error executing project");
			e.printStackTrace();
			return;
		}
		
		
	}

}
