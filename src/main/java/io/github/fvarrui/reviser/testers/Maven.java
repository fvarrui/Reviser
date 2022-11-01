package io.github.fvarrui.reviser.testers;

import static io.github.fvarrui.reviser.utils.FileUtils.findFile;

import java.io.File;

import org.apache.maven.shared.invoker.MavenInvocationException;

import io.github.fvarrui.reviser.ui.Reviser;
import io.github.fvarrui.reviser.utils.MavenUtils;

public class Maven extends Tester {

	public void test(File submissionDir) throws Exception {

		try {
			
			File pomFile = findFile(submissionDir, "pom.xml");
			File mavenProject = pomFile.getParentFile();
			
			Reviser.console.println("Compilando y ejecutando el proyecto" + mavenProject.getName());
						
			MavenUtils.compileAndExec(mavenProject, "");
			
			Reviser.console.println("¡Completado!");			

		} catch (MavenInvocationException e) {

			Reviser.console.println("Ha ocurrido un error: " + e.getMessage());			
			
			if (e.getMessage().equals("compile failed")) { 
				throw new Exception("Fallo al compilar el proyecto (mvn compile). Revisa la versión de Java en las properties del pom.xml.");
			}
			
			if (e.getMessage().equals("exec:java failed")) {
				throw new Exception("Fallo al ejecutar el proyecto (mvn exec:java). Revisa la propiedad 'exec.mainClass' del fichero pom.xml.");
			}
			
			throw e;

		}

	}

	@Override
	public boolean matches(File submissionDir) {
		return findFile(submissionDir, "pom.xml") != null;
	}

}
