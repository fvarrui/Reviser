package fvarrui.batchtesting.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.io.input.NullInputStream;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class MavenUtils {

	public static void runGoal(File projectDir, String goal, InputStream in) throws MavenInvocationException {
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(projectDir, "pom.xml"));
		request.setGoals(Collections.singletonList(goal));

		InvocationOutputHandler handler = line -> {
			if (line.startsWith("[INFO]") || line.startsWith("[WARNING]") || line.startsWith("[WARN]")) return;
			System.out.println(line);
		};
		
		Invoker invoker = new DefaultInvoker();
		if (in != null) invoker.setInputStream(in);
		invoker.setOutputHandler(handler);
		invoker.setErrorHandler(handler);
		if (invoker.execute(request).getExitCode() != 0) {
			throw new MavenInvocationException(goal + " failed");
		}
		
	}

	public static void compile(File projectDir) throws MavenInvocationException {
		runGoal(projectDir, "compile", new NullInputStream(0));
	}

	public static void exec(File projectDir) throws MavenInvocationException {
		exec(projectDir, null);
	}

	public static void exec(File projectDir, InputStream in) throws MavenInvocationException {
		runGoal(projectDir, "exec:java", in != null ? in : new NullInputStream(0));
	}
	
}
