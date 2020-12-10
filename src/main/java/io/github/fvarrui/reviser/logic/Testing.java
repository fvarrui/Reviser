package io.github.fvarrui.reviser.logic;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import io.github.fvarrui.reviser.ui.App;
import io.github.fvarrui.reviser.utils.FileUtils;
import io.github.fvarrui.reviser.utils.GitUtils;
import io.github.fvarrui.reviser.utils.MavenUtils;
import io.github.fvarrui.reviser.utils.URLUtils;
import io.github.fvarrui.reviser.utils.ZipUtils;

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

			// comprueba si la entrega es una URL 
			String url = URLUtils.extractUrl(submittedFile);
			if (url != null) { 
			
				App.console.println("--- URL extraída de la entrega: " + url);
				
				// comprueba si la URL es un repo de GitHub
				if (url.startsWith("https://github.com")) {
					
					// si es un repo de github, lo clona
					
					App.console.println("--- La entrega es un repositorio de GitHub");
					App.console.println("--- Clonando repositorio GIT desde " + url + " en " + submittedFile.getParentFile() + "...");
					
					try {				
						
						File originalFile = submittedFile;
						
						// clone git repo
						submittedFile = GitUtils.clone(url, submittedFile.getParentFile());
						
						// elimina el fichero del envío
						originalFile.delete();
						
					} catch (Exception e) {
						App.console.println("Error cloning project: " + e.getMessage());
						throw e;
					}			
					
				} else {

					App.console.println("--- Abriendo URL en el navegador ...");
					
					// si no es un repo, abre la URL en el navegador predeterminado
					Desktop.getDesktop().browse(URLUtils.toURL(url).toURI());
					
				}
				
			}
						
		}
		
		// busca el proyecto maven de forma recursiva
		File pomFile = FileUtils.find(submittedFile, "pom.xml");
		
		// se trata de un proyecto Maven
		if (pomFile != null) {
			
			File mavenProject = pomFile.getParentFile();

			App.console.println("--- Encontrado fichero pom.xml en el proyecto. Se trata de un repositorio Maven: " + mavenProject.getName());
			
			if (Arrays.asList(submittedFile.list()).contains(".git")) {
				App.console.println("--- Haciendo pull al repositorio GIT en " + submittedFile.getName());
				GitUtils.pull(submittedFile);
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
