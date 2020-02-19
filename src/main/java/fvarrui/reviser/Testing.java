package fvarrui.reviser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.reviser.ui.App;
import fvarrui.reviser.utils.FileUtils;
import fvarrui.reviser.utils.GitUtils;
import fvarrui.reviser.utils.MavenUtils;
import fvarrui.reviser.utils.URLUtils;
import fvarrui.reviser.utils.ZipUtils;

public class Testing {
	
	private static final List<String> COMPRESSED_FORMATS = Arrays.asList("zip", "7z", "rar");
	
	public static void run(String input, File submissionsDir) throws IOException, CommandLineException, Exception {
		
		App.console.println("Ejecutando entrega: " + submissionsDir.getName());
		
		for (File file : submissionsDir.listFiles()) {
			test(input, file);
		};
	}

	private static void test(String input, File submittedFile) throws Exception, IOException, CommandLineException {

		App.console.println("--- Procesando fichero de la entrega: " + submittedFile.getName());
		
		String extension = FilenameUtils.getExtension(submittedFile.getName());
	
		// el envío es un fichero comprimido
		if (COMPRESSED_FORMATS.contains(extension)) {
			
			App.console.println("--- El fichero está comprimido: " + extension);			
			
			App.console.println("--- Descomprimiendo " + submittedFile.getName());
			
			// descomprime el fichero 
			ZipUtils.uncompress(submittedFile, false);

			// obtiene el fichero extraído
			File parentFile = submittedFile.getParentFile();
			
			// elimina el original y se queda con el extraído
			submittedFile.delete();
			submittedFile = parentFile.listFiles()[0];

			App.console.println("--- Fichero extraído: " + submittedFile.getName());
			
		}
		
		// el envío es texto en línea
		if (submittedFile.getName().equals("onlinetext.html")) {
			
			App.console.println("--- El envío es texto en línea: " + submittedFile.getName());
			
			String repoUrl = URLUtils.extractUrl(submittedFile);

			App.console.println("--- URL extraída de la entrega: " + repoUrl);
			
			submittedFile.delete();
			
			// clone git repo
			App.console.println("--- Clonando repositorio GIT desde " + repoUrl + " en " + submittedFile.getParentFile());
			try {				
				submittedFile = GitUtils.clone(repoUrl, submittedFile.getParentFile());
			} catch (Exception e) {
				System.out.println("Error cloning project: " + e.getMessage());
				throw e;
			}
			
		}
		
		// busca el proyecto maven de forma recursiva
		File pomFile = FileUtils.find(submittedFile, "pom.xml");
		
		// se trata de un proyecto Maven
		if (pomFile != null) {
			
			File mavenProject = pomFile.getParentFile();

			App.console.println("--- Encontrado fichero pom.xml en el proyecto. Se trata de un repositorio Maven: " + mavenProject.getName());
			
			if (Arrays.asList(mavenProject.list()).contains(".git")) {
				App.console.println("--- Haciendo pull al repositorio GIT en " + mavenProject.getName());
				GitUtils.pull(mavenProject);
			}
			
			App.console.println("--- Compilando y ejecutando con Maven: " + mavenProject.getName());
			MavenUtils.compileAndExec(mavenProject, input);
			
		} else {

			Arrays.asList(submittedFile.getParentFile().listFiles()).stream().forEach(f -> {
				try {
					App.console.println("--- Abriendo: " + f.getName());
					Desktop.getDesktop().open(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
		}
		
		App.console.println("--- Test completado\n");

	}
	
}
