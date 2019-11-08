package dad.test.projectrunner.utils;

import java.io.File;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class MavenUtils {

	public static void runGoal(File projectDir, String goal) throws MavenInvocationException {
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(projectDir, "pom.xml"));
		request.setGoals(Collections.singletonList(goal));
		Invoker invoker = new DefaultInvoker();
		invoker.setOutputHandler(line -> System.out.println(line));
		invoker.setErrorHandler(line -> System.err.println(line));
		if (invoker.execute(request).getExitCode() != 0) {
			throw new MavenInvocationException(goal + " failed");
		}
	}

	public static void compile(File projectDir) throws MavenInvocationException {
		System.out.println("Compiling " + projectDir);
		runGoal(projectDir, "compile");
	}

	public static void exec(File projectDir) throws MavenInvocationException {
		System.out.println("Executing " + projectDir);
		runGoal(projectDir, "exec:java");
	}
	
}
