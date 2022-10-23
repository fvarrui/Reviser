package io.github.fvarrui.reviser.utils;

import java.io.ByteArrayInputStream;
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

import io.github.fvarrui.reviser.config.Config;
import io.github.fvarrui.reviser.ui.Reviser;

public class MavenUtils {
	
	/**
	 * Runs specified goal in a Maven project 
	 * @param projectDir Maven project directory
	 * @param goal Goal to execute
	 * @param in Input  
	 * @throws MavenInvocationException
	 */
	public static void runGoal(File projectDir, String goal, InputStream in) throws MavenInvocationException {
		
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(projectDir, "pom.xml"));
		request.setBaseDirectory(projectDir);
		request.setGoals(Collections.singletonList(goal));

		InvocationOutputHandler handler = line -> {
			Reviser.console.println(line);
		};
		
		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(Config.getConfig().getMavenHome()));
		if (in != null) invoker.setInputStream(in);
		invoker.setOutputHandler(handler);
		invoker.setErrorHandler(handler);
		if (invoker.execute(request).getExitCode() != 0) {
			throw new MavenInvocationException(goal + " failed");
		}
		
	}

	/**
	 * Runs "mvn compile"
	 * @param projectDir Maven project directory
	 * @throws MavenInvocationException
	 */
	public static void compile(File projectDir) throws MavenInvocationException {
		runGoal(projectDir, "compile", new NullInputStream(0));
	}

	/**
	 * Runs "mvn exec:java"
	 * @param projectDir Maven project directory
	 * @throws MavenInvocationException
	 */
	public static void exec(File projectDir) throws MavenInvocationException {
		exec(projectDir, null);
	}

	/**
	 * Runs "mvn exec:java"
	 * @param projectDir Maven project directory
	 * @param in Input
	 * @throws MavenInvocationException
	 */
	public static void exec(File projectDir, InputStream in) throws MavenInvocationException {
		runGoal(projectDir, "exec:java", in != null ? in : new NullInputStream(0));
	}
	
	/**
	 * Runs "mvn compile exec:java"
	 * @param projectDir Maven project directory
	 * @param input Input
	 * @throws Exception
	 */
	public static void compileAndExec(File projectDir, String input) throws Exception {
		
		// compile project
		compile(projectDir);

		// execute project
		InputStream in = input != null ? new ByteArrayInputStream(input.getBytes()) : null;
		exec(projectDir, in);
		
	}
	
}
