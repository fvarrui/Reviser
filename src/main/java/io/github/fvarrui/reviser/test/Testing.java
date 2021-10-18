package io.github.fvarrui.reviser.test;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.maven.shared.invoker.MavenInvocationException;

import io.github.fvarrui.reviser.ui.App;
import io.github.fvarrui.reviser.utils.FileUtils;
import io.github.fvarrui.reviser.utils.GitUtils;
import io.github.fvarrui.reviser.utils.MavenUtils;

public class Testing {
	
	public static void testAll(String input, File submissionsDir) throws Exception {
		
		App.console.println("--- Ejecutando entrega: " + submissionsDir.getName());
		try {
			
			for (File submissionDir : Arrays.asList(submissionsDir.listFiles())) {
				test(input, submissionDir);
			}
			
		} catch (MavenInvocationException e) {
			
			if (e.getMessage().equals("compile failed")) throw new Exception("Fallo al compilar el proyecto (mvn compile). Revisa la versión de Java en las properties del pom.xml.");
			if (e.getMessage().equals("exec:java failed")) throw new Exception("Fallo al ejecutar el proyecto (mvn exec:java). Revisa la propiedad 'exec.mainClass' del fichero pom.xml.");
			throw e;
			
		}
		
	}

	private static void test(String input, File submittedFile) throws Exception {

		App.console.println("--- Ejecutando: " + submittedFile.getName());
		
		// busca el proyecto maven de forma recursiva
		File pomFile = FileUtils.find(submittedFile, "pom.xml");
		
		// se trata de un proyecto Maven
		if (pomFile != null) {
			
			testMaven(input, submittedFile, pomFile);
			
		} else {

			open(submittedFile);
			
		}
		
		App.console.println("--- Test completado");

	}

	private static void testMaven(String input, File submittedFile, File pomFile) throws Exception {
		File mavenProject = pomFile.getParentFile();

		App.console.println("--- Encontrado fichero pom.xml en el proyecto. Se trata de un repositorio Maven: " + mavenProject.getName());
		
		if (Arrays.asList(submittedFile.list()).contains(".git")) {
			App.console.println("--- Haciendo pull al repositorio GIT en " + submittedFile.getName());
			GitUtils.pull(submittedFile);
		}
		
		App.console.println("--- Compilando y ejecutando con Maven: " + mavenProject.getName());
		MavenUtils.compileAndExec(mavenProject, input);
		
	}

	private static void open(File file) throws IOException {
		App.console.println("--- Abriendo " + file.getName() + " ...");
		Desktop.getDesktop().open(file);
	}

}
